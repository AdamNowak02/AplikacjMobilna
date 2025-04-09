package pl.wsei.pam.lab06.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme()
private val DarkColors = darkColorScheme()

@Composable
fun Lab06Theme(
    darkTheme: Boolean = false, // możesz też użyć isSystemInDarkTheme()
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = androidx.compose.material3.Typography(),
        shapes = androidx.compose.material3.Shapes(),
        content = content
    )
}
