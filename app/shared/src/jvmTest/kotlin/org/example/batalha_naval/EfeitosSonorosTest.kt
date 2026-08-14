package org.example.batalha_naval

import org.example.batalha_naval.audio.EfeitosSonoros
import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertTrue

// Como os efeitos são gerados por conta (e não lidos de um arquivo), vale conferir
// que a onda realmente tem som: nem muda, nem estourada do começo ao fim.
class EfeitosSonorosTest {

    @Test
    fun `o splash da agua tem volume audivel e nao satura`() {
        val pico = conferirOnda(EfeitosSonoros.gerarAgua(), duracaoEsperada = 0.45f, saturacaoMaxima = 0.001f)

        // A água é o som "de fundo": tem que ficar mais baixa que a explosão.
        assertTrue(pico < 0.85f, "a água ficou alta demais (pico $pico)")
    }

    @Test
    fun `a explosao e mais forte que a agua e satura so no impacto`() {
        val pico = conferirOnda(EfeitosSonoros.gerarExplosao(), duracaoEsperada = 0.9f, saturacaoMaxima = 0.02f)

        assertTrue(pico > 0.85f, "a explosão ficou fraca demais (pico $pico)")
    }

    // Confere o básico da onda e devolve o pico dela.
    private fun conferirOnda(onda: ByteArray, duracaoEsperada: Float, saturacaoMaxima: Float): Float {
        val amostras = paraAmostras(onda)

        assertTrue(
            abs(amostras.size - 44100 * duracaoEsperada) < 100,
            "a onda deveria durar ${duracaoEsperada}s, mas tem ${amostras.size} amostras"
        )

        // Começa alto o bastante pra ser ouvido...
        val pico = amostras.maxOf { abs(it) }
        assertTrue(pico > 0.5f, "volume baixo demais (pico $pico)")

        // ...e termina em silêncio, senão o som cortaria com um estalo.
        val cauda = amostras.takeLast(200).maxOf { abs(it) }
        assertTrue(cauda < 0.05f, "a onda não morre no fim (cauda $cauda)")

        // Saturar demais viraria só chiado quadrado no lugar do efeito.
        val saturadas = amostras.count { abs(it) > 0.999f } / amostras.size.toFloat()
        assertTrue(saturadas < saturacaoMaxima, "onda saturada demais ($saturadas do total)")

        return pico
    }

    // Desfaz o empacotamento de 16 bits little-endian, voltando pra faixa -1..1.
    private fun paraAmostras(onda: ByteArray): List<Float> =
        (0 until onda.size / 2).map { i ->
            val baixo = onda[i * 2].toInt() and 0xFF
            val alto = onda[i * 2 + 1].toInt() // com sinal: carrega o bit mais significativo
            ((alto shl 8) or baixo) / Short.MAX_VALUE.toFloat()
        }
}
