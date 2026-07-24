package org.example.batalha_naval.themes

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush

import org.example.batalha_naval.themes.palettes.AzulOceano
import org.example.batalha_naval.themes.palettes.AzulProfundo

@Composable
fun FundoGradiente(content: @Composable () -> Unit) {
    // 1. Criamos uma transição infinita (uma animação que nunca para)
    val transicaoInfinita = rememberInfiniteTransition()

    // 2. Animamos um valor de 0 até 1000, que vai e volta (Reverse) a cada 4 segundos
    val deslocamento by transicaoInfinita.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse // Vai e volta suavemente
        )
    )

    // 3. Criamos um "Pincel" Gradiente. As coordenadas mudam com base na animação!
    val gradienteAnimado = Brush.linearGradient(
        colors = listOf(AzulProfundo, AzulOceano),
        start = Offset(0f, deslocamento),
        end = Offset(1000f, 1000f - deslocamento)
    )

    // 4. Usamos o Box para colocar esse gradiente no fundo, e o conteúdo (content) por cima
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = gradienteAnimado)
    ) {
        content() // Aqui dentro vai ser desenhado o menu e o tabuleiro
    }
}