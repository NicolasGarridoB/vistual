package com.example.vistual.viewmodel

import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import com.example.vistual.model.CategoriaPrenda
import com.example.vistual.model.ColorPrenda
import com.example.vistual.model.Prenda
import java.util.UUID

// --- Data Classes para la nueva función ---

// Usuario simplificado para la comunidad
data class CommunityUser(
    val name: String,
    // En el futuro: val avatarUrl: String
)

// Representa un outfit compartido en la comunidad
data class SharedOutfit(
    val id: String = UUID.randomUUID().toString(),
    val user: CommunityUser,
    val outfitName: String,
    val prendas: List<Prenda>,
    var likes: Int,
    var isLikedByUser: Boolean = false
)

// --- ViewModel ---

class CommunityViewModel : ViewModel() {

    private var _sharedOutfits = createFakeData().toMutableStateList()
    val sharedOutfits: List<SharedOutfit> get() = _sharedOutfits

    /**
     * Añade un outfit del usuario actual a la lista de la comunidad.
     */
    fun shareOutfit(outfitName: String, prendas: List<Prenda>, userName: String) {
        val newSharedOutfit = SharedOutfit(
            user = CommunityUser(name = userName),
            outfitName = if (outfitName.isNotBlank()) outfitName else "Mi Nuevo Outfit",
            prendas = prendas,
            likes = 0,
            isLikedByUser = false
        )
        // Añadir al principio de la lista
        _sharedOutfits.add(0, newSharedOutfit)
    }

    /**
     * Incrementa/decrementa los likes de un outfit.
     */
    fun toggleLike(outfitId: String) {
        val index = _sharedOutfits.indexOfFirst { it.id == outfitId }
        if (index != -1) {
            val outfit = _sharedOutfits[index]
            val updatedOutfit = outfit.copy(
                likes = if (outfit.isLikedByUser) outfit.likes - 1 else outfit.likes + 1,
                isLikedByUser = !outfit.isLikedByUser
            )
            _sharedOutfits[index] = updatedOutfit
        }
    }

    /**
     * Crea datos ficticios para poblar el foro inicialmente.
     */
    private fun createFakeData(): List<SharedOutfit> {
        // Ruta especial para referenciar recursos drawable
        val drawableUriPrefix = "android.resource://com.example.vistual/drawable/"

        val fakeUser1 = CommunityUser("StyleStar_Sarah")
        val fakePrenda1 = Prenda(id = 9001, nombre = "Sudadera Negra", imagenPath = "${drawableUriPrefix}sudadera_negra", categoria = CategoriaPrenda.PARTE_SUPERIOR, color = ColorPrenda.NEGRO, usuarioId = -1)
        val fakePrenda2 = Prenda(id = 9002, nombre = "Vaqueros Oscuros", imagenPath = "${drawableUriPrefix}vaqueros_oscuros", categoria = CategoriaPrenda.PARTE_INFERIOR, color = ColorPrenda.NEGRO, usuarioId = -1)
        val fakePrenda3 = Prenda(id = 9003, nombre = "Zapatillas Blancas", imagenPath = "${drawableUriPrefix}zapatillas_blancas", categoria = CategoriaPrenda.ZAPATOS, color = ColorPrenda.BLANCO, usuarioId = -1)

        val fakeUser2 = CommunityUser("UrbanExplorer")
        val fakePrenda4 = Prenda(id = 9004, nombre = "Camiseta Blanca", imagenPath = "${drawableUriPrefix}camiseta_blanca", categoria = CategoriaPrenda.PARTE_SUPERIOR, color = ColorPrenda.BLANCO, usuarioId = -1)
        val fakePrenda5 = Prenda(id = 9005, nombre = "Pantalón Cargo", imagenPath = "${drawableUriPrefix}pantalon_cargo", categoria = CategoriaPrenda.PARTE_INFERIOR, color = ColorPrenda.GRIS, usuarioId = -1)
        val fakePrenda6 = Prenda(id = 9006, nombre = "Botas Negras", imagenPath = "${drawableUriPrefix}botas_negras", categoria = CategoriaPrenda.ZAPATOS, color = ColorPrenda.NEGRO, usuarioId = -1)

        return listOf(
            SharedOutfit(
                user = fakeUser1,
                outfitName = "Vibes de fin de semana",
                prendas = listOf(fakePrenda1, fakePrenda2, fakePrenda3),
                likes = 142
            ),
            SharedOutfit(
                user = fakeUser2,
                outfitName = "Look Urbano",
                prendas = listOf(fakePrenda4, fakePrenda5, fakePrenda6),
                likes = 98
            )
        )
    }
}
