package org.example.batalha_naval.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.batalha_naval.components.BotaoAnimado

@Composable
fun TelaTabuleiro(onVoltarClick: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Área do Tabuleiro de Jogo",
                fontSize = 32.sp
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            BotaoAnimado(texto = "Voltar ao Menu") {
                onVoltarClick()
            }
        }
    }
}