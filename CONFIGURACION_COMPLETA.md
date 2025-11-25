# ✅ Configuración Final XANO - Vistual

## 🎉 ¡Todo Configurado!

Tu aplicación Android está ahora completamente configurada para conectarse con tu backend de XANO.

---

## 📡 URLs Configuradas

### 1. Authentication API (Autenticación)
```
https://x8ki-letl-twmt.n7.xano.io/api:bOk8Zi6W/
```
**Endpoints disponibles:**
- ✅ `POST auth/signup` - Registro de usuarios
- ✅ `POST auth/login` - Login de usuarios  
- ✅ `GET auth/me` - Obtener usuario actual

### 2. Vistual API (Prendas y Outfits)
```
https://x8ki-letl-twmt.n7.xano.io/api:G1UzV9hT/
```
**Endpoints disponibles:**

**Prendas:**
- ✅ `GET prenda` - Listar todas las prendas
- ✅ `GET prenda/{prenda_id}` - Obtener prenda por ID
- ✅ `POST prenda` - Crear nueva prenda
- ✅ `PATCH prenda/{prenda_id}` - Editar prenda
- ✅ `DELETE prenda/{prenda_id}` - Eliminar prenda

**Outfits:**
- ✅ `GET outfit` - Listar todos los outfits
- ✅ `GET outfit/{outfit_id}` - Obtener outfit por ID
- ✅ `POST outfit` - Crear nuevo outfit
- ✅ `PATCH outfit/{outfit_id}` - Editar outfit
- ✅ `DELETE outfit/{outfit_id}` - Eliminar outfit

---

## 🔧 Archivos Actualizados

### 1. `ApiConfig.kt`
```kotlin
const val BASE_URL = "https://x8ki-letl-twmt.n7.xano.io/api:G1UzV9hT/"
const val AUTH_BASE_URL = "https://x8ki-letl-twmt.n7.xano.io/api:bOk8Zi6W/"
```

### 2. `AuthApiService.kt` (NUEVO)
Maneja todos los endpoints de autenticación:
- signup, login, me

### 3. `ApiService.kt` (ACTUALIZADO)
Maneja todos los endpoints de prendas y outfits según XANO:
- Usa `prenda` (singular) en lugar de `prendas`
- Usa `outfit` (singular) en lugar de `outfits`
- Incluye endpoints PATCH para editar

### 4. `RetrofitClient.kt` (ACTUALIZADO)
Ahora crea dos servicios:
- `authService` → AuthApiService
- `apiService` → ApiService

### 5. `UserRepository.kt` (ACTUALIZADO)
Usa `authService` para login y registro

### 6. `VistualApplication.kt` (ACTUALIZADO)
Inyecta `authService` y `apiService` correctamente

### 7. DTOs Actualizados
- `UserDto`: Usa `name` y `email` (campos de XANO)
- `PrendaDto`: Usa `user_id` y timestamps
- `OutfitDto`: Usa `user_id` y timestamps
- `LoginRequest`: Usa `email`
- `RegisterRequest`: Usa `name` y `email`

---

## 🚀 Cómo Probar la Conexión

### Opción 1: Usar XanoConnectionTester (Recomendado)

1. **Abre `MainActivity.kt`**

2. **Agrega esta línea en `onCreate()`**, justo después de `setContent {`:
   ```kotlin
   XanoConnectionTester.testConnection()
   ```

3. **Ejecuta la app**

4. **Abre Logcat** (View > Tool Windows > Logcat)

5. **Filtra por**: `XanoConnectionTester`

6. **Verás uno de estos resultados**:

   **✅ Si funciona:**
   ```
   === Iniciando prueba de conexión con XANO ===
   URL Auth: https://x8ki-letl-twmt.n7.xano.io/api:bOk8Zi6W/
   URL API: https://x8ki-letl-twmt.n7.xano.io/api:G1UzV9hT/
   Enviando petición de registro...
   ✅ CONEXIÓN EXITOSA!
   Usuario creado: Usuario Test
   ```

   **❌ Si hay error:**
   ```
   ❌ EXCEPCIÓN AL CONECTAR CON XANO
   Error: [mensaje de error]
   💡 [sugerencia de solución]
   ```

7. **Una vez confirmada la conexión**, comenta o elimina la línea de `testConnection()`

---

### Opción 2: Probar Directamente en la App

1. **Ejecuta la app normalmente**

2. **Intenta registrarte**:
   - Nombre: "Test User"
   - Email: "test@example.com"
   - Password: "test123"

3. **Si el registro es exitoso**:
   - ✅ XANO está funcionando correctamente
   - Podrás iniciar sesión y usar la app

4. **Si hay error**:
   - Revisa Logcat filtrando por `OkHttp`
   - Verás los detalles de la petición HTTP

---

## 📊 Verificar en XANO

Para confirmar que los datos llegan a XANO:

1. **Ve a tu proyecto en XANO**: https://app.xano.com

2. **Navega a "Database"**

3. **Abre la tabla `user`**

4. **Deberías ver**:
   - El usuario "Test User" (si usaste el tester)
   - O tu usuario registrado desde la app

5. **Ve a "Logs"** en XANO:
   - Verás todas las peticiones que llegaron
   - POST /auth/signup
   - POST /auth/login
   - etc.

---

## 🔍 Formato de Respuestas de XANO

### Login/Signup Response
```json
{
  "authToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "user": {
    "id": 1,
    "name": "Juan Pérez",
    "email": "juan@test.com",
    "account_id": 1,
    "role": "user",
    "created_at": 1732550400000,
    "updated_at": 1732550400000
  }
}
```

### Lista de Prendas
```json
[
  {
    "id": 1,
    "nombre": "Camisa azul",
    "categoria": "PARTE_SUPERIOR",
    "color": "AZUL",
    "imagen_url": "https://...",
    "user_id": 1,
    "created_at": 1732550400000,
    "updated_at": 1732550400000
  }
]
```

### Lista de Outfits
```json
[
  {
    "id": 1,
    "nombre": "Outfit casual",
    "user_id": 1,
    "prenda_superior_id": 1,
    "prenda_inferior_id": 2,
    "zapatos_id": 3,
    "created_at": 1732550400000,
    "updated_at": 1732550400000
  }
]
```

**Nota**: Si las respuestas no coinciden, necesitaremos ajustar `LoginResponse`, `RegisterResponse`, etc.

---

## 🐛 Troubleshooting

### Error: "UnknownHostException"
- ❌ La URL está mal configurada
- ✅ Verifica `ApiConfig.kt`
- ✅ Asegúrate que las URLs terminen con `/`

### Error: "401 Unauthorized"
- ❌ Token inválido o expirado
- ✅ Verifica que estés enviando el token en el header
- ✅ Formato correcto: `"Bearer {token}"`

### Error: "404 Not Found"
- ❌ El endpoint no existe en XANO
- ✅ Verifica que los endpoints estén publicados en XANO
- ✅ Revisa que las rutas coincidan exactamente

### Error: JSON parsing
- ❌ El formato de respuesta no coincide con los DTOs
- ✅ Revisa la respuesta real en Logcat (OkHttp)
- ✅ Ajusta los DTOs según sea necesario

---

## ✨ Próximos Pasos (Opcional)

### 1. Sincronizar Prendas con XANO
Actualmente las prendas se guardan solo localmente. Para sincronizar con XANO:

1. Modifica `PrendaRepository.kt`
2. Cambia `getPrendas()` para llamar a `apiService.getAllPrendas()`
3. Usa Room como caché local

### 2. Sincronizar Outfits con XANO
Similar a prendas:

1. Modifica `OutfitRepository.kt`
2. Usa `apiService.getAllOutfits()`
3. Implementa sincronización bidireccional

### 3. Implementar Subida de Imágenes
Para subir imágenes de prendas a XANO:

1. Usa el almacenamiento de archivos de XANO
2. Sube la imagen primero
3. Guarda la URL retornada en el campo `imagen_url`

---

## 📝 Checklist Final

- [x] URLs configuradas en `ApiConfig.kt`
- [x] `AuthApiService` creado con endpoints de autenticación
- [x] `ApiService` actualizado con endpoints exactos de XANO
- [x] `RetrofitClient` maneja dos servicios
- [x] `UserRepository` usa `authService`
- [x] DTOs actualizados con nombres correctos
- [ ] **Probar conexión** con `XanoConnectionTester`
- [ ] **Registrar un usuario** desde la app
- [ ] **Verificar en XANO** que el usuario se creó
- [ ] **Iniciar sesión** con el usuario creado

---

## 🎯 Resumen

✅ **Configurado:**
- Dos URLs de XANO separadas
- Endpoints de autenticación (signup, login, me)
- Endpoints de prendas (CRUD completo)
- Endpoints de outfits (CRUD completo)
- DTOs con nombres correctos de campos

⏳ **Pendiente:**
- Probar la conexión
- Ajustar respuestas si es necesario
- (Opcional) Implementar sincronización completa

---

**¡Ahora solo falta probar que funcione!** 🚀

Ejecuta `XanoConnectionTester.testConnection()` y revisa el resultado en Logcat.
