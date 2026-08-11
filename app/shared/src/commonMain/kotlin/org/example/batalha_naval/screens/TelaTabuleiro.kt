package org.example.batalha_naval.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import org.example.batalha_naval.components.*
import org.example.batalha_naval.jogo.*
import org.example.batalha_naval.themes.palettes.*

@Composable
fun TelaTabuleiro(
    tipoMapa: TipoMapa = TipoMapa.OCEANO,
    onVoltarClick: () -> Unit
) {
    // Tabuleiro do inimigo: é nele que o jogador atira.
    val tabuleiroInimigo = remember(tipoMapa) { EstadoTabuleiro(tipoMapa) }

    // Tabuleiro do jogador: por enquanto só serve pra mostrar a sua frota na caixinha lateral.
    //! Quando o inimigo souber atirar, é aqui que os tiros dele vão cair.
    val tabuleiroJogador = remember(tipoMapa) { EstadoTabuleiro(tipoMapa) }

    var pontos by remember(tipoMapa) { mutableStateOf(0) }
    var combo by remember(tipoMapa) { mutableStateOf(0) }
    var powerUpAtivo by remember(tipoMapa) { mutableStateOf<PowerUp?>(null) }

    // Quantas vezes ainda dá pra usar cada power-up nessa partida.
    val usosDePowerUp = remember(tipoMapa) {
        mutableStateMapOf(PowerUp.BOMBA to 1, PowerUp.BOMBARDEIO to 1)
    }

    // Tabuleiro pequeno = casas maiores, pra ocupar mais ou menos o mesmo espaço na tela.
    val tamanhoCelula = when (tipoMapa.tamanho) {
        5 -> 56.dp
        8 -> 42.dp
        else -> 34.dp
    }

    // Quais casas o tiro vai atingir. Sem power-up, é só a casa clicada.
    fun alvosDoTiro(linha: Int, coluna: Int): List<Pair<Int, Int>> {
        val alvos = when (powerUpAtivo) {
            PowerUp.BOMBA -> (linha - 1..linha + 1).flatMap { l -> (coluna - 1..coluna + 1).map { c -> l to c } }
            PowerUp.BOMBARDEIO -> (0 until tipoMapa.tamanho).map { c -> linha to c }
            null -> listOf(linha to coluna)
        }
        return alvos.filter { (l, c) -> tabuleiroInimigo.dentroDoTabuleiro(l, c) }
    }

    // É esta função que o onRelease do botãozinho chama.
    fun atirar(linha: Int, coluna: Int) {
        var acertouAlgumaCoisa = false

        alvosDoTiro(linha, coluna).forEach { (l, c) ->
            when (tabuleiroInimigo.atacar(l, c)) {
                ResultadoTiro.ACERTOU -> {
                    acertouAlgumaCoisa = true
                    combo++
                    pontos += 100 * multiplicadorDoCombo(combo)
                }
                ResultadoTiro.AFUNDOU -> {
                    acertouAlgumaCoisa = true
                    combo++
                    pontos += 250 * multiplicadorDoCombo(combo)
                }
                ResultadoTiro.AGUA, ResultadoTiro.JA_ATACADA -> { /* não ganha nada */ }
            }
        }

        // Se um power-up estava armado, ele foi gasto neste tiro.
        powerUpAtivo?.let { powerUp ->
            usosDePowerUp[powerUp] = (usosDePowerUp[powerUp] ?: 1) - 1
        }
        powerUpAtivo = null

        // Errar o tiro acaba com o turno do jogador, então o combo zera (e a caixinha some).
        if (!acertouAlgumaCoisa) combo = 0
    }

    Row(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        // ---------- LADO ESQUERDO: o combo ----------
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.Center
        ) {
            // A caixinha do combo só existe a partir de 2 acertos seguidos.
            if (combo >= 2) {
                IndicadorCombo(combo = combo, modifier = Modifier.width(140.dp))
            }
        }

        // ---------- MEIO: o tabuleiro ----------
        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            Text(
                text = "Mar: ${tipoMapa.nomeExibido} (${tipoMapa.tamanho}x${tipoMapa.tamanho})",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = brancoTexto
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Moldura de madeira em volta da grade de botões.
            Surface(
                color = marromMadeira,
                shape = RoundedCornerShape(12.dp)
            ) {
                TabuleiroNaval(
                    tabuleiro = tabuleiroInimigo,
                    modifier = Modifier.padding(8.dp),
                    tamanhoCelula = tamanhoCelula,
                    habilitado = !tabuleiroInimigo.acabou(),
                    onTiro = { linha, coluna -> atirar(linha, coluna) }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (tabuleiroInimigo.acabou()) {
                Text(
                    text = "🏴 Frota inimiga afundada!",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = brancoTexto
                )
            }

            BotaoAnimado(
                texto = "Voltar",
                corFundo = vermelho,
                corTexto = brancoTexto
            ) {
                onVoltarClick()
            }
        }

        // ---------- LADO DIREITO: pontos, power-ups e embarcações ----------
        Column(
            modifier = Modifier.weight(1f).padding(start = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CaixaPontos(
                pontos = pontos,
                embarcacoesAfundadas = tabuleiroInimigo.afundadas.size,
                totalDeEmbarcacoes = tabuleiroInimigo.embarcacoes.size,
                modifier = Modifier.width(160.dp)
            )

            BotoesPowerUp(
                powerUpAtivo = powerUpAtivo,
                usosRestantes = { powerUp -> usosDePowerUp[powerUp] ?: 0 },
                onSelecionar = { powerUp ->
                    // Clicar de novo no mesmo power-up desarma ele.
                    powerUpAtivo = if (powerUpAtivo == powerUp) null else powerUp
                },
                modifier = Modifier.width(160.dp)
            )

            CaixaEmbarcacoes(
                embarcacoesJogador = tabuleiroJogador.embarcacoes,
                afundadasDoJogador = tabuleiroJogador.afundadas,
                embarcacoesInimigo = tabuleiroInimigo.embarcacoes,
                afundadasDoInimigo = tabuleiroInimigo.afundadas,
                modifier = Modifier.width(160.dp)
            )
        }
    }
}

// A partir de 2 acertos seguidos, o combo vira multiplicador de pontos.
private fun multiplicadorDoCombo(combo: Int): Int = if (combo >= 2) combo else 1
