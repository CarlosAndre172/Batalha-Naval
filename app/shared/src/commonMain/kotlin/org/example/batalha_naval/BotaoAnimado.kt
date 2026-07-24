package org.example.batalha_naval 

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp

@Composable
fun BotaoAnimado(texto: String, onClick: () -> Unit) {
    // Fonte de interações para saber o estado do botão.
    val interactionSource = remember { MutableInteractionSource() }

    // isPressed guarda se o botão está sendo pressionado naquele instante.
    val isPressed by interactionSource.collectIsPressedAsState()

    // Se estiver pressionado, diminui para 90% (0.9f). Se soltar, volta a 100% (1f).
    val scale by animateFloatAsState(targetValue = if (isPressed) 0.9f else 1f)

    Button(
        onClick = onClick,
        interactionSource = interactionSource,
        // 4. Aplicamos a escala animada ao modificador do botão
        modifier = Modifier
            .scale(scale)
            .padding(8.dp)
    ) {
        Text(texto)
    }
}