package testeJogo

import kotlin.random.Random
import strategy.*;

class Tabuleiro(private val tipoMapa: TipoMapa) {

    private val tamanho = tipoMapa.tamanho;

    private val matriz = Array(tamanho) { Array(tamanho) { 0 } };

    fun gerarNavios() {
        var posicionado: Boolean;

        do {
            limpar();
            posicionado = true;

            for (tamanhoNavio in tipoMapa.navios) {
                if (!adicionarNavio(tamanhoNavio)) {
                    posicionado = false;
                    break;
                }
            }
        } while (!posicionado);
    }

    private fun limpar() {
        for (linha in matriz) {
            linha.fill(0);
        }
    }

    private fun adicionarNavio(tamanhoNavio: Int): Boolean {
        val maxTentativas = tamanho * tamanho * 20;

        for (tentativa in 0 until maxTentativas) {
            val horizontal = Random.nextBoolean();
            val i = Random.nextInt(tamanho);
            val j = Random.nextInt(tamanho);

            if (podeAdicionarNavio(i, j, tamanhoNavio, horizontal)) {
                for (k in 0 until tamanhoNavio) {
                    if (horizontal) {
                        matriz[i][j + k] = 1;
                    } else {
                        matriz[i + k][j] = 1;
                    }
                }
                return true;
            }
        }

        return false;
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
            matriz[linha][coluna] = 3; // acertou
            return true;
        }
        else if (matriz[linha][coluna] == 0) {
            matriz[linha][coluna] = 2 // errou
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

    fun contarNaviosVivos(): Int {
        var blocosIntactos = 0

        for (linha in matriz) {
            for (valor in linha) {
                if (valor == 1) {
                    blocosIntactos++
                }
            }
        }

        return blocosIntactos
    }

    fun getTamanho() = tamanho;

    fun imprimir() {
        for (linha in matriz) {
            for (valor in linha) {
                val simbolo = when (valor) {
                    0 -> "0" // agua
                    1 -> "1" // navio intacto
                    2 -> "X" // errou a posicao
                    3 -> "!" // acertou navio
                    else -> "?"
                }
                print("$simbolo ");
            }
            println();
        }
    }
}