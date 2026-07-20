class Jogo {

    private val jogador = Jogador("Jogador")
    private val computador = Jogador("Computador")

    private val tabuleiroVisivel =
        Array(10) { Array(10) { "?" } }

    fun iniciar() {

        var jogando = true

        while (jogando) {

            println("Tabuleiro do jogador")
            jogador.tabuleiro.imprimir()

            println("\nTabuleiro do computador")

            for (linha in tabuleiroVisivel) {
                for (valor in linha)
                    print("$valor ")
                println()
            }

            println("Linha:")
            val linha = readln().toInt() - 1

            println("Coluna:")
            val coluna = readln().toInt() - 1

            if (computador.tabuleiro.atacar(linha, coluna)) {

                tabuleiroVisivel[linha][coluna] = "!"
                println("Acertou!")

            } else {

                tabuleiroVisivel[linha][coluna] = "X"
                println("Errou!")

            }

            if (!computador.tabuleiro.existeNavio()) {
                println("Parabéns! Você venceu!")
                jogando = false
            }
        }
    }

}