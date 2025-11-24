# Vistual - Closet Virtual Android

Vistual es una aplicación Android moderna que permite gestionar tu closet virtual, tomar fotos de tu ropa, categorizarla y armar outfits. Desarrollada como proyecto universitario siguiendo las mejores prácticas de desarrollo Android.

## 🎯 Funcionalidades

- **Sistema de Autenticación**: Login y registro con validación de credenciales
- **Captura de Fotos**: Usa la cámara del dispositivo para fotografiar prendas
- **Gestión de Prendas**: Organiza tu ropa por categorías (Camisas, Pantalones, Zapatos, Accesorios, etc.)
- **Closet Virtual**: Visualiza todas tus prendas en una interfaz tipo grid moderna
- **Creación de Outfits**: Combina prendas para crear y guardar outfits completos
- **Persistencia Dual**: 
  - **Interna**: Room Database (SQLite) para almacenamiento local
  - **Externa**: API REST con Retrofit para sincronización en la nube

## 🏗️ Arquitectura

El proyecto sigue el patrón **MVVM (Model-View-ViewModel)** con separación clara de capas:

### Estructura de Capas

```
┌─────────────────────────────────────┐
│   UI Layer (Jetpack Compose)       │
│   - Screens (LoginScreen, etc.)    │
│   - Navegación                      │
└────────────┬────────────────────────┘
             │
┌────────────▼────────────────────────┐
│   ViewModel Layer                   │
│   - AuthViewModel                   │
│   - MainViewModel                   │
│   - AgregarPrendaViewModel          │
│   - OutfitViewModel                 │
└────────────┬────────────────────────┘
             │
┌────────────▼────────────────────────┐
│   Repository Layer                  │
│   - UserRepository                  │
│   - PrendaRepository                │
│   - OutfitRepository                │
└────────┬────────────┬───────────────┘
         │            │
    ┌────▼────┐  ┌────▼─────┐
    │  Room   │  │ Retrofit │
    │   DAO   │  │   API    │
    └─────────┘  └──────────┘
```

### Capa de Datos (Data Layer)

#### Persistencia Interna - Room Database
- **Entidades**: `Usuario`, `Prenda`, `Outfit`
- **DAOs**: `UsuarioDao`, `PrendaDao`, `OutfitDao`
- **Base de Datos**: `AppDatabase` (SQLite)
- **Ubicación**: `com.example.vistual.db` y `com.example.vistual.model`

#### Persistencia Externa - API REST
- **Cliente HTTP**: Retrofit con OkHttp
- **Serialización**: Moshi para JSON
- **Endpoints**: Login, Register, CRUD de Prendas
- **Ubicación**: `com.example.vistual.api`
- **Modelos DTO**: `com.example.vistual.api.models`

### Capa de Negocio (Domain Layer)

#### Repositories
Los repositorios implementan la lógica de sincronización:
1. **Operaciones de escritura**: Primero guardan en Room (local), luego intentan sincronizar con API
2. **Operaciones de lectura**: Retornan datos de Room como fuente de verdad
3. **Sincronización**: Método `syncFromApi()` para actualizar datos locales desde el servidor
4. **Fallback**: Si el API falla, la app sigue funcionando con datos locales

Ubicación: `com.example.vistual.repository`

### Capa de Presentación (Presentation Layer)

#### ViewModels
- Gestionan el estado de la UI usando `StateFlow` y `State`
- Ejecutan operaciones asíncronas con Coroutines
- No contienen referencias a Android Framework (excepto ViewModel base)
- Ubicación: `com.example.vistual.viewmodel`

#### UI con Jetpack Compose
- **Material Design 3** para diseño moderno
- **Navigation Compose** para navegación entre pantallas
- **Accompanist Permissions** para permisos de cámara
- **Coil** para carga de imágenes
- Ubicación: `com.example.vistual.ui`

## 🛠️ Tecnologías Utilizadas

### Core
- **Lenguaje**: Kotlin 1.9+
- **Min SDK**: 24 (Android 7.0)
- **Target SDK**: 34 (Android 14)

### UI
- Jetpack Compose + Material Design 3
- Navigation Compose
- CameraX para captura de fotos
- Coil para carga de imágenes

### Arquitectura y Persistencia
- Room Database (SQLite)
- Retrofit + OkHttp (API REST)
- Moshi (JSON parsing)
- Coroutines + Flow (programación asíncrona)
- ViewModel + LiveData/StateFlow

### Testing
- JUnit 4
- Mockito + Mockito-Kotlin
- Coroutines Test
- AndroidX Test (Espresso, JUnit)

## 📱 Estructura del Proyecto

```
app/src/main/java/com/example/vistual/
├── api/
│   ├── models/           # DTOs para API REST
│   │   ├── LoginRequest.kt
│   │   ├── LoginResponse.kt
│   │   ├── RegisterRequest.kt
│   │   ├── RegisterResponse.kt
│   │   ├── PrendaDto.kt
│   │   ├── PrendasResponse.kt
│   │   └── UserDto.kt
│   ├── ApiService.kt     # Interface de Retrofit
│   └── RetrofitClient.kt # Configuración de Retrofit
├── db/
│   ├── AppDatabase.kt    # Configuración de Room
│   ├── PrendaDao.kt
│   ├── OutfitDao.kt
│   └── UsuarioDao.kt
├── model/
│   ├── Prenda.kt         # Entidades de Room
│   ├── Outfit.kt
│   ├── Usuario.kt
│   └── EnumConverter.kt  # TypeConverter para Room
├── repository/
│   ├── UserRepository.kt     # Lógica de datos de usuarios
│   ├── PrendaRepository.kt   # Lógica de datos de prendas
│   └── OutfitRepository.kt   # Lógica de datos de outfits
├── viewmodel/
│   ├── AuthViewModel.kt          # Estado de autenticación
│   ├── MainViewModel.kt          # Estado de pantalla principal
│   ├── AgregarPrendaViewModel.kt # Estado de agregar prenda
│   ├── OutfitViewModel.kt        # Estado de outfits
│   └── ViewModelFactory.kt       # Factory para inyección
├── ui/
│   ├── LoginScreen.kt            # Pantalla de login
│   ├── RegisterScreen.kt         # Pantalla de registro
│   ├── MainScreen.kt             # Pantalla principal (closet)
│   ├── AgregarPrendaScreen.kt    # Pantalla agregar prenda
│   ├── SavedOutfitsScreen.kt     # Pantalla de outfits guardados
│   ├── VistualApp.kt             # Navegación principal
│   └── theme/                    # Tema Material Design
├── MainActivity.kt               # Activity principal
└── VistualApplication.kt         # Application class (DI manual)
```

## 🚀 Instalación y Uso

### Requisitos Previos
- Android Studio Hedgehog (2023.1.1) o superior
- JDK 17 o superior
- Dispositivo Android con API 24+ o emulador

### Pasos de Instalación

1. **Clonar el repositorio**
```bash
git clone https://github.com/NicolasGarridoB/vistual.git
cd vistual
```

2. **Abrir en Android Studio**
   - File → Open → Seleccionar carpeta del proyecto
   - Esperar sincronización de Gradle

3. **Configurar API REST** (Opcional pero recomendado)
   - Abrir `app/src/main/java/com/example/vistual/api/RetrofitClient.kt`
   - Cambiar `BASE_URL` a tu servidor:
     - Para emulador: `http://10.0.2.2:8080/api/`
     - Para dispositivo físico: `http://TU_IP_LOCAL:8080/api/`
     - Para producción: `https://tu-servidor.com/api/`

4. **Ejecutar la aplicación**
   - Conectar dispositivo o iniciar emulador
   - Run → Run 'app' o presionar Shift+F10

## 🧪 Ejecutar Tests

### Tests Unitarios

```bash
# En Android Studio
Run → Run 'All Tests'

# Desde terminal
./gradlew test
```

Los tests cubren:
- ✅ `UserRepositoryTest`: Lógica de autenticación y sincronización
- ✅ `PrendaRepositoryTest`: CRUD y sincronización de prendas
- ✅ `AuthViewModelTest`: Estados y flujos de login/register

### Ver Reporte de Tests

```bash
./gradlew test
# El reporte HTML estará en: app/build/reports/tests/testDebugUnitTest/index.html
```

## 🔧 Configuración de API REST

### Endpoints Esperados

El proyecto espera que el servidor implemente los siguientes endpoints:

#### Autenticación
```
POST /auth/register
Request: { "nombre": "string", "correo": "string", "password": "string" }
Response: { "success": boolean, "user": UserDto, "message": string }

POST /auth/login
Request: { "correo": "string", "password": "string" }
Response: { "success": boolean, "user": UserDto, "token": string }
```

#### Prendas
```
GET /prendas/usuario/{usuarioId}
Headers: Authorization: Bearer {token}
Response: { "success": boolean, "prendas": [PrendaDto] }

POST /prendas
Headers: Authorization: Bearer {token}
Request: PrendaDto
Response: PrendaDto

DELETE /prendas/{prendaId}
Headers: Authorization: Bearer {token}
Response: { "success": boolean }
```

### Modo Offline

Si no tienes servidor disponible, la app funciona completamente offline usando solo Room Database.

## 📋 Requisitos Cumplidos

### ✅ Requisitos Obligatorios

- [x] **Persistencia Interna**: Room Database con entidades Usuario, Prenda y Outfit
- [x] **Persistencia Externa**: Retrofit para consumir API REST
- [x] **Arquitectura MVVM**: Separación clara de capas (UI → ViewModel → Repository → Data)
- [x] **Jetpack Compose**: UI moderna y declarativa
- [x] **Material Design**: Tema consistente con Material 3
- [x] **Navegación**: Navigation Compose entre múltiples pantallas

### ✅ Aspectos Valorados

- [x] **Pruebas Unitarias**: Tests para ViewModels y Repositories
- [x] **Código Limpio**: Estructura organizada, sin código duplicado
- [x] **Git con Commits**: Historial claro y descriptivo
- [x] **README Completo**: Documentación detallada de arquitectura

### ✅ No Hay Puntos Negativos

- [x] Arquitectura MVVM correctamente implementada
- [x] Sin archivos basura (eliminados Activities y DBHelper obsoletos)
- [x] Separación de capas respetada (sin lógica en UI)
- [x] Sin variables de estado en UI (todo en ViewModels)
- [x] Todas las funcionalidades conectadas y funcionales

## 🔐 Funcionalidades de Seguridad

- Passwords no se almacenan en SharedPreferences (solo IDs y emails)
- Tokens JWT guardados de forma segura
- Validación de inputs en ViewModels
- Manejo de errores en toda la app

## 📝 Permisos Requeridos

```xml
<uses-permission android:name="android.permission.CAMERA" />
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```

## 🤝 Contribuciones

Este proyecto es académico. Si encuentras bugs o tienes sugerencias:
1. Abre un Issue en GitHub
2. Describe el problema claramente
3. Si es posible, adjunta screenshots

## 👨‍💻 Autor

**Nicolás Garrido**  
Proyecto desarrollado para cumplir requisitos universitarios de:
- Arquitectura MVVM
- Persistencia de datos (interna y externa)
- Testing unitario
- Mejores prácticas de Android

## 📄 Licencia

Este proyecto es para fines educativos y académicos.

---

## 🚧 Notas de Desarrollo

### Sincronización de Datos

La estrategia de sincronización implementada es:
1. **Write-Through**: Las escrituras van primero a local, luego intentan sincronizar con API
2. **Read-From-Local**: Las lecturas siempre son desde Room (fuente de verdad)
3. **Manual Sync**: Se puede forzar sincronización con `syncFromApi()`

### Mejoras Futuras

- [ ] Implementar WorkManager para sincronización en background
- [ ] Agregar DataStore para preferencias
- [ ] Implementar paginación en listas grandes
- [ ] Agregar modo oscuro completo
- [ ] Optimizar carga de imágenes con caché
- [ ] Implementar búsqueda y filtros avanzados
- [ ] Agregar compartir outfits con otros usuarios

---

**Última actualización**: Noviembre 2025

