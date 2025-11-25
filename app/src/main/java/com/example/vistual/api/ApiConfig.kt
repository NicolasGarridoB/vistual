package com.example.vistual.api

/**
 * Objeto de configuración para el API de XANO
 * 
 * INSTRUCCIONES PARA CONFIGURAR XANO:
 * 1. Ve a XANO.com y crea tu backend usando el archivo XANO_PROMPT.md
 * 2. Una vez creado, copia la URL base de tu API de XANO
 * 3. Pega la URL en la constante BASE_URL abajo
 * 4. Asegúrate de que la URL termine con "/"
 * 
 * Ejemplo de URL de XANO:
 * "https://x8ki-letl-twmt.n7.xano.io/api:Ab12Cd34/"
 */
object ApiConfig {
    
    /**
     * URL base del API de XANO para endpoints generales (Vistual API)
     * Incluye: prendas, outfits, etc.
     */
    const val BASE_URL = "https://x8ki-letl-twmt.n7.xano.io/api:G1UzV9hT/"
    
    /**
     * URL base del API de XANO para autenticación (Authentication)
     * Incluye: login, signup, me, etc.
     */
    const val AUTH_BASE_URL = "https://x8ki-letl-twmt.n7.xano.io/api:bOk8Zi6W/"
    
    /**
     * Timeout para conexiones HTTP (en segundos)
     */
    const val CONNECT_TIMEOUT = 30L
    
    /**
     * Timeout para lectura de respuestas (en segundos)
     */
    const val READ_TIMEOUT = 30L
    
    /**
     * Timeout para escritura de peticiones (en segundos)
     */
    const val WRITE_TIMEOUT = 30L
    
    /**
     * Habilitar logs de HTTP para debugging
     * Cambiar a false en producción
     */
    const val ENABLE_LOGGING = true
}
