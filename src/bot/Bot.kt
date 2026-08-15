package bot

import testeJogo.Tabuleiro


class Bot {
    private val historicoTiros = mutableSetOf<Coordenada>()

    private val caca = CACA()
    private val alvo = ALVO()

    private var modoAtual = ModoBot.CACA

    fun escolherTiro(tabuleiro: Tabuleiro): Coordenada {
        verificarModo()

        println("\nBot calculando as coordenadas...")
        Thread.sleep(2000)

        val tiroEscolhido = when (modoAtual) {

            ModoBot.CACA -> {
                caca.escolherTiro(tabuleiro, historicoTiros)
            }

            ModoBot.ALVO -> {
                alvo.proximoTiro()
            }
        }
        historicoTiros.add(tiroEscolhido)

        return tiroEscolhido
    }

    fun registrarAcerto(coordenada: Coordenada, tabuleiro: Tabuleiro) {

        alvo.addPosicoesAdjacentes(
            posicao = coordenada,
            tamanhoTabuleiro = tabuleiro.getTamanho(),
            historicoTiros = historicoTiros
        )

        modoAtual = ModoBot.ALVO
    }

    fun verificarModo() {

        if (!alvo.temAlvos()) {
            modoAtual = ModoBot.CACA
        }
    }

    fun getModoAtual(): ModoBot {
        return modoAtual
    }
}