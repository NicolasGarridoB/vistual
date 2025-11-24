package com.example.vistual.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.vistual.model.Prenda
import com.example.vistual.model.SeccionOutfit
import com.example.vistual.viewmodel.MainViewModel
import com.example.vistual.viewmodel.OutfitViewModel
import java.io.File

/**
 * Pantalla de carrusel para crear outfits
 * Muestra las prendas organizadas por secciones horizontales
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarouselOutfitScreen(
    mainViewModel: MainViewModel = viewModel(),
    outfitViewModel: OutfitViewModel = viewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToAddPrenda: () -> Unit
) {
    val prendasState by mainViewModel.prendasState
    val prendas = prendasState.prendas
    
    // Estado para las prendas seleccionadas por sección
    var prendasSeleccionadas by remember { mutableStateOf<Map<SeccionOutfit, Prenda?>>(
        mapOf(
            SeccionOutfit.PARTE_SUPERIOR to null,
            SeccionOutfit.PARTE_INFERIOR to null,
            SeccionOutfit.ZAPATOS to null
        )
    ) }
    
    // Estado para el diálogo de guardar outfit
    var mostrarDialogoGuardar by remember { mutableStateOf(false) }
    var nombreOutfit by remember { mutableStateOf("") }
    
    // Verificar si todas las categorías están seleccionadas
    val todasSeleccionadas = prendasSeleccionadas.values.all { it != null }
    
    // Agrupar prendas por sección
    val prendasPorSeccion = remember(prendas) {
        prendas.groupBy { it.categoria.seccion }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Crear Outfit") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToAddPrenda) {
                        Icon(Icons.Default.Add, contentDescription = "Agregar prenda")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Scrollable content con las secciones
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                // Iterar por cada sección en orden
                SeccionOutfit.values().forEach { seccion ->
                    item {
                        SeccionCarrusel(
                            seccion = seccion,
                            prendas = prendasPorSeccion[seccion] ?: emptyList(),
                            prendaSeleccionada = prendasSeleccionadas[seccion],
                            onPrendaClick = { prenda ->
                                prendasSeleccionadas = prendasSeleccionadas.toMutableMap().apply {
                                    // Si la prenda ya está seleccionada, deseleccionarla
                                    if (this[seccion]?.id == prenda.id) {
                                        this[seccion] = null
                                    } else {
                                        this[seccion] = prenda
                                    }
                                }
                            }
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
            
            // Botón para guardar outfit
            Button(
                onClick = {
                    mostrarDialogoGuardar = true
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                enabled = todasSeleccionadas
            ) {
                Icon(Icons.Default.Check, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(if (todasSeleccionadas) "Guardar Outfit" else "Selecciona todas las categorías")
            }
        }
    }
    
    // Diálogo para guardar outfit
    if (mostrarDialogoGuardar) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoGuardar = false },
            title = { Text("Guardar Outfit") },
            text = {
                Column {
                    Text("Dale un nombre a tu outfit")
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = nombreOutfit,
                        onValueChange = { nombreOutfit = it },
                        label = { Text("Nombre del outfit") },
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (nombreOutfit.isNotBlank()) {
                            val prendasIds = prendasSeleccionadas.values
                                .filterNotNull()
                                .map { it.id }
                            outfitViewModel.saveOutfit(nombreOutfit, prendasIds)
                            mostrarDialogoGuardar = false
                            nombreOutfit = ""
                            // Limpiar selección
                            prendasSeleccionadas = mapOf(
                                SeccionOutfit.PARTE_SUPERIOR to null,
                                SeccionOutfit.PARTE_INFERIOR to null,
                                SeccionOutfit.ZAPATOS to null
                            )
                            onNavigateBack()
                        }
                    },
                    enabled = nombreOutfit.isNotBlank()
                ) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                TextButton(onClick = { 
                    mostrarDialogoGuardar = false
                    nombreOutfit = ""
                }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

/**
 * Componente de una sección con carrusel horizontal de prendas
 */
@Composable
private fun SeccionCarrusel(
    seccion: SeccionOutfit,
    prendas: List<Prenda>,
    prendaSeleccionada: Prenda?,
    onPrendaClick: (Prenda) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Título de la sección
        Text(
            text = seccion.displayName,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
        
        // Carrusel horizontal de prendas
        if (prendas.isEmpty()) {
            Text(
                text = "No tienes prendas en esta sección",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        } else {
            // Crear lista "infinita" repitiendo las prendas muchas veces
            val prendasInfinitas = remember(prendas) {
                if (prendas.size < 10) {
                    // Si hay pocas prendas, repetirlas para crear efecto infinito
                    List(100) { index -> prendas[index % prendas.size] }
                } else {
                    prendas
                }
            }
            
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(prendasInfinitas.size) { index ->
                    val prenda = prendasInfinitas[index]
                    PrendaCarouselCard(
                        prenda = prenda,
                        isSelected = prendaSeleccionada?.id == prenda.id,
                        onClick = { onPrendaClick(prenda) }
                    )
                }
            }
        }
    }
}

/**
 * Card de prenda para el carrusel
 */
@Composable
private fun PrendaCarouselCard(
    prenda: Prenda,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val borderColor = if (isSelected) {
        MaterialTheme.colorScheme.primary
    } else {
        Color.Transparent
    }
    
    Column(
        modifier = Modifier
            .width(120.dp)
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Imagen de la prenda
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(RoundedCornerShape(12.dp))
                .border(
                    width = if (isSelected) 3.dp else 1.dp,
                    color = if (isSelected) borderColor else MaterialTheme.colorScheme.outline,
                    shape = RoundedCornerShape(12.dp)
                )
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            if (prenda.imagenPath.isNotEmpty()) {
                Image(
                    painter = rememberAsyncImagePainter(File(prenda.imagenPath)),
                    contentDescription = prenda.nombre,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
            
            // Indicador de selección
            if (isSelected) {
                Surface(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(24.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primary
                ) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = "Seleccionado",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.padding(4.dp)
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Nombre de la prenda
        Text(
            text = prenda.nombre,
            fontSize = 14.sp,
            maxLines = 1,
            modifier = Modifier.padding(horizontal = 4.dp)
        )
    }
}
