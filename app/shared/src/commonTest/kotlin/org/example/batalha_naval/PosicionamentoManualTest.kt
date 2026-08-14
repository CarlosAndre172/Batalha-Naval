package org.example.batalha_naval

import org.example.batalha_naval.jogo.EstadoTabuleiro
import org.example.batalha_naval.jogo.TipoMapa
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

// Testes da distribuição manual dos navios (a aleatória continua sendo a padrão).
class PosicionamentoManualTest {

    @Test
    fun `tabuleiro manual nasce vazio`() {
        val tabuleiro = EstadoTabuleiro(TipoMapa.POCA, sortearNoInicio = false)

        assertTrue(tabuleiro.casasOcupadas().isEmpty())
        assertFalse(tabuleiro.frotaCompleta())
        assertEquals(4, tabuleiro.proximaEmbarcacao()?.tamanho) // Poça começa pelo navio de 4.
    }

    @Test
    fun `tabuleiro aleatorio ja vem com a frota inteira`() {
        val tabuleiro = EstadoTabuleiro(TipoMapa.OCEANO)

        assertTrue(tabuleiro.frotaCompleta())
        assertNull(tabuleiro.proximaEmbarcacao())
        assertEquals(TipoMapa.OCEANO.navios.sum(), tabuleiro.casasOcupadas().size)
    }

    @Test
    fun `navio ocupa as casas certas e passa a vez pro proximo`() {
        val tabuleiro = EstadoTabuleiro(TipoMapa.POCA, sortearNoInicio = false)
        val navio = assertNotNull(tabuleiro.proximaEmbarcacao())

        assertTrue(tabuleiro.colocar(navio, linha = 0, coluna = 0, horizontal = true))

        assertEquals(setOf(0 to 0, 0 to 1, 0 to 2, 0 to 3), tabuleiro.casasOcupadas())
        assertEquals(3, tabuleiro.proximaEmbarcacao()?.tamanho)
    }

    @Test
    fun `navio nao entra fora do tabuleiro nem encostado em outro`() {
        val tabuleiro = EstadoTabuleiro(TipoMapa.POCA, sortearNoInicio = false)
        val primeiro = assertNotNull(tabuleiro.proximaEmbarcacao())
        tabuleiro.colocar(primeiro, linha = 0, coluna = 0, horizontal = true)

        val segundo = assertNotNull(tabuleiro.proximaEmbarcacao())

        // Passaria da borda direita (3 casas a partir da coluna 3 de um mapa 5x5).
        assertFalse(tabuleiro.podeColocar(4, 3, segundo.tamanho, horizontal = true))
        // Encostaria na lateral do primeiro navio.
        assertFalse(tabuleiro.podeColocar(1, 0, segundo.tamanho, horizontal = true))
        // Duas linhas abaixo já dá.
        assertTrue(tabuleiro.podeColocar(2, 0, segundo.tamanho, horizontal = true))

        // Só o encaixe válido é aceito, e a frota não anda enquanto isso.
        assertFalse(tabuleiro.colocar(segundo, 1, 0, horizontal = true))
        assertEquals(segundo, tabuleiro.proximaEmbarcacao())
    }

    @Test
    fun `navio nao pode nem tocar outro na diagonal`() {
        val tabuleiro = EstadoTabuleiro(TipoMapa.LAGOA, sortearNoInicio = false)
        val primeiro = assertNotNull(tabuleiro.proximaEmbarcacao())
        // Navio de 4 casas na linha 2, colunas 2 a 5.
        assertTrue(tabuleiro.colocar(primeiro, linha = 2, coluna = 2, horizontal = true))

        val segundo = assertNotNull(tabuleiro.proximaEmbarcacao())

        // Só tocaria a quina do primeiro navio (linha 1, coluna 1) — ainda assim não pode.
        assertFalse(tabuleiro.podeColocar(1, 1, segundo.tamanho, horizontal = true))
        assertFalse(tabuleiro.colocar(segundo, 1, 1, horizontal = true))

        // Mesma coisa do outro lado (linha 3, coluna 5, encostando na quina inferior direita).
        assertFalse(tabuleiro.podeColocar(3, 5, segundo.tamanho, horizontal = false))

        // Uma casa de distância em qualquer direção já é permitido.
        assertTrue(tabuleiro.podeColocar(0, 1, segundo.tamanho, horizontal = true))

        assertEquals(segundo, tabuleiro.proximaEmbarcacao())
    }

    @Test
    fun `recomecar apaga a frota inteira`() {
        val tabuleiro = EstadoTabuleiro(TipoMapa.POCA, sortearNoInicio = false)
        val navio = assertNotNull(tabuleiro.proximaEmbarcacao())
        tabuleiro.colocar(navio, linha = 0, coluna = 0, horizontal = true)

        tabuleiro.limparPosicionamento()

        assertTrue(tabuleiro.casasOcupadas().isEmpty())
        assertEquals(navio, tabuleiro.proximaEmbarcacao())
    }

    @Test
    fun `frota fica completa depois do ultimo navio`() {
        val tabuleiro = EstadoTabuleiro(TipoMapa.POCA, sortearNoInicio = false)

        // Poça = navios de 4, 3 e 2 casas; uma linha vazia entre eles pra não encostar.
        listOf(0, 2, 4).forEach { linha ->
            val navio = assertNotNull(tabuleiro.proximaEmbarcacao())
            assertTrue(tabuleiro.colocar(navio, linha, coluna = 0, horizontal = true))
        }

        assertTrue(tabuleiro.frotaCompleta())
        assertNull(tabuleiro.proximaEmbarcacao())
    }

    @Test
    fun `casas do navio seguem a rotacao`() {
        val tabuleiro = EstadoTabuleiro(TipoMapa.LAGOA, sortearNoInicio = false)

        assertEquals(
            listOf(2 to 1, 2 to 2, 2 to 3),
            tabuleiro.casasDoNavio(2, 1, tamanhoNavio = 3, horizontal = true)
        )
        assertEquals(
            listOf(2 to 1, 3 to 1, 4 to 1),
            tabuleiro.casasDoNavio(2, 1, tamanhoNavio = 3, horizontal = false)
        )
    }
}
