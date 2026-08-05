import kotlin.random.Random;
import strategy.*;

class Jogador(val nome: String, tipoMapa: TipoMapa) {

    val tabuleiro = Tabuleiro(tipoMapa);

    private val tamanho = tipoMapa.tamanho;

    private val posicoesJaAtacadas = mutableSetOf<Pair<Int, Int>>();

    init {
        tabuleiro.gerarNavios();
    }

    fun escolherAtaqueAleatorio(): Pair<Int, Int> {
        var linha: Int;
        var coluna: Int;

        do {
            linha = Random.nextInt(tamanho);
            coluna = Random.nextInt(tamanho);
        } while (Pair(linha, coluna) in posicoesJaAtacadas);

        posicoesJaAtacadas.add(Pair(linha, coluna));
        return Pair(linha, coluna);
    }

}
