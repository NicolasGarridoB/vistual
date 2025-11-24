# Guía de Uso y Configuración - Vistual

## 🎯 Configuración Inicial

### 1. Configurar la URL del API

**Archivo**: `app/src/main/java/com/example/vistual/api/RetrofitClient.kt`

```kotlin
// Línea 18
private const val BASE_URL = "https://api.vistual.example.com/"
```

**Opciones según tu entorno**:

#### Para desarrollo con emulador Android:
```kotlin
private const val BASE_URL = "http://10.0.2.2:8080/api/"
```
- `10.0.2.2` es la IP especial para localhost en emuladores Android
- Cambia `8080` por el puerto de tu servidor

#### Para desarrollo con dispositivo físico:
```kotlin
private const val BASE_URL = "http://192.168.1.100:8080/api/"
```
- Reemplaza `192.168.1.100` con la IP local de tu computadora
- Tu teléfono y computadora deben estar en la misma red WiFi

#### Para producción:
```kotlin
private const val BASE_URL = "https://tu-servidor.com/api/"
```

### 2. Sincronizar Gradle

Después de clonar el proyecto:
1. Abrir en Android Studio
2. Esperar a que Gradle sincronice automáticamente
3. Si hay errores, ejecutar: `Build → Clean Project` y luego `Build → Rebuild Project`

## 🧪 Ejecutar Tests

### Desde Android Studio

1. **Todos los tests**:
   - Click derecho en `app/src/test/java`
   - Seleccionar `Run 'All Tests'`

2. **Un test específico**:
   - Abrir archivo de test (ej: `UserRepositoryTest.kt`)
   - Click en el ícono verde al lado del nombre de la clase
   - Seleccionar `Run 'UserRepositoryTest'`

3. **Un método de test**:
   - Click en el ícono verde al lado del método
   - Seleccionar `Run 'nombreDelMetodo()'`

### Desde Terminal

```bash
# Ejecutar todos los tests unitarios
./gradlew test

# Ejecutar tests con reporte detallado
./gradlew test --info

# Limpiar y ejecutar tests
./gradlew clean test

# Ver reporte HTML (después de ejecutar tests)
# Windows:
start app/build/reports/tests/testDebugUnitTest/index.html

# Linux/Mac:
open app/build/reports/tests/testDebugUnitTest/index.html
```

## 🏃 Ejecutar la Aplicación

### Opción 1: Con dispositivo físico
1. Habilitar "Opciones de desarrollador" en tu Android
2. Activar "Depuración USB"
3. Conectar por USB
4. En Android Studio: `Run → Run 'app'` (Shift+F10)

### Opción 2: Con emulador
1. `Tools → Device Manager`
2. Crear un dispositivo virtual (API 24+)
3. Iniciar el emulador
4. `Run → Run 'app'` (Shift+F10)

## 📊 Arquitectura MVVM Explicada

### Flujo de Datos

```
Usuario interactúa con UI (Compose)
            ↓
UI llama funciones del ViewModel
            ↓
ViewModel llama al Repository
            ↓
Repository decide si usa API o Room
            ↓
Repository retorna Result<T>
            ↓
ViewModel actualiza State
            ↓
UI se recompone automáticamente
```

### Ejemplo: Login de Usuario

**1. Usuario ingresa credenciales en LoginScreen.kt**:
```kotlin
LoginScreen(
    onLoginClick = { correo, password ->
        authViewModel.login(correo, password)
    }
)
```

**2. AuthViewModel.kt procesa el login**:
```kotlin
fun login(correo: String, password: String) {
    viewModelScope.launch {
        _loginState.value = LoginState(isLoading = true)
        val result = userRepository.validarCredenciales(correo, password)
        // Actualiza estado según resultado
    }
}
```

**3. UserRepository.kt consulta API y Room**:
```kotlin
suspend fun validarCredenciales(correo: String, password: String): Result<Usuario> {
    return try {
        // Intenta validar con API
        val response = apiService.login(LoginRequest(correo, password))
        if (response.isSuccessful) {
            // Guarda en Room y retorna éxito
        } else {
            // Fallback: valida contra Room
        }
    } catch (e: Exception) {
        // Si no hay internet, valida solo contra Room
    }
}
```

**4. UI reacciona automáticamente al estado**:
```kotlin
val loginState = authViewModel.loginState.value

if (loginState.isLoggedIn) {
    // Navegar a pantalla principal
}
if (loginState.errorMessage != null) {
    // Mostrar error
}
```

## 🔄 Sincronización de Datos

### Estrategia Implementada: Write-Through + Read-Local

#### Escritura (Create/Update/Delete):
```
Usuario crea prenda
    ↓
1. Guardar en Room (éxito garantizado)
    ↓
2. Intentar enviar al API
    ↓
3a. Si API responde: Actualizar ID local
3b. Si API falla: Marcar para sync posterior
```

#### Lectura (Read):
```
Usuario abre lista de prendas
    ↓
1. Leer desde Room (fuente de verdad)
    ↓
2. Mostrar datos inmediatamente
    ↓
3. En background: sincronizar con API
    ↓
4. Actualizar Room con datos nuevos del API
```

### Forzar Sincronización Manual

En cualquier ViewModel con acceso a PrendaRepository:

```kotlin
viewModelScope.launch {
    val result = prendaRepository.syncPrendasFromApi(usuarioId)
    if (result.isSuccess) {
        // Sincronización exitosa
    } else {
        // Falló (probablemente sin internet)
    }
}
```

## 🧩 Agregar Nuevas Funcionalidades

### Ejemplo: Agregar campo "talla" a Prenda

**1. Actualizar entidad Room** (`model/Prenda.kt`):
```kotlin
@Entity(tableName = "prendas")
data class Prenda(
    // ... campos existentes
    val talla: String = "M" // Nuevo campo con valor por defecto
)
```

**2. Incrementar versión de BD** (`db/AppDatabase.kt`):
```kotlin
@Database(..., version = 3) // Cambiar de 2 a 3
```

**3. Actualizar DTO del API** (`api/models/PrendaDto.kt`):
```kotlin
data class PrendaDto(
    // ... campos existentes
    @Json(name = "talla")
    val talla: String? = null
)
```

**4. Actualizar mapeo en Repository** (`repository/PrendaRepository.kt`):
```kotlin
// En syncPrendasFromApi
Prenda(
    // ... mapeos existentes
    talla = dto.talla ?: "M"
)

// En insert
PrendaDto(
    // ... mapeos existentes
    talla = prenda.talla
)
```

**5. Actualizar UI** (`ui/AgregarPrendaScreen.kt`):
```kotlin
// Agregar campo en el formulario
var talla by remember { mutableStateOf("M") }

OutlinedTextField(
    value = talla,
    onValueChange = { talla = it },
    label = { Text("Talla") }
)
```

**6. Ejecutar y probar**:
- Room recreará la tabla automáticamente (datos se perderán en desarrollo)
- Para producción, necesitarías implementar Migration

## 🐛 Solución de Problemas Comunes

### Error: "Cannot access database on main thread"
**Causa**: Intentando hacer operación de BD en hilo principal  
**Solución**: Todas las operaciones de DAO están en funciones `suspend`, asegúrate de llamarlas dentro de coroutines:
```kotlin
viewModelScope.launch {
    prendaRepository.insert(prenda)
}
```

### Error: "lateinit property has not been initialized"
**Causa**: Intentando usar ViewModelFactory antes de inicializar repositorios  
**Solución**: Verificar que `VistualApplication` esté declarado en `AndroidManifest.xml`:
```xml
<application
    android:name=".VistualApplication"
    ...>
```

### Tests fallan con "Method ... not mocked"
**Causa**: Mockito necesita configuración adicional para clases de Android  
**Solución**: Ya incluimos `mockito-inline` en dependencies. Si persiste:
```kotlin
// En archivo de test, agregar antes de @Before:
@Mock(answer = Answers.RETURNS_DEEP_STUBS)
private lateinit var sharedPreferences: SharedPreferences
```

### API no responde desde emulador
**Causa**: URL incorrecta o servidor no accesible  
**Solución**:
1. Verificar que el servidor esté corriendo
2. Usar `http://10.0.2.2:PUERTO` en lugar de `localhost`
3. Verificar permisos de INTERNET en AndroidManifest

### Gradle sync falla
**Causa**: Versiones incompatibles o caché corrupta  
**Solución**:
```bash
# 1. Limpiar caché
./gradlew clean

# 2. Invalidar cachés de Android Studio
File → Invalidate Caches → Invalidate and Restart

# 3. Si persiste, eliminar carpetas:
rm -rf .gradle
rm -rf app/build
```

## 📝 Checklist Pre-Entrega

- [ ] Todos los tests pasan (`./gradlew test`)
- [ ] App compila sin errores (`./gradlew assembleDebug`)
- [ ] No hay archivos de Activities o XML layouts obsoletos
- [ ] README actualizado con arquitectura completa
- [ ] Commits tienen mensajes descriptivos
- [ ] Código comentado adecuadamente
- [ ] No hay TODOs ni código comentado sin usar
- [ ] Funcionalidades principales probadas en dispositivo/emulador
- [ ] API configurada correctamente (o funciona en modo offline)

## 🎓 Conceptos para Defensa del Proyecto

### ¿Qué es MVVM?
- **Model**: Datos (Room entities, API DTOs)
- **View**: UI (Jetpack Compose screens)
- **ViewModel**: Lógica de presentación y estado

### ¿Por qué Repository Pattern?
- Abstrae la fuente de datos (API o BD local)
- Permite cambiar implementación sin afectar ViewModel
- Facilita testing (se puede mockear fácilmente)

### ¿Cómo funciona Room?
- ORM (Object-Relational Mapping) para SQLite
- Entidades son clases Kotlin con @Entity
- DAOs son interfaces con queries SQL
- Validación en tiempo de compilación

### ¿Cómo funciona Retrofit?
- Convierte interface Kotlin en peticiones HTTP
- Usa Moshi para serializar/deserializar JSON
- OkHttp maneja la conexión HTTP real
- Soporta coroutines con `suspend fun`

### ¿Por qué usar coroutines?
- Evita bloquear el hilo principal
- Código asíncrono que se lee de forma secuencial
- Manejo automático de ciclo de vida con `viewModelScope`
- Mejor que callbacks o RxJava

## 🔗 Recursos Adicionales

- [Documentación oficial de Room](https://developer.android.com/training/data-storage/room)
- [Guía de Retrofit](https://square.github.io/retrofit/)
- [Jetpack Compose Tutorial](https://developer.android.com/jetpack/compose/tutorial)
- [Testing en Android](https://developer.android.com/training/testing)
- [Coroutines Guide](https://kotlinlang.org/docs/coroutines-guide.html)

---

**¿Necesitas ayuda?** Revisa los comentarios en el código, todos los archivos importantes están documentados.
