package com.psilva.comunicafacil.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.psilva.comunicafacil.ui.theme.AppErrorContainerCustom
import com.psilva.comunicafacil.ui.theme.AppInfoContainer
import com.psilva.comunicafacil.ui.theme.AppOnErrorContainerCustom
import com.psilva.comunicafacil.ui.theme.AppOnInfoContainer
import com.psilva.comunicafacil.ui.theme.AppOnSuccessContainer
import com.psilva.comunicafacil.ui.theme.AppSuccessContainer

enum class TipoMensaje {
    EXITO, INFO, ERROR
}

@Composable
fun AppSnackbarHost(
    hostState: SnackbarHostState,
    tipoMensaje: TipoMensaje
) {
    // Se fuerza el contenedor a ocupar todo el alto para poder alinear el Snackbar arriba,
    // evitando que el teclado lo tape y respetando la barra de estado.
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(WindowInsets.statusBars.asPaddingValues())
            .padding(top = 8.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        SnackbarHost(hostState = hostState) { data: SnackbarData ->
            val (bg, fg) = when (tipoMensaje) {
                TipoMensaje.EXITO -> AppSuccessContainer to AppOnSuccessContainer
                TipoMensaje.INFO -> AppInfoContainer to AppOnInfoContainer
                TipoMensaje.ERROR -> AppErrorContainerCustom to AppOnErrorContainerCustom
            }

            Snackbar(
                snackbarData = data,
                containerColor = bg,
                contentColor = fg
            )
        }
    }
}
