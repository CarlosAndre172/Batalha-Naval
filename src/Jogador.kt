import strategy.*;

class Jogador(val nome: String, tipoMapa: TipoMapa) {

    val tabuleiro = Tabuleiro(tipoMapa);

    init {
        tabuleiro.gerarNavios();
    }

}