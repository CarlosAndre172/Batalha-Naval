package org.example.batalha_naval.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import org.example.batalha_naval.jogo.EstadoCelula
import org.example.batalha_naval.jogo.EstadoTabuleiro
import org.example.batalha_naval.themes.escurecer
import org.example.batalha_naval.themes.palettes.*

// A "box" com os botõezinhos: 10x10 (Oceano), 8x8 (Lagoa) ou 5x5 (Poça).
// O tamanho vem do próprio tabuleiro, então não precisamos escolher aqui.
@Composable
fun TabuleiroNaval(
    tabuleiro: EstadoTabuleiro,
    modifier: Modifier = Modifier,
    tamanhoCelula: Dp = 36.dp,
    habilitado: Boolean = true,
    onTiro: (linha: Int, coluna: Int) -> Unit
) {
    // Guarda qual casa está sendo segurada pelo mouse AGORA. Se for null, nenhuma está.
    var celulaPressionada by remember { mutableStateOf<Pair<Int, Int>?>(null) }

    Column(modifier = modifier) {
        for (linha in 0 until tabuleiro.tamanho) {
            Row {
                for (coluna in 0 until tabuleiro.tamanho) {

                    val estado = tabuleiro.estadoDe(linha, coluna)
                    val estaPressionada = celulaPressionada == (linha to coluna)

                    // Cor normal da casa, de acordo com o que já foi descoberto nela.
                    val corNormal = corDaCelula(estado)

                    BotaoCelula(
                        // Se o mouse está segurando, mostramos a versão escura da cor.
                        cor = if (estaPressionada) escurecer(corNormal) else corNormal,
                        tamanho = tamanhoCelula,
                        // Só dá pra clicar em casa que ainda não foi revelada.
                        habilitado = habilitado && estado == EstadoCelula.DESCONHECIDA,

                        // onClickDown: marca essa casa como "pressionada", o que deixa ela mais escura.
                        onClickDown = { celulaPressionada = linha to coluna },

                        // onRelease: aqui o clique VALE. Solta o escurecido e dispara o tiro,
                        // que é quem vai atualizar a cor da casa (embarcação = colorido, nada = escuro).
                        onRelease = {
                            celulaPressionada = null
                            onTiro(linha, coluna)
                        },

                        // Se arrastou pra fora, o tiro não conta: só desfazemos o escurecido.
                        onCliqueCancelado = { celulaPressionada = null },

                        conteudo = { SimboloDaCelula(estado, tamanhoCelula) }
                    )
                }
            }
        }
    }
}

// Traduz o estado da casa na cor do botão.
private fun corDaCelula(estado: EstadoCelula): Color = when (estado) {
    EstadoCelula.DESCONHECIDA -> azulOceano        // Água que ainda não foi explorada.
    EstadoCelula.AGUA -> escurecer(azulProfundo, 0.3f) // Não tinha nada: fica só escuro.
    EstadoCelula.ACERTO -> laranjaAcerto           // Tem embarcação aqui!
    EstadoCelula.AFUNDADO -> vermelhoAfundado      // A embarcação inteira já era.
}

// Desenho que aparece dentro da casa depois do tiro.
@Composable
private fun SimboloDaCelula(estado: EstadoCelula, tamanhoCelula: Dp) {
    val simbolo = when (estado) {
        EstadoCelula.ACERTO -> "🚢"
        EstadoCelula.AFUNDADO -> "💥"
        else -> ""
    }

    if (simbolo.isNotEmpty()) {
        // O tamanho da letra acompanha o tamanho da casa (metade dela).
        Text(text = simbolo, fontSize = (tamanhoCelula.value * 0.5f).sp)
    }
}
