package com.psilva.comunicafacil.ui.components

import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
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
