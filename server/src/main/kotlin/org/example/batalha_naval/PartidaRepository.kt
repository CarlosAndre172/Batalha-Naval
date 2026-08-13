package org.example.batalha_naval

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.example.batalha_naval.dto.RankingEntryDTO
import org.example.batalha_naval.dto.SalvarPartidaRequest
import java.sql.Statement

// Port de src/repository/PartidaRepository.kt, falando os DTOs compartilhados do core.
class PartidaRepository {

    suspend fun salvarPartida(request: SalvarPartidaRequest): Unit = withContext(Dispatchers.IO) {
        Database.criarConexao().use { conexao ->
            conexao.autoCommit = false
            try {
                val sqlJogador = "INSERT INTO jogadores(nome) VALUES (?)"
                val idJogador = conexao.prepareStatement(sqlJogador, Statement.RETURN_GENERATED_KEYS).use { statement ->
                    statement.setString(1, request.nomeJogador)
                    statement.executeUpdate()
                    statement.generatedKeys.use { chaves ->
                        if (chaves.next()) chaves.getInt(1) else 0
                    }
                }

                val sqlPartida = "INSERT INTO partidas (id_jogador, pontuacao, tempo, tabuleiro) VALUES (?, ?, ?, ?)"
                conexao.prepareStatement(sqlPartida).use { statement ->
                    statement.setInt(1, idJogador)
                    statement.setInt(2, request.pontuacao)
                    statement.setInt(3, request.tempo)
                    statement.setString(4, request.tabuleiro)
                    statement.executeUpdate()
                }

                conexao.commit()
            } catch (e: Exception) {
                conexao.rollback()
                throw e
            }
        }
    }

    suspend fun rankingPorMapa(codigoMapa: String): List<RankingEntryDTO> = withContext(Dispatchers.IO) {
        val sql = """
            SELECT j.nome, p.pontuacao, p.tempo
            FROM partidas p
            INNER JOIN jogadores j ON p.id_jogador = j.id
            WHERE p.tabuleiro = ?
            ORDER BY p.pontuacao DESC, p.tempo ASC
            LIMIT 10
        """.trimIndent()

        Database.criarConexao().use { conexao ->
            conexao.prepareStatement(sql).use { statement ->
                statement.setString(1, codigoMapa)
                statement.executeQuery().use { resultado ->
                    val ranking = mutableListOf<RankingEntryDTO>()
                    while (resultado.next()) {
                        ranking.add(
                            RankingEntryDTO(
                                nomeJogador = resultado.getString("nome"),
                                pontuacao = resultado.getInt("pontuacao"),
                                tempo = resultado.getInt("tempo")
                            )
                        )
                    }
                    ranking
                }
            }
        }
    }
}
