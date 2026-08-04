import kotlin.random.Random
import strategy.*;

class Tabuleiro(private val tipoMapa: TipoMapa) {

    private val tamanho = tipoMapa.tamanho;

    private val matriz = Array(tamanho) { Array(tamanho) { 0 } };

    fun gerarNavios() {
        for (tamanhoNavio in tipoMapa.navios) {
            adicionarNavio(tamanhoNavio);
        }
    }

    private fun adicionarNavio(tamanhoNavio: Int) {
        val horizontal = Random.nextBoolean();

        var i: Int;
        var j: Int;

        do {
            i = Random.nextInt(tamanho);
            j = Random.nextInt(tamanho);
        } while (!podeAdicionarNavio(i, j, tamanhoNavio, horizontal));

        for (k in 0 until tamanhoNavio) {
            if (horizontal) {
                matriz[i][j + k] = 1;
            } else {
                matriz[i + k][j] = 1;
            }
        }
    }

    private fun podeAdicionarNavio(i: Int, j: Int, tamanhoNavio: Int, horizontal: Boolean): Boolean {

        for (k in 0 until tamanhoNavio) {

            val linha = if (horizontal) i else i + k;
            val coluna = if (horizontal) j + k else j;

            if (linha !in 0 until tamanho || coluna !in 0 until tamanho) {
                return false;
            }

            for (x in linha - 1..linha + 1) {
                for (y in coluna - 1..coluna + 1) {

                    if (x in 0 until tamanho && y in 0 until tamanho) {
                        if (matriz[x][y] == 1) {
                            return false;
                        }
                    }
                }
            }
        }

        return true;
    }

    fun atacar(linha: Int, coluna: Int): Boolean {
        if (matriz[linha][coluna] == 1) {
            matriz[linha][coluna] = 0;
            return true;
        }
        return false;
    }

    fun existeNavio(): Boolean {
        for (linha in matriz) {
            for (valor in linha) {
                if (valor == 1) {
                    return true;
                }
            }
        }

        return false;
    }

    fun getTamanho() = tamanho;

    fun imprimir() {
        for (linha in matriz) {
            for (valor in linha) {
                print("$valor ");
            }
            println();
        }
    }
}