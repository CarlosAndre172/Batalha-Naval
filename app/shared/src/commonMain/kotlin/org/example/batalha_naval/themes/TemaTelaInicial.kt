package org.example.batalha_naval.themes

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import org.example.batalha_naval.themes.palettes.PaletaBatalhaNaval

// Função de Tema customizada. 
// Ela "empacota" o MaterialTheme para esconder a "complexidade" do resto do app.
@Composable
fun BatalhaNavalTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = PaletaBatalhaNaval,
        content = content // O "content" aqui represetna a nossa tela no App.kt.
    )
}