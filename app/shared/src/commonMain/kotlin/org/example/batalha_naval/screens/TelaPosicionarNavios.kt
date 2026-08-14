package org.example.batalha_naval.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import org.example.batalha_naval.components.BotaoAnimado
import org.example.batalha_naval.components.TabuleiroPosicionamento
import org.example.batalha_naval.jogo.EstadoTabuleiro
import org.example.batalha_naval.themes.palettes.*

// Tela onde o jogador monta a própria frota antes da partida começar.
// Os navios entram na ordem da lista do mapa (do maior pro menor).
@Composable
fun TelaPosicionarNavios(
    tabuleiro: EstadoTabuleiro,
    tamanhoCelula: Dp,
    onConfirmar: () -> Unit,
    onVoltarClick: () -> Unit
) {
    // Deitado (true) ou em pé (false). O botão direito do mouse inverte isso.
    var horizontal by remember(tabuleiro) { mutableStateOf(true) }

    val embarcacaoAtual = tabuleiro.proximaEmbarcacao()
    val frotaCompleta = tabuleiro.frotaCompleta()

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Posicione sua frota — ${tabuleiro.tipoMapa.nomeExibido} " +
                "(${tabuleiro.tamanho}x${tabuleiro.tamanho})",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = brancoTexto
        )

        Text(
            text = if (embarcacaoAtual != null) {
                "Navio ${tabuleiro.posicionadas.size + 1} de ${tabuleiro.embarcacoes.size} " +
                    "— ${embarcacaoAtual.tamanho} casas (${if (horizontal) "deitado" else "em pé"})"
            } else {
                "✅ Frota completa! Confirme para começar a partida."
            },
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = brancoTexto
        )

        Text(
            text = "Botão esquerdo posiciona • Botão direito gira",
            fontSize = 12.sp,
            color = brancoTexto
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Mesma moldura de madeira do tabuleiro da partida.
        Surface(
            color = marromMadeira,
            shape = RoundedCornerShape(12.dp)
        ) {
            TabuleiroPosicionamento(
                tabuleiro = tabuleiro,
                embarcacaoAtual = embarcacaoAtual,
                horizontal = horizontal,
                modifier = Modifier.padding(8.dp),
                tamanhoCelula = tamanhoCelula,
                onGirar = { horizontal = !horizontal },
                onPosicionar = { linha, coluna ->
                    embarcacaoAtual?.let { embarcacao ->
                        tabuleiro.colocar(embarcacao, linha, coluna, horizontal)
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            // Só depois que o último navio entra é que aparecem confirmar/recomeçar.
            if (frotaCompleta) {
                BotaoAnimado(
                    texto = "Confirmar",
                    corFundo = azulOceano,
                    corTexto = brancoTexto
                ) {
                    onConfirmar()
                }

                BotaoAnimado(
                    texto = "Recomeçar",
                    corFundo = marromMadeira,
                    corTexto = brancoTexto
                ) {
                    tabuleiro.limparPosicionamento()
                }
            } else {
                // Atalho pra quem não quiser usar o botão direito do mouse.
                BotaoAnimado(
                    texto = "Girar",
                    corFundo = azulOceano,
                    corTexto = brancoTexto
                ) {
                    horizontal = !horizontal
                }
            }

            BotaoAnimado(
                texto = "Voltar",
                corFundo = vermelho,
                corTexto = brancoTexto
            ) {
                onVoltarClick()
            }
        }
    }
}
