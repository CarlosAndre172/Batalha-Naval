package org.example.batalha_naval.jogo

// Como a frota do jogador vai parar no tabuleiro. A frota do bot é SEMPRE sorteada.
enum class ModoPosicionamento(val nomeExibido: String) {
    ALEATORIO("Aleatória"),
    MANUAL("Escolher");
}
