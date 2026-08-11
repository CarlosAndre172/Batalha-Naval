package org.example.batalha_naval.jogo

// Cada modo de jogo tem um tamanho de tabuleiro e uma lista de navios.
// A lista de navios guarda o TAMANHO de cada embarcação (ex: 4 = navio de 4 casas).
enum class TipoMapa(
    val nomeExibido: String,
    val tamanho: Int,
    val navios: List<Int>
) {
    POCA("Poça", 5, listOf(4, 3, 2)),
    LAGOA("Lagoa", 8, listOf(4, 3, 3, 2, 2)),
    OCEANO("Oceano", 10, listOf(4, 4, 3, 3, 2, 2, 2));

    companion object {
        // Serve pra converter o texto do botão da tela de início ("Poça", "Lagoa"...) no tipo certo.
        fun porNome(nome: String): TipoMapa =
            entries.firstOrNull { it.nomeExibido == nome } ?: POCA
    }
}
