package org.example.batalha_naval.audio

// Os barulhinhos do jogo: um splash quando o tiro cai na água e um estouro
// quando acerta uma embarcação.
//
// Cada plataforma implementa do seu jeito (no desktop as ondas são geradas na
// hora, então não precisamos carregar nenhum arquivo de áudio junto do jogo).
// Se a máquina não tiver som, o jogo continua normal, só fica mudo.
expect object EfeitosSonoros {
    fun tocarAgua()
    fun tocarExplosao()
}
