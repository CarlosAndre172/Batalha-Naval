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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.TimeSource

import org.example.batalha_naval.components.*
import org.example.batalha_naval.jogo.*
import org.example.batalha_naval.jogo.bot.BotAdversario
import org.example.batalha_naval.rede.ApiClient
import org.example.batalha_naval.themes.palettes.*

@Composable
fun TelaTabuleiro(
    tipoMapa: TipoMapa = TipoMapa.OCEANO,
    nomeJogador: String = "",
    onVoltarClick: () -> Unit
) {
    // Tabuleiro do inimigo: é nele que o jogador atira.
    val tabuleiroInimigo = remember(tipoMapa) { EstadoTabuleiro(tipoMapa) }

    // Tabuleiro do jogador: é nele que o bot atira.
    val tabuleiroJogador = remember(tipoMapa) { EstadoTabuleiro(tipoMapa) }

    // De quem é a vez de atirar. Também decide qual tabuleiro aparece em foco na tela.
    var turno by remember(tipoMapa) { mutableStateOf(Turno.JOGADOR) }
    val bot = remember(tipoMapa) { BotAdversario() }

    var pontos by remember(tipoMapa) { mutableStateOf(0) }
    var combo by remember(tipoMapa) { mutableStateOf(0) }
    var totalAcertosJogador by remember(tipoMapa) { mutableStateOf(0) }
    var powerUpAtivo by remember(tipoMapa) { mutableStateOf<PowerUp?>(null) }

    val coroutineScope = rememberCoroutineScope()
    var bloqueioDeClique by remember { mutableStateOf(false) }

    // Quantas vezes ainda dá pra usar cada power-up nessa partida.
    val usosDePowerUp = remember(tipoMapa) {
        mutableStateMapOf(PowerUp.BOMBA to 1, PowerUp.BOMBARDEIO to 1)
    }

    val inicioPartida = remember(tipoMapa) { TimeSource.Monotonic.markNow() }
    var scoreEnviado by remember(tipoMapa) { mutableStateOf(false) }

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
        if (turno != Turno.JOGADOR) return

        var acertouAlgumaCoisa = false

        alvosDoTiro(linha, coluna).forEach { (l, c) ->
            when (tabuleiroInimigo.atacar(l, c)) {
                ResultadoTiro.ACERTOU -> {
                    acertouAlgumaCoisa = true
                    combo++
                    totalAcertosJogador++
                    pontos += 100 * multiplicadorDoCombo(combo)
                }
                ResultadoTiro.AFUNDOU -> {
                    acertouAlgumaCoisa = true
                    combo++
                    totalAcertosJogador++
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

        // Errar o tiro passa a vez pro bot; o combo zera (e a caixinha some).
        if (!acertouAlgumaCoisa) {
            combo = 0
            bloqueioDeClique = true

            coroutineScope.launch {
                delay(1500) // Congela por 1,5 segundos para ver a água
                turno = Turno.BOT // agora passa a vez para o Bot
                bloqueioDeClique = false // Destrava o tabuleiro
            }
        }
    }

    // Enquanto for a vez do bot, ele atira sozinho no tabuleiro do jogador até errar.
    LaunchedEffect(turno, tipoMapa) {
        while (turno == Turno.BOT && !tabuleiroJogador.acabou() && !tabuleiroInimigo.acabou()) {
            delay(1000)
            val (linha, coluna) = bot.escolherTiro(tipoMapa.tamanho)
            val resultado = tabuleiroJogador.atacar(linha, coluna)
            bot.registrarResultado(linha to coluna, resultado, tipoMapa.tamanho)

            if (resultado == ResultadoTiro.AGUA) {
                turno = Turno.JOGADOR
            }
        }
    }

    // Ao afundar a frota inimiga, manda o resultado da partida pro servidor (uma vez só).
    LaunchedEffect(tabuleiroInimigo.acabou()) {
        if (tabuleiroInimigo.acabou() && !scoreEnviado) {
            scoreEnviado = true
            val tempoSegundos = inicioPartida.elapsedNow().inWholeSeconds.toInt()
            val score = calcularScoreFinal(
                acertos = totalAcertosJogador,
                naviosVivos = tabuleiroJogador.celulasDeNaviosRestantes(),
                tempoSegundos = tempoSegundos,
                tipoMapa = tipoMapa
            )
            ApiClient.salvarPartida(nomeJogador, score, tempoSegundos, tipoMapa)
        }
    }

    // Os tabuleiros se intercalam: na vez do jogador, mostra o mar do inimigo pra
    // atacar; na vez do bot, mostra o próprio mar recebendo os tiros dele.
    val tabuleiroEmFoco = if (turno == Turno.JOGADOR) tabuleiroInimigo else tabuleiroJogador

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

            Text(
                text = if (turno == Turno.JOGADOR) "🎯 Sua vez!" else "⏳ Vez do inimigo...",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = brancoTexto
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Moldura de madeira em volta da grade de botões. Alterna entre o mar do
            // inimigo (vez do jogador) e o mar do jogador (vez do bot).
            Surface(
                color = marromMadeira,
                shape = RoundedCornerShape(12.dp)
            ) {
                TabuleiroNaval(
                    tabuleiro = tabuleiroEmFoco,
                    modifier = Modifier.padding(8.dp),
                    tamanhoCelula = tamanhoCelula,
                    habilitado = turno == Turno.JOGADOR && !tabuleiroInimigo.acabou() && !bloqueioDeClique,
                    onTiro = { linha, coluna -> atirar(linha, coluna) }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (tabuleiroInimigo.acabou()) {
                Text(
                    text = "🏴 Frota inimiga afundada! Você venceu!",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = brancoTexto
                )
            } else if (tabuleiroJogador.acabou()) {
                Text(
                    text = "☠️ Sua frota afundou! Você perdeu.",
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
