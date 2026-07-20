import kotlin.random.Random

class Tabuleiro {

    private val matriz = Array(10) { Array(10) { 0 } }

    fun gerarNavios() {
        for (tamNavio in 1..4) {
            adicionarNavio(tamNavio)
        }
    }

    private fun adicionarNavio(tamNavio: Int) {
        val horizontal = Random.nextBoolean()

        for (vez in 0..4 - tamNavio) {

            var i = 10
            var j = 10

            while (!podeAdicionarNavio(i, j, tamNavio, horizontal)) {
                i = Random.nextInt(10)
                j = Random.nextInt(10)
            }

            for (k in 0 until tamNavio) {
                if (horizontal)
                    matriz[i][j + k] = 1
                else
                    matriz[i + k][j] = 1
            }
        }
    }

    private fun podeAdicionarNavio(
        i: Int,
        j: Int,
        tamNavio: Int,
        horizontal: Boolean
    ): Boolean {

        for (k in 0 until tamNavio) {

            val linha = if (horizontal) i else i + k
            val coluna = if (horizontal) j + k else j

            if (linha !in 0..9 || coluna !in 0..9)
                return false

            for (x in linha - 1..linha + 1) {
                for (y in coluna - 1..coluna + 1) {

                    if (x in 0..9 && y in 0..9) {
                        if (matriz[x][y] == 1)
                            return false
                    }
                }
            }
        }

        return true
    }

    fun atacar(linha: Int, coluna: Int): Boolean {
        if (matriz[linha][coluna] == 1) {
            matriz[linha][coluna] = 0
            return true
        }
        return false
    }

    fun existeNavio(): Boolean {
        for (linha in matriz)
            for (valor in linha)
                if (valor == 1)
                    return true

        return false
    }

    fun imprimir() {
        for (linha in matriz) {
            for (valor in linha)
                print("$valor ")
            println()
        }
    }
}