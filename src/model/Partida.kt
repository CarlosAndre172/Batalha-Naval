package model

data class Partida(
    val idPartida: Int? = null,
    val jogador: Jogador,
    val pontuacao: Int,
    val tempo: Int,
    val tabuleiro: TipoTabuleiro
)