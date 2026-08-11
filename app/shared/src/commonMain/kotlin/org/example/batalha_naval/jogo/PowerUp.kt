package org.example.batalha_naval.jogo

// Os dois power-ups que aparecem na caixinha da direita.
enum class PowerUp(val nomeExibido: String, val descricao: String) {
    BOMBA("Bomba", "Explode a casa e todas as vizinhas (3x3)"),
    BOMBARDEIO("Bombardeio", "Atinge a linha inteira do tiro")
}
