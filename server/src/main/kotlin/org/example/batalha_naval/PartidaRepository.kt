package org.example.batalha_naval

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.example.batalha_naval.dto.RankingEntryDTO
import org.example.batalha_naval.dto.SalvarPartidaRequest
import java.sql.Connection
import java.sql.Statement

// Port de src/repository/PartidaRepository.kt, falando os DTOs compartilhados do core.
//
// Regra do ranking: cada jogador tem UMA linha por mar, que guarda o melhor
// resultado dele. Se a partida nova não bate o recorde anterior, o banco não muda.
class PartidaRepository {

    // O que aconteceu com a partida que acabou de ser enviada.
    enum class ResultadoDoEnvio {
        PRIMEIRA_PARTIDA, // O jogador ainda não tinha marca nesse mar.
        NOVO_RECORDE,     // Bateu a própria marca: a linha antiga foi substituída.
        MANTEVE_RECORDE   // Pontuou menos que antes: o ranking continua como estava.
    }

    suspend fun salvarPartida(request: SalvarPartidaRequest): ResultadoDoEnvio = withContext(Dispatchers.IO) {
        Database.criarConexao().use { conexao ->
            conexao.autoCommit = false
            try {
                val idJogador = buscarOuCriarJogador(conexao, request.nomeJogador)
                val recorde = buscarRecorde(conexao, idJogador, request.tabuleiro)

                val resultado = when {
                    // Estreia do jogador nesse mar: entra no ranking direto.
                    recorde == null -> {
                        inserirPartida(conexao, idJogador, request)
                        ResultadoDoEnvio.PRIMEIRA_PARTIDA
                    }
                    // Bateu o próprio recorde: a marca antiga dá lugar à nova.
                    request.pontuacao > recorde.pontuacao -> {
                        atualizarPartida(conexao, recorde.idPartida, request)
                        ResultadoDoEnvio.NOVO_RECORDE
                    }
                    // Não superou: o ranking fica com a marca antiga, que é melhor.
                    else -> ResultadoDoEnvio.MANTEVE_RECORDE
                }

                conexao.commit()
                resultado
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

    // ---------- Bastidores ----------

    // A marca que o jogador já tem naquele mar (null = ainda não jogou lá).
    private data class Recorde(val idPartida: Int, val pontuacao: Int)

    // O mesmo nome sempre aponta pro mesmo jogador; só cria linha nova na estreia.
    private fun buscarOuCriarJogador(conexao: Connection, nome: String): Int {
        val sqlBusca = "SELECT id FROM jogadores WHERE nome = ? LIMIT 1"
        conexao.prepareStatement(sqlBusca).use { statement ->
            statement.setString(1, nome)
            statement.executeQuery().use { resultado ->
                if (resultado.next()) return resultado.getInt("id")
            }
        }

        val sqlInsercao = "INSERT INTO jogadores(nome) VALUES (?)"
        conexao.prepareStatement(sqlInsercao, Statement.RETURN_GENERATED_KEYS).use { statement ->
            statement.setString(1, nome)
            statement.executeUpdate()
            statement.generatedKeys.use { chaves ->
                if (chaves.next()) return chaves.getInt(1)
            }
        }

        throw IllegalStateException("Não foi possível criar o jogador '$nome'")
    }

    private fun buscarRecorde(conexao: Connection, idJogador: Int, tabuleiro: String): Recorde? {
        // Se um banco antigo tiver mais de uma linha pro mesmo par, ficamos com a melhor.
        val sql = """
            SELECT id_partida, pontuacao
            FROM partidas
            WHERE id_jogador = ? AND tabuleiro = ?
            ORDER BY pontuacao DESC
            LIMIT 1
        """.trimIndent()

        conexao.prepareStatement(sql).use { statement ->
            statement.setInt(1, idJogador)
            statement.setString(2, tabuleiro)
            statement.executeQuery().use { resultado ->
                return if (resultado.next()) {
                    Recorde(
                        idPartida = resultado.getInt("id_partida"),
                        pontuacao = resultado.getInt("pontuacao")
                    )
                } else {
                    null
                }
            }
        }
    }

    private fun inserirPartida(conexao: Connection, idJogador: Int, request: SalvarPartidaRequest) {
        val sql = "INSERT INTO partidas (id_jogador, pontuacao, tempo, tabuleiro) VALUES (?, ?, ?, ?)"
        conexao.prepareStatement(sql).use { statement ->
            statement.setInt(1, idJogador)
            statement.setInt(2, request.pontuacao)
            statement.setInt(3, request.tempo)
            statement.setString(4, request.tabuleiro)
            statement.executeUpdate()
        }
    }

    // Substitui a marca antiga no lugar: o ranking guarda uma linha por jogador/mar.
    private fun atualizarPartida(conexao: Connection, idPartida: Int, request: SalvarPartidaRequest) {
        val sql = "UPDATE partidas SET pontuacao = ?, tempo = ? WHERE id_partida = ?"
        conexao.prepareStatement(sql).use { statement ->
            statement.setInt(1, request.pontuacao)
            statement.setInt(2, request.tempo)
            statement.setInt(3, idPartida)
            statement.executeUpdate()
        }
    }
}
