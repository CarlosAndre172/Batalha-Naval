package org.example.batalha_naval.screens

import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.TimeSource

import org.example.batalha_naval.audio.EfeitosSonoros
import org.example.batalha_naval.components.*
import org.example.batalha_naval.jogo.*
import org.example.batalha_naval.jogo.bot.BotAdversario
import org.example.batalha_naval.rede.ApiClient
import org.example.batalha_naval.themes.palettes.*
import org.example.batalha_naval.components.TelaResultado

@Composable
fun TelaTabuleiro(
    tipoMapa: TipoMapa = TipoMapa.OCEANO,
    nomeJogador: String = "",
    modoPosicionamento: ModoPosicionamento = ModoPosicionamento.ALEATORIO,
    onVoltarClick: () -> Unit
) {
    // Tabuleiro do inimigo: é nele que o jogador atira. A frota do bot é sempre sorteada.
    val tabuleiroInimigo = remember(tipoMapa) { EstadoTabuleiro(tipoMapa) }

    // Tabuleiro do jogador: é nele que o bot atira. No modo manual ele nasce vazio,
    // porque quem coloca os navios é o jogador, na tela de posicionamento.
    val tabuleiroJogador = remember(tipoMapa, modoPosicionamento) {
        EstadoTabuleiro(tipoMapa, sortearNoInicio = modoPosicionamento == ModoPosicionamento.ALEATORIO)
    }

    // De quem é a vez de atirar.
    var turno by remember(tipoMapa) { mutableStateOf(Turno.JOGADOR) }
    val bot = remember(tipoMapa) { BotAdversario() }

    // A soma de todas as jogadas certeiras do jogador, cada uma já multiplicada pelo
    // combo do momento. É a "pontuacaoFinalAcertos" que a calculadora usa no fim.
    var pontuacaoFinalAcertos by remember(tipoMapa) { mutableStateOf(0) }
    var combo by remember(tipoMapa) { mutableStateOf(0) }
    var comboMaximo by remember(tipoMapa) { mutableStateOf(0) }
    var powerUpAtivo by remember(tipoMapa) { mutableStateOf<PowerUp?>(null) }

    val coroutineScope = rememberCoroutineScope()
    var bloqueioDeClique by remember { mutableStateOf(false) }

    // Estados visuais para a animação e marcação do bot
    var ultimaJogadaBot by remember(tipoMapa) { mutableStateOf<Pair<Int, Int>?>(null) }
    var raioCirculoBot by remember(tipoMapa) { mutableStateOf(0f) }
    var alfaCirculoBot by remember(tipoMapa) { mutableStateOf(1f) }
    var casaErroBotVermelha by remember(tipoMapa) { mutableStateOf<Pair<Int, Int>?>(null) }

    // Quantas vezes ainda dá pra usar cada power-up nessa partida.
    val usosDePowerUp = remember(tipoMapa) {
        mutableStateMapOf(PowerUp.BOMBA to 1, PowerUp.BOMBARDEIO to 1)
    }

    // O cronômetro do score só começa a valer quando a partida começa de verdade,
    // por isso ele é reiniciado ao confirmar o posicionamento manual.
    var inicioPartida by remember(tipoMapa) { mutableStateOf(TimeSource.Monotonic.markNow()) }
    var scoreEnviado by remember(tipoMapa) { mutableStateOf(false) }

    // Tabuleiro pequeno = casas maiores, pra ocupar mais ou menos o mesmo espaço na tela.
    val tamanhoCelula = when (tipoMapa.tamanho) {
        5 -> 56.dp
        8 -> 42.dp
        else -> 34.dp
    }

    // No modo manual, a partida só aparece depois que o jogador monta a frota dele.
    var posicionandoFrota by remember(tipoMapa, modoPosicionamento) {
        mutableStateOf(modoPosicionamento == ModoPosicionamento.MANUAL)
    }
    
    if (posicionandoFrota) {
        TelaPosicionarNavios(
            tabuleiro = tabuleiroJogador,
            tamanhoCelula = tamanhoCelula,
            onConfirmar = {
                posicionandoFrota = false
                inicioPartida = TimeSource.Monotonic.markNow()
            },
            onVoltarClick = onVoltarClick
            )
            return
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

    // Função de tiro do Jogador
    fun atirar(linha: Int, coluna: Int) {
        if (turno != Turno.JOGADOR) return

        var acertouAlgumaCoisa = false
        var caiuNaAgua = false

        alvosDoTiro(linha, coluna).forEach { (l, c) ->
            when (tabuleiroInimigo.atacar(l, c)) {
                // Acertar e afundar pontuam igual: o que muda a conta é o combo.
                ResultadoTiro.ACERTOU, ResultadoTiro.AFUNDOU -> {
                    acertouAlgumaCoisa = true
                    combo++
                    if (combo > comboMaximo) comboMaximo = combo
                    // A pontuação da jogada entra no acumulado na hora, já com o combo.
                    pontuacaoFinalAcertos += pontosDaJogada(tipoMapa, combo)
                }
                ResultadoTiro.AGUA -> caiuNaAgua = true
                ResultadoTiro.JA_ATACADA -> { /* não ganha nada */ }
            }
        }

        // Um som por tiro, mesmo quando o power-up atinge várias casas de uma vez:
        // se pegou navio em alguma delas, toca a explosão; senão, o splash.
        if (acertouAlgumaCoisa) {
            EfeitosSonoros.tocarExplosao()
        } else if (caiuNaAgua) {
            EfeitosSonoros.tocarAgua()
        }

        // Se um power-up estava armado, ele foi gasto neste tiro.
        powerUpAtivo?.let { powerUp ->
            usosDePowerUp[powerUp] = (usosDePowerUp[powerUp] ?: 1) - 1
        }
        powerUpAtivo = null

        if (!acertouAlgumaCoisa) {
            combo = 0
            bloqueioDeClique = true

            coroutineScope.launch {
                delay(1500)
                turno = Turno.BOT
                bloqueioDeClique = false
            }
        }
    }

    // Turno do Bot, faz a animação de círculo e também a marcação (de vermelho).
    LaunchedEffect(turno, tipoMapa) {
        while (turno == Turno.BOT && !tabuleiroJogador.acabou() && !tabuleiroInimigo.acabou()) {
            // Quando a tela volta para o bot, limpamos o erro vermelho anterior
            casaErroBotVermelha = null
            delay(1000) // Bot "pensando"

            // Bot escolhe o alvo
            val (linha, coluna) = bot.escolherTiro(tipoMapa.tamanho)
            
            // Esolhe a casa e ataca.
            val resultado = tabuleiroJogador.atacar(linha, coluna)
            bot.registrarResultado(linha to coluna, resultado, tipoMapa.tamanho)
            
            
            when (resultado) {
                ResultadoTiro.ACERTOU, ResultadoTiro.AFUNDOU -> EfeitosSonoros.tocarExplosao()
                ResultadoTiro.AGUA -> EfeitosSonoros.tocarAgua()
            ResultadoTiro.JA_ATACADA -> { /* sem som */ }
        }
        
            // Se errou, a casa do tabuleiro fica vermelha
            if (resultado == ResultadoTiro.AGUA) {
                casaErroBotVermelha = linha to coluna
            }

            // Dispara a animação do círculo
            ultimaJogadaBot = linha to coluna
            for (i in 1..8) {
                raioCirculoBot = i * (tamanhoCelula.value / 3f)
                alfaCirculoBot = 1f - (i / 8f)
                delay(35)
            }
            ultimaJogadaBot = null // Limpa o objeto do círculo para liberar memória

            // Se o bot errou, faz a pausa e devolve o turno para o jogador
            if (resultado == ResultadoTiro.AGUA) {
                delay(1500) // Pausa para o jogador absorver a jogada
                casaErroBotVermelha = null // Limpa a marcação vermelha
                turno = Turno.JOGADOR // Sua vez!
            }
        }
    }

    // A partida acabou quando uma das duas frotas afundou inteira.
    val partidaAcabou = tabuleiroInimigo.acabou() || tabuleiroJogador.acabou()

    // Congela o resultado no instante em que a partida termina: o relógio para de
    // correr e a mesma conta serve pra tela de resultado e pro ranking.
    var resultadoFinal by remember(tipoMapa) { mutableStateOf<DetalheDaPontuacao?>(null) }
    var tempoFinalSegundos by remember(tipoMapa) { mutableStateOf(0) }

    LaunchedEffect(partidaAcabou) {
        if (partidaAcabou && !scoreEnviado) {
            scoreEnviado = true

            val tempoSegundos = inicioPartida.elapsedNow().inWholeSeconds.toInt()
            val detalhe = detalharScoreFinal(
                pontuacaoFinalAcertos = pontuacaoFinalAcertos,
                naviosVivos = tabuleiroJogador.celulasDeNaviosRestantes(),
                tempoSegundos = tempoSegundos,
                tipoMapa = tipoMapa
            )

            tempoFinalSegundos = tempoSegundos
            resultadoFinal = detalhe

            // Vitória ou derrota, a partida vai pro ranking: o servidor só guarda a
            // pontuação se ela bater o recorde anterior do jogador naquele mar.
            ApiClient.salvarPartida(nomeJogador, detalhe.pontuacaoFinal, tempoSegundos, tipoMapa)
        }
    }

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
            if (combo >= 2) {
                IndicadorCombo(combo = combo, modifier = Modifier.width(140.dp))
            }
        }

        // ---------- MEIO: o tabuleiro com sobreposição de efeitos ----------
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

            Surface(
                color = marromMadeira,
                shape = RoundedCornerShape(12.dp)
            ) {
                // Usamos uma Box dentro da moldura para desenhar os efeitos visuais por cima do tabuleiro
                Box(modifier = Modifier.padding(8.dp)) {
                    TabuleiroNaval(
                        tabuleiro = tabuleiroEmFoco,
                        modifier = Modifier.align(Alignment.Center),
                        tamanhoCelula = tamanhoCelula,
                        habilitado = turno == Turno.JOGADOR && !tabuleiroInimigo.acabou() && !bloqueioDeClique,
                        onTiro = { linha, coluna -> atirar(linha, coluna) }
                    )

                    // Canvas para desenhar a marcação vermelha e o círculo animado do bot
                    if (turno == Turno.BOT || casaErroBotVermelha != null) {
                        Canvas(modifier = Modifier.matchParentSize()) {
                            val cellPx = tamanhoCelula.toPx()

                            // Desenha a marcação vermelha se o bot errou nesta célula
                            casaErroBotVermelha?.let { (l, c) ->
                                val x = c * cellPx
                                val y = l * cellPx
                                drawRect(
                                    color = Color.Red.copy(alpha = 0.5f),
                                    topLeft = androidx.compose.ui.geometry.Offset(x, y),
                                    size = androidx.compose.ui.geometry.Size(cellPx, cellPx)
                                )
                            }

                            // Desenha a circunferência amarela expandindo e sumindo
                            ultimaJogadaBot?.let { (l, c) ->
                                val centerX = (c * cellPx) + (cellPx / 2f)
                                val centerY = (l * cellPx) + (cellPx / 2f)
                                if (raioCirculoBot > 0f) {
                                    drawCircle(
                                        color = Color.Yellow.copy(alpha = alfaCirculoBot.coerceIn(0f, 1f)),
                                        radius = raioCirculoBot,
                                        center = androidx.compose.ui.geometry.Offset(centerX, centerY),
                                        style = Stroke(width = 3.dp.toPx())
                                    )
                                }
                            }
                        }
                    }
                }
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
                // Durante a partida o placar mostra o acumulado dos acertos; os bônus
                // de sobrevivência e a penalidade de tempo entram só no fim.
                pontos = pontuacaoFinalAcertos,
                embarcacoesAfundadas = tabuleiroInimigo.afundadas.size,
                totalDeEmbarcacoes = tabuleiroInimigo.embarcacoes.size,
                modifier = Modifier.width(160.dp)
            )

            BotoesPowerUp(
                powerUpAtivo = powerUpAtivo,
                usosRestantes = { powerUp -> usosDePowerUp[powerUp] ?: 0 },
                onSelecionar = { powerUp ->
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

    // Pra mostrar a tela de resultado quando o jogo termina. Só aparece depois que o
    // LaunchedEffect acima fechou a conta, então os números nunca saem pela metade.
    resultadoFinal?.let { detalhe ->
        Box(modifier = Modifier.fillMaxSize()) {
            TelaResultado(
                vitoria = tabuleiroInimigo.acabou(),
                nomeJogador = nomeJogador,
                tipoMapa = tipoMapa,
                embarcacoesDerrubadas = tabuleiroInimigo.afundadas.size,
                embarcacoesDanificadas = tabuleiroInimigo.embarcacoesDanificadas(),
                casasDeNavioIntactas = tabuleiroJogador.celulasDeNaviosRestantes(),
                comboMaximo = comboMaximo,
                powerUpsRestantes = usosDePowerUp.values.sum(),
                tempoSegundos = tempoFinalSegundos,
                detalhe = detalhe,
                onVoltarMenu = onVoltarClick
            )
        }
    }
}