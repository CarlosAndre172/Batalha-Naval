package org.example.batalha_naval.jogo.bot

import org.example.batalha_naval.jogo.ResultadoTiro

// Adversário controlado pelo jogo: atira em casas aleatórias (modo CAÇA) até acertar
// um navio, então passa a mirar nas casas vizinhas (modo ALVO) até a fila de alvos
// esvaziar. É o mesmo algoritmo caça/alvo do protótipo em src/bot, portado pra cá.
class BotAdversario {

    private val historicoTiros = mutableSetOf<Pair<Int, Int>>()
    private val filaAlvos = mutableListOf<Pair<Int, Int>>()

    private var modoAtual = ModoBot.CACA
        private set

    fun escolherTiro(tamanhoTabuleiro: Int): Pair<Int, Int> {
        if (filaAlvos.isEmpty()) modoAtual = ModoBot.CACA

        val tiroEscolhido = if (modoAtual == ModoBot.ALVO) {
            filaAlvos.removeAt(0)
        } else {
            var coordenada: Pair<Int, Int>
            do {
                val linha = (0 until tamanhoTabuleiro).random()
                val coluna = (0 until tamanhoTabuleiro).random()
                coordenada = linha to coluna
            } while (coordenada in historicoTiros)
            coordenada
        }

        historicoTiros.add(tiroEscolhido)
        return tiroEscolhido
    }

    fun registrarResultado(coordenada: Pair<Int, Int>, resultado: ResultadoTiro, tamanhoTabuleiro: Int) {
        if (resultado != ResultadoTiro.ACERTOU && resultado != ResultadoTiro.AFUNDOU) return

        val (linha, coluna) = coordenada
        val adjacentes = listOf(
            linha - 1 to coluna,
            linha + 1 to coluna,
            linha to coluna - 1,
            linha to coluna + 1
        )

        for (candidata in adjacentes) {
            val (l, c) = candidata
            val dentroDoTabuleiro = l in 0 until tamanhoTabuleiro && c in 0 until tamanhoTabuleiro
            if (dentroDoTabuleiro && candidata !in historicoTiros && candidata !in filaAlvos) {
                filaAlvos.add(candidata)
            }
        }

        modoAtual = ModoBot.ALVO
    }
}
