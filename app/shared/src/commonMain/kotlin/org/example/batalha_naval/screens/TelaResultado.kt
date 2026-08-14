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

import org.example.batalha_naval.jogo.DetalheDaPontuacao
import org.example.batalha_naval.jogo.PENALIDADE_POR_SEGUNDO
import org.example.batalha_naval.jogo.TipoMapa
import org.example.batalha_naval.jogo.pesoDaSobrevivencia
import org.example.batalha_naval.themes.palettes.*

// Placar do fim da partida. Os números da tabela NÃO são calculados aqui: eles vêm
// prontos da CalculadoraDePontuacao, via TelaTabuleiro, pra tela e banco de dados
// mostrarem exatamente a mesma pontuação.
@Composable
fun TelaResultado(
    vitoria: Boolean,
    nomeJogador: String,
    tipoMapa: TipoMapa,
    embarcacoesDerrubadas: Int,
    embarcacoesDanificadas: Int,
    casasDeNavioIntactas: Int,
    comboMaximo: Int,
    powerUpsRestantes: Int,
    tempoSegundos: Int,
    detalhe: DetalheDaPontuacao,
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

                // Tabela de resultados: só as três primeiras linhas viram pontos,
                // as de baixo são informativas.
                Column(modifier = Modifier.fillMaxWidth()) {
                    LinhaEstatistica(
                        label = "Acertos (já com o combo):",
                        calculo = "soma das jogadas",
                        total = "${detalhe.pontuacaoFinalAcertos}"
                    )
                    LinhaEstatistica(
                        label = "Casas de navio intactas:",
                        calculo = "$casasDeNavioIntactas * ${pesoDaSobrevivencia(tipoMapa)}",
                        total = "${detalhe.bonusSobrevivencia}"
                    )
                    LinhaEstatistica(
                        label = "Tempo de partida:",
                        calculo = "${tempoSegundos}s * $PENALIDADE_POR_SEGUNDO",
                        total = "-${detalhe.penalidadeTempo}"
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    LinhaEstatistica(
                        label = "Embarcações inimigas derrubadas:",
                        calculo = "$embarcacoesDerrubadas",
                        total = "" // Já contabilizadas nos acertos.
                    )
                    LinhaEstatistica(
                        label = "Embarcações inimigas danificadas:",
                        calculo = "$embarcacoesDanificadas",
                        total = ""
                    )
                    LinhaEstatistica(
                        label = "Combo máx:",
                        calculo = "x$comboMaximo",
                        total = ""
                    )
                    LinhaEstatistica(
                        label = "Power-ups restantes:",
                        calculo = "$powerUpsRestantes",
                        total = ""
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
                
                // Linha divisória antes do total
                Box(modifier = Modifier.fillMaxWidth().height(2.dp).background(Color.White.copy(alpha = 0.2f)))

                Spacer(modifier = Modifier.height(16.dp))
                // Pontuação total: exatamente o que a CalculadoraDePontuacao devolveu
                // e o que foi enviado para o ranking.
                Text(
                    text = "${detalhe.pontuacaoFinal}",
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