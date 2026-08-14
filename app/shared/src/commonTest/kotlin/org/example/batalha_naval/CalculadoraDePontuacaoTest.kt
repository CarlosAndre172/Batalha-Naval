package org.example.batalha_naval

import org.example.batalha_naval.jogo.TipoMapa
import org.example.batalha_naval.jogo.calcularScoreFinal
import org.example.batalha_naval.jogo.detalharScoreFinal
import org.example.batalha_naval.jogo.multiplicadorDoCombo
import org.example.batalha_naval.jogo.pontosDaJogada
import kotlin.test.Test
import kotlin.test.assertEquals

// Testes da CalculadoraDePontuacao: a pontuação de cada jogada (com combo) e a
// conta final que vai pra tela de resultado e pro ranking.
class CalculadoraDePontuacaoTest {

    @Test
    fun `o combo so multiplica a partir do segundo acerto seguido`() {
        assertEquals(1, multiplicadorDoCombo(0))
        assertEquals(1, multiplicadorDoCombo(1))
        assertEquals(2, multiplicadorDoCombo(2))
        assertEquals(3, multiplicadorDoCombo(3))
        assertEquals(7, multiplicadorDoCombo(7))
    }

    @Test
    fun `cada jogada vale o peso do mar vezes o combo`() {
        // Poça: peso 50.
        assertEquals(50, pontosDaJogada(TipoMapa.POCA, combo = 1))
        assertEquals(100, pontosDaJogada(TipoMapa.POCA, combo = 2))
        assertEquals(150, pontosDaJogada(TipoMapa.POCA, combo = 3))

        // Oceano: peso 150.
        assertEquals(150, pontosDaJogada(TipoMapa.OCEANO, combo = 1))
        assertEquals(450, pontosDaJogada(TipoMapa.OCEANO, combo = 3))
    }

    @Test
    fun `somar jogada a jogada premia a sequencia sem erro`() {
        // Quatro acertos seguidos na Lagoa (peso 100): 1x + 2x + 3x + 4x.
        val emSequencia = (1..4).sumOf { combo -> pontosDaJogada(TipoMapa.LAGOA, combo) }
        assertEquals(100 + 200 + 300 + 400, emSequencia)

        // Os mesmos quatro acertos, mas errando entre um e outro (combo sempre volta a 1).
        val comErros = (1..4).sumOf { pontosDaJogada(TipoMapa.LAGOA, combo = 1) }
        assertEquals(400, comErros)
    }

    @Test
    fun `o score final soma os acertos com o bonus e desconta o tempo`() {
        val detalhe = detalharScoreFinal(
            pontuacaoFinalAcertos = 1000,
            naviosVivos = 3,
            tempoSegundos = 60,
            tipoMapa = TipoMapa.LAGOA
        )

        assertEquals(1000, detalhe.pontuacaoFinalAcertos)
        assertEquals(600, detalhe.bonusSobrevivencia)  // 3 casas × 200 (Lagoa)
        assertEquals(120, detalhe.penalidadeTempo)     // 60s × 2
        assertEquals(1480, detalhe.pontuacaoFinal)     // 1000 + 600 - 120
    }

    @Test
    fun `o score final nunca fica negativo`() {
        val detalhe = detalharScoreFinal(
            pontuacaoFinalAcertos = 50,
            naviosVivos = 0,
            tempoSegundos = 9999,
            tipoMapa = TipoMapa.POCA
        )

        assertEquals(0, detalhe.pontuacaoFinal)
    }

    @Test
    fun `o atalho calcularScoreFinal devolve o mesmo total do detalhe`() {
        val total = calcularScoreFinal(
            pontuacaoFinalAcertos = 800,
            naviosVivos = 2,
            tempoSegundos = 30,
            tipoMapa = TipoMapa.OCEANO
        )

        val detalhe = detalharScoreFinal(
            pontuacaoFinalAcertos = 800,
            naviosVivos = 2,
            tempoSegundos = 30,
            tipoMapa = TipoMapa.OCEANO
        )

        assertEquals(detalhe.pontuacaoFinal, total)
        assertEquals(1340, total) // 800 + (2 × 300) - (30 × 2)
    }
}
