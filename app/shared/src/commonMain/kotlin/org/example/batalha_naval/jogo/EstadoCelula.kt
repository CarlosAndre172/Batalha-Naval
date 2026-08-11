package org.example.batalha_naval.jogo

// O que o jogador já descobriu sobre uma casa do tabuleiro.
// É isso que decide a COR do botãozinho na tela.
enum class EstadoCelula {
    DESCONHECIDA, // Ainda não atiraram aqui (cor de água normal).
    AGUA,         // Atirou e não tinha nada (fica escuro).
    ACERTO,       // Atirou e tinha uma embarcação (fica colorido).
    AFUNDADO      // A embarcação inteira foi destruída.
}

// O que aconteceu quando o jogador atirou numa casa.
enum class ResultadoTiro {
    JA_ATACADA, // Clicou de novo numa casa que já tinha sido revelada.
    AGUA,
    ACERTOU,
    AFUNDOU
}

// Uma embarcação do tabuleiro. O "id" é usado pra marcar as casas que ela ocupa.
data class Embarcacao(val id: Int, val tamanho: Int)
