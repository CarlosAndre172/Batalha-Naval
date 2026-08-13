package org.example.batalha_naval.dto

import kotlinx.serialization.Serializable

// Contrato entre o app (client) e o server para salvar o resultado de uma partida.
// "tabuleiro" é sempre o código aceito pelo banco: "POCA", "LAGO" ou "OCEANO".
@Serializable
data class SalvarPartidaRequest(
    val nomeJogador: String,
    val pontuacao: Int,
    val tempo: Int,
    val tabuleiro: String
)

// Uma linha da tábua de líderes.
@Serializable
data class RankingEntryDTO(
    val nomeJogador: String,
    val pontuacao: Int,
    val tempo: Int
)
