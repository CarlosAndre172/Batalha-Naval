package org.example.batalha_naval.themes

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp

// Mistura a cor com preto pra deixá-la mais escura.
// O "fator" vai de 0f (cor original) até 1f (preto puro).
fun escurecer(cor: Color, fator: Float = 0.35f): Color = lerp(cor, Color.Black, fator)
