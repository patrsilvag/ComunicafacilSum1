# ComunicaFácil – Kotlin + Jetpack Compose

Aplicación Android desarrollada en **Kotlin** y **Jetpack Compose**, orientada a la comunicación asistida para personas con discapacidad sensorial del habla.  
El proyecto transforma el dispositivo móvil en una herramienta de apoyo comunicacional mediante **entrada de texto**, **salida por voz (Text To Speech)** y una interfaz diseñada bajo criterios de **accesibilidad (WCAG)**.

---

## 🏗️ Estructura del Proyecto

La aplicación se organiza siguiendo una arquitectura modular que separa responsabilidades y facilita la mantención del código:

- **ui/screens**  
  Pantallas principales de la aplicación:  
  `HomeScreen.kt`, `LoginScreen.kt`, `RegisterScreen.kt`, `RecoverScreen.kt`.

- **ui/components**  
  Componentes reutilizables de interfaz como `EmailField.kt`, `PasswordField.kt`, `PasswordField.kt` y `AppSnackbarHost.kt`.

- **viewmodel**  
  Gestión de estado y orquestación de la lógica relacionada con usuarios (`UsuariosViewModel.kt`).

- **data**  
  Fuente de datos y repositorio de usuarios (`UsuariosRepository.kt`, `UsuariosDataSource.kt`).

- **ui/settings / ui/utils**  
  Configuración de accesibilidad y utilidades comunes (modo de fuente, validaciones inline).

---

## 🧠 Tópicos de Kotlin Implementados y Justificados

A continuación, se detallan los **9 conceptos de Kotlin solicitados en la Semana 5**, indicando su **ubicación real** y su **uso justificado dentro de la aplicación**.

---

### 1. Funciones de orden superior
**Ubicación:** `ui/screens/LoginScreen.kt`, `RegisterScreen.kt`

**Descripción:**  
Se utilizan funciones de orden superior para manejar navegación y cambios de estado desde la UI hacia niveles superiores de la aplicación.  
Las pantallas reciben lambdas como parámetros (`onIrARegistro`, `onIrAHome`, `onVolverLogin`, etc.), permitiendo desacoplar la navegación de la lógica interna de la pantalla.

---

### 2. Lambdas
**Ubicación:** `ui/screens/HomeScreen.kt`

**Descripción:**  
Se emplean lambdas en la definición de comportamientos dinámicos asociados a la interfaz, especialmente en los eventos `onClick` de botones, como las acciones **“Mostrar en pantalla”** y **“Hablar”**, manteniendo la UI declarativa y reactiva.

---

### 3. Lambda con etiqueta
**Ubicación:** `ui/screens/HomeScreen.kt`

**Descripción:**  
Se aplica una lambda con etiqueta dentro de la función `procesarYMostrarMensaje()` utilizando el bloque:

`run validacion@{
    if (!texto.isSpeakable) {
        errorMensaje = "Escriba un mensaje antes de mostrarlo"
        return@validacion
    }
}`

### 4. Funciones de extensión
**Ubicación:** `ui/screens/HomeScreen.kt` y utilidades de validación

**Descripción:**  
Se utilizan funciones de extensión sobre `String` para normalizar y preparar datos antes de su procesamiento, como el uso de `trim()` y `lowercase()`, evitando duplicación de lógica y mejorando la legibilidad del código.

---

### 5. Propiedades de extensión
**Ubicación:** `ui/screens/HomeScreen.kt`
**Descripción:**  
Se define la siguiente propiedad de extensión:

`
val String.isSpeakable: Boolean
    get() = this.trim().isNotBlank()`

### 6. Filter (Funciones de colección)

**Ubicación:** viewmodel/UsuariosViewModel.kt / data/UsuariosRepository.kt
**Descripción:**
Se utilizan funciones de colección como any y filter para procesar listas de usuarios, por ejemplo al validar credenciales o verificar la existencia de un correo registrado, permitiendo un manejo declarativo y expresivo de los datos.

### 7. Funciones inline
**Ubicación:** ui/utils/ValidationUtils.kt
**Descripción:**
Se implementan funciones inline para centralizar validaciones comunes de formularios (Login, Registro y Recuperar contraseña).
Esto reduce duplicación de código y evita la creación innecesaria de objetos lambda durante las recomposiciones en Jetpack Compose.

### 8. Excepciones
**Ubicación:** ui/screens/HomeScreen.kt
**Descripción:**
La aplicación evalúa estados excepcionales del motor TextToSpeech, como LANG_MISSING_DATA o LANG_NOT_SUPPORTED, controlando estos escenarios para informar al usuario mediante la interfaz en lugar de provocar fallos en ejecución.

### 9. Try / Catch
**Ubicación:** viewmodel/UsuariosViewModel.kt y data/UsuariosRepository.kt
**Descripción:**
Se implementan bloques try/catch en operaciones sensibles como el registro de usuarios, capturando errores inesperados y devolviendo resultados controlados que luego son informados al usuario mediante Snackbar.

## ✅ Tecnologías y Accesibilidad

- **Kotlin & Jetpack Compose**: Base del desarrollo moderno en Android.
- **Material Design 3**: Uso de componentes como `ElevatedCard` y `OutlinedTextField`.
- **Accesibilidad**: Inclusión de etiquetas semánticas (`contentDescription`), manejo de contraste visual y tipografía escalable.

**Autor:** Patricio Silva  
**Asignatura:** Desarrollo de Aplicaciones Móviles  
**Evaluación:** Sumativa 2 – Semana 5