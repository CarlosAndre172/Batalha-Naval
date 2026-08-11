package org.example.batalha_naval.components

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.ButtonDefaults

@Composable
fun BotaoAnimado(    
        texto: String, 
        corFundo: Color = MaterialTheme.colorScheme.primary, 
        corTexto: Color = MaterialTheme.colorScheme.onPrimary,
        habilitado: Boolean = true, // Quando é false, o botão fica apagado e não responde ao clique.
        modifier: Modifier = Modifier.padding(8.dp),
        onClick: () -> Unit
    ) {
        // Fonte de interações para saber o estado do botão.
        val interactionSource = remember { MutableInteractionSource() }

        // isPressed guarda se o botão está sendo pressionado naquele instante.
        val isPressed by interactionSource.collectIsPressedAsState()

        // Se estiver pressionado, diminui para 90% (0.9f). Se soltar, volta a 100% (1f).
        val scale by animateFloatAsState(targetValue = if (isPressed) 0.9f else 1f)

        Button(
        onClick = onClick,
        enabled = habilitado,
        interactionSource = interactionSource,
        // Cores do botão e do texto, que podem ser passadas como parâmetros.
        colors = ButtonDefaults.buttonColors(
            containerColor = corFundo,
            contentColor = corTexto,
            disabledContainerColor = corFundo.copy(alpha = 0.5f),
            disabledContentColor = corTexto.copy(alpha = 0.7f)
        ),
        // Animação da escala do botão
        modifier = modifier.scale(scale)
    ) {
        Text(texto)
    }
}