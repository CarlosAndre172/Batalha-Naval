package org.example.batalha_naval.jogo

// Port de src/service/VerificacaoPartida.kt: pesos de acerto/sobrevivência por
// mapa, penalidade por tempo, resultado nunca fica negativo.
//
// A pontuação da partida é montada em duas etapas:
//   1. A CADA jogada certeira do jogador somamos pontosDaJogada(), que já leva o
//      combo em conta. O acumulado disso é a "pontuacaoFinalAcertos".
//   2. No fim da partida, essa pontuacaoFinalAcertos entra em detalharScoreFinal()
//      junto com o bônus de sobrevivência e a penalidade de tempo.

// Quanto vale um acerto em cada mar.
fun pesoDoAcerto(tipoMapa: TipoMapa): Int = when (tipoMapa) {
    TipoMapa.POCA -> 50
    TipoMapa.LAGOA -> 100
    TipoMapa.OCEANO -> 150
}

// Quanto vale cada casa de navio que sobreviveu no fim da partida.
fun pesoDaSobrevivencia(tipoMapa: TipoMapa): Int = when (tipoMapa) {
    TipoMapa.POCA -> 100
    TipoMapa.LAGOA -> 200
    TipoMapa.OCEANO -> 300
}

// Cada segundo de partida desconta isso do total.
const val PENALIDADE_POR_SEGUNDO = 2

// A partir de 2 acertos seguidos, o combo vira multiplicador de pontos.
fun multiplicadorDoCombo(combo: Int): Int = if (combo >= 2) combo else 1

// Os pontos de UMA jogada certeira, já com o combo aplicado. O jogo chama isso a
// cada acerto e vai somando o resultado na pontuacaoFinalAcertos da partida.
fun pontosDaJogada(tipoMapa: TipoMapa, combo: Int): Int =
    pesoDoAcerto(tipoMapa) * multiplicadorDoCombo(combo)

// A conta final aberta em parcelas, pra tela de resultado poder mostrar de onde
// veio cada pedaço da pontuação.
data class DetalheDaPontuacao(
    val pontuacaoFinalAcertos: Int, // Soma de todas as jogadas certeiras (com combo).
    val bonusSobrevivencia: Int,    // Casas de navio que sobraram × peso do mapa.
    val penalidadeTempo: Int,       // Quanto o relógio custou.
    val pontuacaoFinal: Int         // O total, nunca negativo.
)

fun detalharScoreFinal(
    pontuacaoFinalAcertos: Int,
    naviosVivos: Int,
    tempoSegundos: Int,
    tipoMapa: TipoMapa
): DetalheDaPontuacao {
    val bonusDefesa = naviosVivos * pesoDaSobrevivencia(tipoMapa)
    val pontosPerdidos = tempoSegundos * PENALIDADE_POR_SEGUNDO
    val scoreFinal = (pontuacaoFinalAcertos + bonusDefesa) - pontosPerdidos

    return DetalheDaPontuacao(
        pontuacaoFinalAcertos = pontuacaoFinalAcertos,
        bonusSobrevivencia = bonusDefesa,
        penalidadeTempo = pontosPerdidos,
        // Garante que o score será sempre positivo.
        pontuacaoFinal = if (scoreFinal > 0) scoreFinal else 0
    )
}

// Atalho pra quem só quer o número final.
fun calcularScoreFinal(
    pontuacaoFinalAcertos: Int,
    naviosVivos: Int,
    tempoSegundos: Int,
    tipoMapa: TipoMapa
): Int = detalharScoreFinal(pontuacaoFinalAcertos, naviosVivos, tempoSegundos, tipoMapa).pontuacaoFinal
