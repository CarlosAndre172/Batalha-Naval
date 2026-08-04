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
                println("\nParabéns! Você afundou todos os navios do computador e venceu!");
                jogando = false;
                break;
            }

            // Turno do computador (protótipo: ataca posições aleatórias, sem repetir)
            val (linhaComputador, colunaComputador) = computador.escolherAtaqueAleatorio();

            println("\nO computador atacou a posição (${linhaComputador + 1}, ${colunaComputador + 1})...");

            if (jogador.tabuleiro.atacar(linhaComputador, colunaComputador)) {
                println("O computador acertou um dos seus navios!");
            } else {
                println("O computador errou!");
            }

            if (!jogador.tabuleiro.existeNavio()) {
                println("\nO computador afundou todos os seus navios. Você perdeu!");
                jogando = false;
            }

            println();
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