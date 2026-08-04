import strategy.*;

class Jogo(private val tipoMapa: TipoMapa) {

    private val jogador = Jogador("Jogador", tipoMapa);
    private val computador = Jogador("Computador", tipoMapa);

    private val tamanho = tipoMapa.tamanho;

    private val tabuleiroVisivel = Array(tamanho) { Array(tamanho) { "?" } };

    fun iniciar() {

        println("Modo de jogo: ${tipoMapa.nome} (${tamanho}x${tamanho})\n");

        var jogando = true;

        while (jogando) {

            println("Tabuleiro do jogador");
            jogador.tabuleiro.imprimir();

            println("\nTabuleiro do computador");

            for (linha in tabuleiroVisivel) {
                for (valor in linha) {
                    print("$valor ");
                }
                println();
            }

            val linha = lerCoordenada("Linha:");
            val coluna = lerCoordenada("Coluna:");

            if (computador.tabuleiro.atacar(linha, coluna)) {

                tabuleiroVisivel[linha][coluna] = "!";
                println("Acertou!");

            } else {

                tabuleiroVisivel[linha][coluna] = "X";
                println("Errou!");

            }

            if (!computador.tabuleiro.existeNavio()) {
                println("Parabéns! Você venceu!");
                jogando = false;
            }
        }
    }

    private fun lerCoordenada(mensagem: String): Int {
        var valor: Int;

        do {
            println(mensagem);
            valor = (readln().toIntOrNull() ?: 0) - 1;

            if (valor !in 0 until tamanho) {
                println("Valor inválido! Digite um número entre 1 e $tamanho.");
            }

        } while (valor !in 0 until tamanho);

        return valor;
    }

}