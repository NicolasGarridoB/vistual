# Vistual - Closet Virtual Android

Vistual es una aplicación Android moderna que permite gestionar tu closet virtual, tomar fotos de tu ropa, categorizarla y armar outfits. Desarrollada como proyecto universitario siguiendo las mejores prácticas de desarrollo Android.

## 🚀 Conexión con Backend XANO

Este proyecto está **configurado para conectarse con XANO**, una plataforma de backend sin código que proporciona:
- ✅ Base de datos PostgreSQL
- ✅ API REST automática
- ✅ Autenticación JWT
- ✅ Almacenamiento de imágenes

### ⚡ Inicio Rápido (5 minutos)

1. **Crea tu backend en XANO**:
   - Ve a [XANO.com](https://www.xano.com) y crea una cuenta
   - Crea un nuevo proyecto llamado "Vistual"
   - Usa el contenido de `XANO_PROMPT.md` para generar el backend automáticamente

2. **Copia tu URL de XANO**:
   - En XANO, ve a la sección "API" y copia la "Base URL"
   - Se verá así: `https://x8ki-letl-twmt.n7.xano.io/api:xxxxx`

3. **Configura la app**:
   - Abre `app/src/main/java/com/example/vistual/api/ApiConfig.kt`
   - Reemplaza `BASE_URL` con tu URL de XANO (asegúrate que termine con `/`)

4. **¡Listo!** Ejecuta la app y todo se sincronizará con tu backend

📖 **Guías detalladas**:
- `INICIO_RAPIDO.md` - Paso a paso con capturas
- `XANO_PROMPT.md` - Prompt completo para XANO
- `XANO_SETUP.md` - Configuración avanzada y troubleshooting

## Funcionalidades

- **Sistema de Autenticación**: Login y registro con validación de credenciales
- **Captura de Fotos**: Usa la cámara del dispositivo o galería para fotografiar prendas
- **Gestión de Prendas**: Organiza tu ropa por 3 categorías principales (Parte Superior, Parte Inferior, Zapatos)
- **Closet Virtual**: Visualiza todas tus prendas en una interfaz tipo grid moderna con filtros por categoría
- **Carrusel de Outfits**: Vista horizontal por secciones para combinar prendas fácilmente
- **Creación de Outfits**: Selecciona una prenda de cada categoría y guarda tus combinaciones favoritas
- **Outfits Guardados**: Visualiza tus outfits completos con imágenes de las prendas
- **Persistencia Dual**: 
  - **Interna**: Room Database (SQLite) para almacenamiento local
  - **Externa**: API REST con Retrofit con estrategia de fallback (funciona offline)

## Arquitectura

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
- **CameraX + Gallery Picker** para captura y selección de fotos
- **Carrusel infinito** para visualización de prendas con scroll continuo
- Ubicación: `com.example.vistual.ui`

## Tecnologías Utilizadas

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

## Estructura del Proyecto

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
│   ├── MainScreen.kt             # Pantalla principal (closet con grid)
│   ├── AgregarPrendaScreen.kt    # Pantalla agregar prenda (cámara/galería)
│   ├── CarouselOutfitScreen.kt   # Pantalla de carrusel para crear outfits
│   ├── SavedOutfitsScreen.kt     # Pantalla de outfits guardados con imágenes
│   ├── VistualApp.kt             # Navegación principal
│   └── theme/                    # Tema Material Design
├── MainActivity.kt               # Activity principal
└── VistualApplication.kt         # Application class (DI manual)
```

## Instalación y Uso

### Requisitos Previos
- Android Studio Hedgehog (2023.1.1) o superior
- JDK 17 o superior
- Dispositivo Android con API 24+ o emulador
- Permisos de cámara para captura de fotos

### Pasos de Instalación

1. **Clonar el repositorio**
```bash
git clone https://github.com/NicolasGarridoB/vistual.git
cd vistual
```

2. **Abrir en Android Studio**
   - File → Open → Seleccionar carpeta del proyecto
   - Esperar sincronización de Gradle

3. **Configurar rutas no-ASCII** (Ya incluido en gradle.properties)
   - El proyecto incluye `android.overridePathCheck=true` para soportar nombres con tildes

4. **Ejecutar la aplicación**
   - Conectar dispositivo o iniciar emulador
   - Run → Run 'app' o presionar Shift+F10
   - Otorgar permisos de cámara cuando se soliciten

### Uso de la Aplicación

1. **Registro/Login**: Crea una cuenta o inicia sesión
2. **Agregar Prendas**: 
   - Click en botón "+" flotante
   - Elige entre cámara o galería
   - Selecciona categoría (Parte Superior, Parte Inferior, Zapatos)
   - Guarda la prenda
3. **Ver Closet**: Visualiza tus prendas en grid con filtros por categoría
4. **Crear Outfit**: 
   - Click en ícono de carrusel en la barra superior
   - Desliza horizontalmente en cada sección
   - Selecciona una prenda de cada categoría
   - Guarda el outfit con un nombre
5. **Ver Outfits**: Accede a "Outfits Guardados" para ver tus combinaciones

##  Ejecutar Tests

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

## 🔧 Configuración de Backend

### Opción 1: XANO (Recomendado) 🌟

**XANO es la forma más rápida de tener un backend funcionando (5 minutos)**:

1. Sigue la guía en `INICIO_RAPIDO.md`
2. Usa el prompt en `XANO_PROMPT.md` para generar el backend automáticamente
3. Configura la URL en `ApiConfig.kt`
4. ¡Listo! Tu app está conectada a un backend real

**Ventajas de XANO**:
- ✅ Sin código backend necesario
- ✅ Base de datos PostgreSQL automática
- ✅ API REST generada automáticamente
- ✅ Autenticación JWT incluida
- ✅ Almacenamiento de archivos/imágenes
- ✅ Panel de administración visual
- ✅ Logs en tiempo real
- ✅ Plan gratuito disponible

### Opción 2: Backend Propio

Si prefieres implementar tu propio backend, estos son los endpoints que debe implementar:

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

Ver especificaciones completas en `XANO_PROMPT.md` (sirve también como documentación de API).

### Opción 3: Modo Offline (Sin Backend)

Si no tienes servidor disponible, la app funciona completamente offline usando solo Room Database.


## Funcionalidades de Seguridad

- Passwords no se almacenan en SharedPreferences (solo IDs y emails)
- Tokens JWT guardados de forma segura
- Validación de inputs en ViewModels
- Manejo de errores en toda la app

## Permisos Requeridos

```xml
<uses-permission android:name="android.permission.CAMERA" />
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```



---

**Última actualización**: Noviembre 2025  
**Versión**: 1.0.0  
**Estado**: ✅ Producción (todas las funcionalidades implementadas y testeadas)

