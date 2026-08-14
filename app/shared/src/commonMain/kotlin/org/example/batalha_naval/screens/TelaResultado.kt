package org.example.batalha_naval.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import org.example.batalha_naval.themes.palettes.*

@Composable
fun TelaResultado(
    vitoria: Boolean,
    nomeJogador: String,
    embarcacoesDerrubadas: Int,
    embarcacoesDanificadas: Int,
    embarcacoesSobreviventes: Int,
    comboMaximo: Int,
    powerUpsRestantes: Int,
    pontuacaoTotal: Int,
    onVoltarMenu: () -> Unit
) {
    // Fundo fumê que cobre o tabuleiro e bloqueia cliques acidentais nos navios de trás
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.75f))
            .clickable(enabled = false) {}, 
        contentAlignment = Alignment.Center
    ) {
        Surface(
            color = Color(0xFF1E1E1E), // Um cinza bem escuro
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.width(600.dp).padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Título de Vitória ou Derrota
                Text(
                    text = if (vitoria) "VITÓRIA!" else "DERROTA!",
                    color = if (vitoria) Color(0xFF4CAF50) else Color(0xFFF44336), // Verde ou Vermelho
                    fontSize = 36.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "Player: $nomeJogador",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Tabela de resultados
                Column(modifier = Modifier.fillMaxWidth()) {
                    // Valores fictícios de multiplicadores (adicionar)
                     LinhaEstatistica(
                        label = "Número embarcações derrubadas:",
                        calculo = "$embarcacoesDerrubadas * 200",
                        total = "${embarcacoesDerrubadas * 200}"
                    )
                    LinhaEstatistica(
                        label = "Número embarcações danificadas:",
                        calculo = "$embarcacoesDanificadas",
                        total = "" // Danificadas não dá ponto
                    )
                    LinhaEstatistica(
                        label = "Número embarcações sobreviventes:",
                        calculo = "$embarcacoesSobreviventes * 500",
                        total = "${embarcacoesSobreviventes * 500}"
                    )
                    LinhaEstatistica(
                        label = "Combo máx:",
                        calculo = "$comboMaximo * 30",
                        total = "${comboMaximo * 30}"
                    )
                    LinhaEstatistica(
                        label = "Power-ups restantes:",
                        calculo = "$powerUpsRestantes * 5000",
                        total = "${powerUpsRestantes * 5000}"
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
                
                // Linha divisória antes do total
                Box(modifier = Modifier.fillMaxWidth().height(2.dp).background(Color.White.copy(alpha = 0.2f)))

                Spacer(modifier = Modifier.height(16.dp))
                //pontuação total
                Text(
                    text = "$pontuacaoTotal",
                    color = Color(0xFFFFD700), // Dourado estranho
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.End // Alinhado na direita
                )

                Spacer(modifier = Modifier.height(32.dp))

                BotaoAnimado(
                    texto = "Voltar ao Menu",
                    corFundo = Color(0xFF2196F3),
                    corTexto = Color.White
                ) {
                    onVoltarMenu()
                }
            }
        }
    }
}

// Função para alinhar a tabela
@Composable
private fun LinhaEstatistica(label: String, calculo: String, total: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Label na esquerda (ocupa o espaço que sobrar)
        Text(
            text = label,
            color = Color.White,
            fontSize = 16.sp,
            modifier = Modifier.weight(1f)
        )

        // Cálculo no meio, cor cinza clara, tamanho fixo para alinhar
        Text(
            text = calculo,
            color = Color.LightGray,
            fontSize = 16.sp,
            textAlign = TextAlign.Start,
            modifier = Modifier.width(100.dp)
        )

        // Total na direita, dourado e em negrito, tamanho fixo para alinhar
        Text(
            text = total,
            color = Color(0xFFFFD700), // Dourado
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.End,
            modifier = Modifier.width(60.dp)
        )
    }
}