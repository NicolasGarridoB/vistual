pluginManagement {
    repositories {
        google()
        mavenCentral()
        // AÑADE ESTE BLOQUE COMPLETO
        maven {
            name = "GradlePluginPortal"
            url = uri("https://plugins.gradle.org/m2/")
        }
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "vistual"
include(":app")

