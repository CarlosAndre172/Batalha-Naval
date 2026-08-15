package org.example.batalha_naval.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import org.example.batalha_naval.themes.palettes.*

// Caixinha do canto superior direito: mostra a pontuação atual
// e uma barrinha com um quadradinho pra cada embarcação inimiga.
@Composable
fun CaixaPontos(
    pontos: Int,
    embarcacoesAfundadas: Int,
    totalDeEmbarcacoes: Int,
    modifier: Modifier = Modifier
) {
    CaixaLateral(titulo = "PONTOS", modifier = modifier) {

        Text(
            text = "$pontos",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = azulProfundo
        )

        Spacer(modifier = Modifier.height(6.dp))

        // A barrinha do desenho: cada quadradinho "acende" quando uma embarcação afunda.
        Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
            repeat(totalDeEmbarcacoes) { indice ->
                Box(
                    modifier = Modifier
                        .size(14.dp)
                        .background(
                            if (indice < embarcacoesAfundadas) laranjaAcerto
                            else cinza.copy(alpha = 0.35f)
                        )
                        .border(1.dp, azulProfundo)
                )
            }
        }
    }
}
