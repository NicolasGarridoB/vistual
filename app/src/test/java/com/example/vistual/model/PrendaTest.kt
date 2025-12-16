package com.example.vistual.model

import org.junit.Test
import org.junit.Assert.*

/**
 * Prueba unitaria para la clase Prenda
 */
class PrendaTest {
    
    @Test
    fun prenda_creacionCorrecta_verificaCampos() {
        // Arrange: Preparar los datos de prueba
        val nombre = "Camiseta Azul"
        val categoria = CategoriaPrenda.PARTE_SUPERIOR
        val color = ColorPrenda.AZUL
        val imagenPath = "/test/path/imagen.jpg"
        val usuarioId = 1
        
        // Act: Crear una instancia de Prenda
        val prenda = Prenda(
            id = 1,
            nombre = nombre,
            categoria = categoria,
            color = color,
            imagenPath = imagenPath,
            usuarioId = usuarioId
        )
        
        // Assert: Verificar que los campos se asignaron correctamente
        assertEquals(1, prenda.id)
        assertEquals(nombre, prenda.nombre)
        assertEquals(categoria, prenda.categoria)
        assertEquals(color, prenda.color)
        assertEquals(imagenPath, prenda.imagenPath)
        assertEquals(usuarioId, prenda.usuarioId)
        assertNotNull(prenda.fechaCreacion)
    }
}
