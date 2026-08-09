package bot

class ALVO {
    private val filaAlvos =  mutableListOf<Coordenada>()

    fun addPosicoesAdjacentes(posicao: Coordenada, tamanhoTabuleiro: Int, historicoTiros: MutableSet<Coordenada>) {
        val cima = Coordenada(x = posicao.x - 1, y = posicao.y)
        val baixo = Coordenada(x = posicao.x + 1, y = posicao.y)
        val esquerda = Coordenada(x = posicao.x, y = posicao.y - 1)
        val direita = Coordenada(x = posicao.x, y = posicao.y + 1)

        val adjacentes = listOf(
            cima,
            baixo,
            esquerda,
            direita
        )

        for (coordenada in adjacentes){
            if (coordenada.x in 0 until tamanhoTabuleiro && coordenada.y in 0 until tamanhoTabuleiro && coordenada !in historicoTiros && coordenada !in filaAlvos) {

                filaAlvos.add(coordenada)
            }
        }
    }

    fun temAlvos(): Boolean {
        return filaAlvos.isNotEmpty()
    }

    fun proximoTiro(): Coordenada {
        return filaAlvos.removeAt(0)
    }
}