# ComunicaFácil – Kotlin + Jetpack Compose

Aplicación Android desarrollada en **Kotlin** y **Jetpack Compose**, orientada a la comunicación asistida para personas con discapacidad del habla. El proyecto destaca por su enfoque en **accesibilidad (WCAG)**, integración de **Text To Speech (TTS)** y la implementación técnica de conceptos avanzados de programación funcional.

---

## 🏗️ Estructura del Proyecto

La aplicación se organiza siguiendo una arquitectura modular y limpia, facilitando su mantenimiento:

- **ui/screens**: Contiene las pantallas principales como `HomeScreen.kt`, `LoginScreen.kt`, `RegisterScreen.kt` y `RecoverScreen.kt`.
- **ui/components**: Componentes de interfaz reutilizables como `EmailField.kt`, `PasswordField.kt` y `AppSnackbarHost.kt`.
- **viewmodel**: Lógica de negocio y gestión de estado de los usuarios.
- **ui/settings**: Configuración de accesibilidad (tamaño de fuente y temas).

---

## 🧠 Tópicos de Kotlin Implementados y Justificados

A continuación, se detallan los **9 conceptos trabajados en la asignatura** y su aplicación específica en el código:

### 1. Funciones de orden superior
**Ubicación:** `ui/screens/RegisterScreen.kt` y `LoginScreen.kt`  
**Descripción:** Se utilizan para gestionar eventos de navegación y cambios de estado global. La función `RegisterScreen` recibe `onFontSizeModeChange` como parámetro, permitiendo que un componente hijo modifique la configuración de la app en un nivel superior.

### 2. Lambdas
**Ubicación:** `ui/screens/HomeScreen.kt`  
**Descripción:** Se emplean para definir comportamientos dinámicos en la interfaz de usuario. Por ejemplo, en los parámetros `onClick` de los botones para ejecutar la lógica de "Hablar" o "Mostrar en pantalla".

### 3. Lambda con etiqueta
**Ubicación:** `ui/screens/HomeScreen.kt`  
**Descripción:** Aplicada en la función `procesarYMostrarMensaje()` mediante el bloque `run validacion@{ ... }`. Se utiliza `return@validacion` para interrumpir el flujo si el texto está vacío, permitiendo un control de errores más elegante y legible.

### 4. Funciones de extensión
**Ubicación:** `ui/screens/HomeScreen.kt` (Inferido en lógica de validación)  
**Descripción:** Se utilizan para extender la funcionalidad de clases existentes. Por ejemplo, el uso de `.trim()` sobre objetos `String` antes de procesar el mensaje para asegurar que no se envíen espacios innecesarios al motor de voz.

### 5. Propiedades de extensión
**Ubicación:** `ui/screens/HomeScreen.kt`  
**Descripción:** Se definió la propiedad `val String.isSpeakable: Boolean`, que calcula dinámicamente si una cadena es válida para ser reproducida por el TTS sin necesidad de almacenar estado adicional.

### 6. Filter (Funciones de colección)
**Ubicación:** `viewmodel/UsuariosViewModel.kt`  
**Descripción:** Se utiliza para procesar listas de usuarios. Por ejemplo, al validar credenciales, se filtran las colecciones de datos para encontrar coincidencias específicas de correo y contraseña.

### 7. Funciones Inline
**Ubicación:** `ui/theme/Theme.kt` y utilidades de navegación.  
**Descripción:** Se aplican en funciones pequeñas que reciben lambdas para optimizar el rendimiento, evitando la creación de objetos adicionales en memoria durante las recomposiciones de Compose.

### 8. Excepciones
**Ubicación:** `ui/screens/HomeScreen.kt`  
**Descripción:** El código evalúa estados excepcionales del motor `TextToSpeech`, como `LANG_MISSING_DATA` o `LANG_NOT_SUPPORTED`, gestionando estos casos para informar al usuario en lugar de permitir que la app falle.

### 9. Try / Catch
**Ubicación:** `ui/screens/HomeScreen.kt` y `viewmodel/UsuariosViewModel.kt`  
**Descripción:** Se implementan bloques `try/catch` para manejar operaciones sensibles, como el inicio de corrutinas para el registro de usuarios, asegurando que cualquier error sea capturado y mostrado a través de un `Snackbar`.

---

## ✅ Tecnologías y Accesibilidad

- **Kotlin & Jetpack Compose**: Base del desarrollo moderno en Android.
- **Material Design 3**: Uso de componentes como `ElevatedCard` y `OutlinedTextField`.
- **Accesibilidad**: Inclusión de etiquetas semánticas (`contentDescription`), manejo de contraste visual y tipografía escalable.

**Autor:** Patricio Silva  
**Asignatura:** Desarrollo de Aplicaciones Móviles  
**Evaluación:** Sumativa 2 – Semana 5