package com.example.vistual.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.vistual.model.Prenda
import com.example.vistual.model.SeccionOutfit
import com.example.vistual.viewmodel.CommunityViewModel
import com.example.vistual.viewmodel.SharedOutfit
import java.io.File

/**
 * Pantalla para la sección de la comunidad, que muestra un feed de outfits compartidos.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityScreen(
    communityViewModel: CommunityViewModel,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Comunidad") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            items(communityViewModel.sharedOutfits, key = { it.id }) {
                SharedOutfitCard(
                    outfit = it,
                    onLikeClicked = { communityViewModel.toggleLike(it.id) }
                )
            }
        }
    }
}

@Composable
fun SharedOutfitCard(
    outfit: SharedOutfit,
    onLikeClicked: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Encabezado con nombre de usuario
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                // Placeholder para el avatar del usuario
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer)
                )
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(outfit.user.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text(outfit.outfitName, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            // Visualización del outfit en columna
            OutfitDisplay(outfit.prendas)

            // Pie de la tarjeta con el botón de like
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 12.dp)
            ) {
                IconButton(onClick = onLikeClicked) {
                    Icon(
                        imageVector = if (outfit.isLikedByUser) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = "Like",
                        tint = if (outfit.isLikedByUser) Color.Red else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text("${outfit.likes} Me gusta", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
fun OutfitDisplay(prendas: List<Prenda>) {
    val parteSuperior = prendas.find { it.categoria.seccion == SeccionOutfit.PARTE_SUPERIOR }
    val parteInferior = prendas.find { it.categoria.seccion == SeccionOutfit.PARTE_INFERIOR }
    val zapatos = prendas.find { it.categoria.seccion == SeccionOutfit.ZAPATOS }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp) // Espacio entre tarjetas
    ) {
        // Parte Superior
        OutfitPartImage(prenda = parteSuperior)

        // Parte Inferior
        OutfitPartImage(prenda = parteInferior)

        // Zapatos
        OutfitPartImage(prenda = zapatos)
    }
}

@Composable
private fun OutfitPartImage(prenda: Prenda?, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .fillMaxWidth(0.85f)
            .height(130.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (prenda != null) {
                val imageModel = if (prenda.imagenPath.startsWith("android.resource://")) {
                    prenda.imagenPath // Es un recurso drawable
                } else {
                    File(prenda.imagenPath) // Es un archivo local
                }

                Image(
                    painter = rememberAsyncImagePainter(imageModel),
                    contentDescription = prenda.nombre,
                    modifier = Modifier.fillMaxSize().padding(8.dp),
                    contentScale = ContentScale.Fit
                )
            } else {
                // Placeholder si no hay prenda
                Text(
                    text = "Prenda no disponible",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                )
            }
        }
    }
}
