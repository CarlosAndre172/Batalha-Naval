package org.example.batalha_naval.jogo

// Port de src/service/VerificacaoPartida.kt: pesos de acerto/sobrevivência por
// mapa, penalidade por tempo, resultado nunca fica negativo.
fun calcularScoreFinal(acertos: Int, naviosVivos: Int, tempoSegundos: Int, tipoMapa: TipoMapa): Int {
    val pesoAcerto = when (tipoMapa) {
        TipoMapa.POCA -> 50
        TipoMapa.LAGOA -> 100
        TipoMapa.OCEANO -> 150
    }

    val pesoSobrevivencia = when (tipoMapa) {
        TipoMapa.POCA -> 100
        TipoMapa.LAGOA -> 200
        TipoMapa.OCEANO -> 300
    }

    val penalidadePorSegundo = 2
    val pontosAtaque = acertos * pesoAcerto
    val bonusDefesa = naviosVivos * pesoSobrevivencia
    val pontosPerdidos = tempoSegundos * penalidadePorSegundo
    val scoreFinal = (pontosAtaque + bonusDefesa) - pontosPerdidos

    return if (scoreFinal > 0) scoreFinal else 0
}
