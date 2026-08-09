package bot

import testeJogo.Tabuleiro

class CACA {
    fun escolherTiro (tabuleiro: Tabuleiro, historicoTiros: Set<Coordenada>): Coordenada{

        var coordenada: Coordenada
        do {
            val linha = (0 until tabuleiro.getTamanho()).random()
            val coluna = (0 until tabuleiro.getTamanho()).random()

            coordenada = Coordenada(linha, coluna)
        } while (coordenada in historicoTiros)

        return coordenada

    }
}