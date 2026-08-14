package org.example.batalha_naval.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.isSecondaryPressed
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

import org.example.batalha_naval.jogo.Embarcacao
import org.example.batalha_naval.jogo.EstadoTabuleiro
import org.example.batalha_naval.themes.palettes.*

// A grade onde o jogador monta a própria frota.
//
// Diferente do TabuleiroNaval (que tem um botão por casa), aqui o mouse é escutado
// pela GRADE INTEIRA de uma vez só. É isso que deixa o navio "grudar" no cursor:
// a gente descobre em qual casa o mouse está pela posição em pixels e desenha a
// embarcação inteira a partir dali.
//
// Botão esquerdo = encaixa o navio. Botão direito = gira (deitado <-> em pé).
@Composable
fun TabuleiroPosicionamento(
    tabuleiro: EstadoTabuleiro,
    embarcacaoAtual: Embarcacao?,   // O navio da vez. Null quando a frota já está completa.
    horizontal: Boolean,            // Se o navio da vez está deitado.
    modifier: Modifier = Modifier,
    tamanhoCelula: Dp = 36.dp,
    onGirar: () -> Unit,
    onPosicionar: (linha: Int, coluna: Int) -> Unit
) {
    // Em qual casa o cursor está agora. Null = mouse fora da grade.
    var celulaSobOMouse by remember(tabuleiro) { mutableStateOf<Pair<Int, Int>?>(null) }

    // Ler o tamanho da lista observável aqui é o que faz a grade se redesenhar a
    // cada navio encaixado (a grade "secreta" em si não é observável).
    val ocupadas = remember(tabuleiro, tabuleiro.posicionadas.size) { tabuleiro.casasOcupadas() }

    // As casas que o navio da vez ocuparia se o jogador clicasse agora, e se ele cabe lá.
    val casa = celulaSobOMouse
    val previa: Set<Pair<Int, Int>> =
        if (embarcacaoAtual != null && casa != null) {
            tabuleiro.casasDoNavio(casa.first, casa.second, embarcacaoAtual.tamanho, horizontal).toSet()
        } else {
            emptySet()
        }
    val previaCabe = embarcacaoAtual != null && casa != null &&
        tabuleiro.podeColocar(casa.first, casa.second, embarcacaoAtual.tamanho, horizontal)

    Column(
        modifier = modifier.pointerInput(tabuleiro, embarcacaoAtual, horizontal) {
            awaitPointerEventScope {
                while (true) {
                    val evento = awaitPointerEvent()
                    val posicao = evento.changes.lastOrNull()?.position

                    // O lado da casa em pixels vem do tamanho medido da grade, não da
                    // conta em dp: assim a conta bate mesmo com zoom/DPI quebrado.
                    val lado = size.width.toFloat() / tabuleiro.tamanho
                    val alvo = posicao?.let { ponto ->
                        val linha = (ponto.y / lado).toInt()
                        val coluna = (ponto.x / lado).toInt()
                        if (ponto.x >= 0f && ponto.y >= 0f && tabuleiro.dentroDoTabuleiro(linha, coluna)) {
                            linha to coluna
                        } else {
                            null
                        }
                    }

                    when (evento.type) {
                        PointerEventType.Exit -> celulaSobOMouse = null

                        PointerEventType.Press -> {
                            celulaSobOMouse = alvo
                            if (evento.buttons.isSecondaryPressed) {
                                onGirar()
                            } else if (alvo != null) {
                                onPosicionar(alvo.first, alvo.second)
                            }
                            evento.changes.forEach { it.consume() }
                        }

                        else -> celulaSobOMouse = alvo
                    }
                }
            }
        }
    ) {
        for (linha in 0 until tabuleiro.tamanho) {
            Row {
                for (coluna in 0 until tabuleiro.tamanho) {
                    val cor = when {
                        (linha to coluna) in previa -> if (previaCabe) verdePosicionavel else vermelhoBloqueado
                        (linha to coluna) in ocupadas -> cinzaNavio
                        else -> azulOceano
                    }

                    Box(
                        modifier = Modifier
                            .size(tamanhoCelula)
                            .background(cor)
                            .border(1.dp, Color.Black.copy(alpha = 0.35f))
                    )
                }
            }
        }
    }
}
