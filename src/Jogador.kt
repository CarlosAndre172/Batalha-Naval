class Jogador(val nome: String) {

    val tabuleiro = Tabuleiro()

    init {
        tabuleiro.gerarNavios()
    }

}