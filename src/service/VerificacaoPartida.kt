package service

import strategy.Lagoa
import strategy.Oceano
import strategy.Poca
import strategy.TipoMapa

class VerificacaoPartida {
    fun calcularScoreFinal(acertos: Int, naviosVivos: Int, tempoSegundos: Int, tabuleiro: TipoMapa): Int {

        //peso do acerto de cada tabuleiro
        val pesoAcerto = when (tabuleiro) {
            is Poca -> 50
            is Lagoa -> 100
            is Oceano -> 150
            else -> 0
        }

        // bonus para navio que nao foi afundado
        val pesoSobrevivencia = when (tabuleiro) {
            is Poca -> 100
            is Lagoa -> 200
            is Oceano -> 300
            else -> 0
        }

        val penalidadePorSegundo = 2 // penalidade por tempo
        val pontosAtaque = acertos * pesoAcerto
        val bonusDefesa = naviosVivos * pesoSobrevivencia
        val pontosPerdidos = tempoSegundos * penalidadePorSegundo
        val scoreFinal = (pontosAtaque + bonusDefesa) - pontosPerdidos

        // garante que o score sera sempre positivo
        return if (scoreFinal > 0) {
            scoreFinal
        } else {
            0
        }
    }


}