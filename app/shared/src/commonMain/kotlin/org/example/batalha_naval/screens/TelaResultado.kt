package org.example.batalha_naval.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Composable
fun TelaResultado() {
    // Este Box cobre a tela inteira. A cor preta com alpha cria o efeito "vidro fumê" (que deixa o fundo escurecido)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.7f)), 
        contentAlignment = Alignment.Center
    ) {
        // Nosso texto de teste bem grande e centralizado
        Text(
            text = "TESTE",
            color = Color.White,
            fontSize = 64.sp,
            fontWeight = FontWeight.Bold
        )
    }
}