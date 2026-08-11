package org.example.batalha_naval.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// Botão QUADRADO e SEM padding nenhum, feito pra formar a grade do mar.
// Ele não decide nada sozinho: quem manda é quem usa o botão, passando a cor e as funções.
@Composable
fun BotaoCelula(
    cor: Color,                              // Cor atual do botão (muda a qualquer momento).
    modifier: Modifier = Modifier,
    tamanho: Dp = 36.dp,                     // Mesmo valor pra largura e altura = quadrado.
    habilitado: Boolean = true,              // Se for false, o botão para de responder ao mouse.
    corBorda: Color = Color.Black.copy(alpha = 0.35f),
    espessuraBorda: Dp = 1.dp,
    onClickDown: () -> Unit = {},            // Chamada assim que o mouse APERTA (e enquanto segura).
    onRelease: () -> Unit = {},              // Chamada quando o mouse é SOLTO em cima do botão.
    onCliqueCancelado: () -> Unit = {},      // Chamada se arrastar pra fora e soltar (serve pra desfazer o escurecido).
    conteudo: @Composable () -> Unit = {}    // Desenho de dentro (a embarcação, a explosão...).
) {
    Box(
        modifier = modifier
            .size(tamanho)          // Quadrado.
            .background(cor)        // A cor vem de fora, então dá pra trocar quando quiser.
            .border(espessuraBorda, corBorda)
            // Aqui é onde o botão "escuta" o mouse. Usamos gesto em vez de Button
            // porque precisamos separar o apertar (down) do soltar (release).
            .pointerInput(habilitado) {
                if (!habilitado) return@pointerInput

                detectTapGestures(
                    onPress = {
                        onClickDown()
                        // tryAwaitRelease() espera o mouse soltar.
                        // Devolve true se soltou em cima do botão e false se arrastou pra fora.
                        val soltouEmCima = tryAwaitRelease()
                        if (soltouEmCima) onRelease() else onCliqueCancelado()
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        conteudo()
    }
}
