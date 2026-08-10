package org.example.batalha_naval.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

import org.example.batalha_naval.themes.palettes.*

// Caixinha do canto esquerdo. Quem decide se ela aparece é a tela:
// só é desenhada a partir de 2 acertos seguidos e some quando o turno acaba.
@Composable
fun IndicadorCombo(combo: Int, modifier: Modifier = Modifier) {
    CaixaLateral(titulo = "COMBO", modifier = modifier) {

        Text(
            text = "${combo}x",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = laranjaAcerto
        )

        Text(
            text = "acertos seguidos",
            fontSize = 10.sp,
            color = azulProfundo
        )
    }
}
