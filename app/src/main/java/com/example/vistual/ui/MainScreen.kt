package com.example.vistual.ui

import android.content.Context
import android.graphics.BitmapFactory
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.vistual.model.CategoriaPrenda
import com.example.vistual.model.Prenda
import com.example.vistual.viewmodel.MainViewModel
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    mainViewModel: MainViewModel,
    usuarioEmail: String,
    onAddPrenda: () -> Unit,
    onLogout: () -> Unit,
    onNavigateToSavedOutfits: () -> Unit,
    onNavigateToCarouselOutfit: () -> Unit = {},
    onNavigateToCommunity: () -> Unit // Nuevo callback
) {
    val prendasState by mainViewModel.prendasState
    val categoriaSeleccionada by mainViewModel.categoriaSeleccionada
    var modoSeleccion by remember { mutableStateOf(false) }
    var prendasSeleccionadas by remember { mutableStateOf<Set<Int>>(emptySet()) }

    Scaffold(
        topBar = {
            MainTopAppBar(
                modoSeleccion = modoSeleccion,
                onCancelSelection = {
                    modoSeleccion = false
                    prendasSeleccionadas = emptySet()
                },
                onSaveOutfit = { /* Lógica para guardar se añadirá aquí */ },
                onNavigateToSavedOutfits = onNavigateToSavedOutfits,
                onNavigateToCarouselOutfit = onNavigateToCarouselOutfit,
                onNavigateToCommunity = onNavigateToCommunity, // Pasar el callback
                onLogout = onLogout,
                numSeleccionadas = prendasSeleccionadas.size
            )
        },
        floatingActionButton = {
            if (!modoSeleccion) {
                FloatingActionButton(onClick = onAddPrenda) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Agregar prenda"
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Filtro de categorías horizontal
            if (!modoSeleccion && prendasState.prendas.isNotEmpty()) {
                CategoryFilter(
                    categoriaSeleccionada = categoriaSeleccionada,
                    onCategoriaSeleccionada = { mainViewModel.seleccionarCategoria(it) }
                )
            }
            
            when {
                prendasState.isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                prendasState.errorMessage != null -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Error: ${prendasState.errorMessage}")
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(onClick = { mainViewModel.cargarPrendas() }) {
                                Text("Reintentar")
                            }
                        }
                    }
                }
                prendasState.prendas.isEmpty() -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Checkroom,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = if (categoriaSeleccionada != null) {
                                    "No tienes prendas en ${categoriaSeleccionada?.displayName}"
                                } else {
                                    "No tienes prendas aún"
                                },
                                style = MaterialTheme.typography.titleMedium,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "¡Agrega tu primera prenda!",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                else -> {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        items(prendasState.prendas, key = { it.id }) { prenda ->
                            PrendaCard(
                                prenda = prenda,
                                isSelected = prenda.id in prendasSeleccionadas,
                                onClick = {
                                    if (modoSeleccion) {
                                        prendasSeleccionadas = if (prenda.id in prendasSeleccionadas) {
                                            prendasSeleccionadas - prenda.id
                                        } else {
                                            prendasSeleccionadas + prenda.id
                                        }
                                        if (prendasSeleccionadas.isEmpty()) {
                                            modoSeleccion = false
                                        }
                                    }
                                },
                                onLongClick = {
                                    if (!modoSeleccion) {
                                        modoSeleccion = true
                                        prendasSeleccionadas = prendasSeleccionadas + prenda.id
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryFilter(
    categoriaSeleccionada: CategoriaPrenda?,
    onCategoriaSeleccionada: (CategoriaPrenda?) -> Unit
) {
    val scrollState = rememberScrollState()
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Chip "Todas"
        FilterChip(
            selected = categoriaSeleccionada == null,
            onClick = { onCategoriaSeleccionada(null) },
            label = { Text("Todas") },
            leadingIcon = if (categoriaSeleccionada == null) {
                {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = "Seleccionada",
                        modifier = Modifier.size(18.dp)
                    )
                }
            } else null
        )
        
        // Chips de categorías
        CategoriaPrenda.values().forEach { categoria ->
            FilterChip(
                selected = categoriaSeleccionada == categoria,
                onClick = { onCategoriaSeleccionada(categoria) },
                label = { Text(categoria.displayName) },
                leadingIcon = if (categoriaSeleccionada == categoria) {
                    {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = "Seleccionada",
                            modifier = Modifier.size(18.dp)
                        )
                    }
                } else null
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainTopAppBar(
    modoSeleccion: Boolean,
    numSeleccionadas: Int,
    onCancelSelection: () -> Unit,
    onSaveOutfit: () -> Unit,
    onNavigateToSavedOutfits: () -> Unit,
    onNavigateToCarouselOutfit: () -> Unit,
    onNavigateToCommunity: () -> Unit, // Nuevo callback
    onLogout: () -> Unit
) {
    TopAppBar(
        title = {
            if (modoSeleccion) {
                Text("$numSeleccionadas seleccionadas")
            } else {
                Text("Closet Virtual")
            }
        },
        navigationIcon = {
            if (modoSeleccion) {
                IconButton(onClick = onCancelSelection) {
                    Icon(Icons.Filled.Close, contentDescription = "Cancelar")
                }
            }
        },
        actions = {
            if (modoSeleccion) {
                IconButton(onClick = onSaveOutfit, enabled = numSeleccionadas > 0) {
                    Icon(Icons.Filled.Save, contentDescription = "Guardar Outfit")
                }
            } else {
                IconButton(onClick = onNavigateToCarouselOutfit) {
                    Icon(Icons.Filled.ViewCarousel, contentDescription = "Crear Outfit")
                }
                // Nuevo botón de Comunidad
                IconButton(onClick = onNavigateToCommunity) {
                    Icon(Icons.Filled.Groups, contentDescription = "Comunidad")
                }
                IconButton(onClick = onNavigateToSavedOutfits) {
                    Icon(Icons.Filled.Checkroom, contentDescription = "Outfits Guardados")
                }
                IconButton(onClick = onLogout) {
                    Icon(Icons.Filled.ExitToApp, contentDescription = "Cerrar Sesión")
                }
            }
        }
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PrendaCard(
    prenda: Prenda,
    isSelected: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    val context = LocalContext.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick,
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple()
            ),
        shape = RoundedCornerShape(16.dp),
        border = if (isSelected) BorderStroke(3.dp, MaterialTheme.colorScheme.primary) else null
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            val imageFile = File(prenda.imagenPath)
            if (imageFile.exists()) {
                val bitmap = BitmapFactory.decodeFile(prenda.imagenPath)
                bitmap?.let {
                    Image(
                        bitmap = it.asImageBitmap(),
                        contentDescription = prenda.nombre,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                contentAlignment = Alignment.BottomStart
            ) {
                Text(
                    text = prenda.nombre,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold, color = Color.White),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.CheckCircle,
                        contentDescription = "Seleccionada",
                        tint = Color.White,
                        modifier = Modifier.size(48.dp)
                    )
                }
            }
        }
    }
}
