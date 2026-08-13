package org.example.batalha_naval.rede

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import org.example.batalha_naval.dto.RankingEntryDTO
import org.example.batalha_naval.dto.SalvarPartidaRequest
import org.example.batalha_naval.jogo.TipoMapa

// Fala com o servidor Ktor (server/) pra salvar partidas e buscar o ranking.
object ApiClient {
    private const val URL_BASE = "http://localhost:8080"

    private val client = HttpClient(engineHttp()) {
        install(ContentNegotiation) { json() }
    }

    // O banco só aceita 'POCA', 'LAGO' ou 'OCEANO' — TipoMapa.LAGOA precisa virar "LAGO".
    private fun TipoMapa.codigoBanco(): String = when (this) {
        TipoMapa.POCA -> "POCA"
        TipoMapa.LAGOA -> "LAGO"
        TipoMapa.OCEANO -> "OCEANO"
    }

    suspend fun salvarPartida(nomeJogador: String, pontuacao: Int, tempo: Int, tipoMapa: TipoMapa): Boolean {
        return try {
            client.post("$URL_BASE/partidas") {
                contentType(ContentType.Application.Json)
                setBody(
                    SalvarPartidaRequest(
                        nomeJogador = nomeJogador,
                        pontuacao = pontuacao,
                        tempo = tempo,
                        tabuleiro = tipoMapa.codigoBanco()
                    )
                )
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun buscarRanking(tipoMapa: TipoMapa): List<RankingEntryDTO> {
        return client.get("$URL_BASE/ranking") {
            url { parameters.append("tabuleiro", tipoMapa.codigoBanco()) }
        }.body()
    }
}
