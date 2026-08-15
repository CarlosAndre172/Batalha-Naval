package org.example.batalha_naval

import kotlinx.coroutines.runBlocking
import org.example.batalha_naval.dto.SalvarPartidaRequest
import org.junit.Assume.assumeTrue
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

// Testa a regra do ranking contra o MySQL de verdade.
//
// Sem banco de pé (ou com senha diferente da configurada), os testes aparecem como
// IGNORADOS no relatório — nunca como verdes. Isso é de propósito: teste que pula
// caladinho vira falsa sensação de que a regra foi conferida.
//
// Pra rodar de verdade: aplique sql/batalha_naval.sql e, se o seu root tiver senha,
// exporte BATALHA_NAVAL_DB_PASSWORD antes de chamar o Gradle.
class PartidaRepositoryTest {

    private val repositorio = PartidaRepository()

    // Nome improvável de colidir com jogador de verdade (cabe nos 20 chars da coluna).
    private val nomeDeTeste = "zz_teste_${(1000..9999).random()}"

    private val bancoDisponivel: Boolean = try {
        Database.criarConexao().use { true }
    } catch (e: Exception) {
        false
    }

    // Interrompe o teste com "ignorado" quando não há banco pra conversar.
    private fun exigirBanco() =
        assumeTrue("MySQL indisponível: teste de ranking ignorado", bancoDisponivel)

    @BeforeTest
    fun limparAntes() = apagarJogadorDeTeste()

    @AfterTest
    fun limparDepois() = apagarJogadorDeTeste()

    @Test
    fun `a primeira partida do jogador entra no ranking`() {
        exigirBanco()

        val resultado = runBlocking { salvar(pontuacao = 500, tempo = 60) }

        assertEquals(PartidaRepository.ResultadoDoEnvio.PRIMEIRA_PARTIDA, resultado)
        assertEquals(500, pontuacaoNoRanking())
    }

    @Test
    fun `pontuacao maior substitui o recorde anterior`() {
        exigirBanco()

        runBlocking { salvar(pontuacao = 500, tempo = 60) }
        val resultado = runBlocking { salvar(pontuacao = 900, tempo = 45) }

        assertEquals(PartidaRepository.ResultadoDoEnvio.NOVO_RECORDE, resultado)
        assertEquals(900, pontuacaoNoRanking())
        // Continua sendo UMA linha só: o recorde substitui, não acumula.
        assertEquals(1, linhasDoJogador())
    }

    @Test
    fun `pontuacao menor nao derruba o recorde`() {
        exigirBanco()

        runBlocking { salvar(pontuacao = 900, tempo = 45) }
        val resultado = runBlocking { salvar(pontuacao = 100, tempo = 300) }

        assertEquals(PartidaRepository.ResultadoDoEnvio.MANTEVE_RECORDE, resultado)
        assertEquals(900, pontuacaoNoRanking())
        assertEquals(1, linhasDoJogador())
    }

    @Test
    fun `o recorde e separado por mar`() {
        exigirBanco()

        runBlocking { salvar(pontuacao = 900, tempo = 45, tabuleiro = "POCA") }
        val resultado = runBlocking { salvar(pontuacao = 100, tempo = 90, tabuleiro = "OCEANO") }

        // Pontuação menor, mas em outro mar: é a estreia do jogador lá.
        assertEquals(PartidaRepository.ResultadoDoEnvio.PRIMEIRA_PARTIDA, resultado)
        assertEquals(2, linhasDoJogador())

        val poca = runBlocking { repositorio.rankingPorMapa("POCA") }
        val oceano = runBlocking { repositorio.rankingPorMapa("OCEANO") }
        assertTrue(poca.any { it.nomeJogador == nomeDeTeste && it.pontuacao == 900 })
        assertTrue(oceano.any { it.nomeJogador == nomeDeTeste && it.pontuacao == 100 })
    }

    @Test
    fun `o mesmo nome nunca vira dois jogadores`() {
        exigirBanco()

        runBlocking { salvar(pontuacao = 100, tempo = 10) }
        runBlocking { salvar(pontuacao = 200, tempo = 10) }
        runBlocking { salvar(pontuacao = 300, tempo = 10, tabuleiro = "LAGO") }

        assertEquals(1, jogadoresComONome())
    }

    // ---------- Ajudantes ----------

    private suspend fun salvar(pontuacao: Int, tempo: Int, tabuleiro: String = "POCA") =
        repositorio.salvarPartida(
            SalvarPartidaRequest(
                nomeJogador = nomeDeTeste,
                pontuacao = pontuacao,
                tempo = tempo,
                tabuleiro = tabuleiro
            )
        )

    // A pontuação do jogador de teste no ranking da Poça (0 se não estiver lá).
    private fun pontuacaoNoRanking(): Int = runBlocking {
        repositorio.rankingPorMapa("POCA").firstOrNull { it.nomeJogador == nomeDeTeste }?.pontuacao ?: 0
    }

    private fun linhasDoJogador(): Int = contar(
        """
        SELECT COUNT(*) FROM partidas p
        INNER JOIN jogadores j ON p.id_jogador = j.id
        WHERE j.nome = ?
        """.trimIndent()
    )

    private fun jogadoresComONome(): Int = contar("SELECT COUNT(*) FROM jogadores WHERE nome = ?")

    private fun contar(sql: String): Int =
        Database.criarConexao().use { conexao ->
            conexao.prepareStatement(sql).use { statement ->
                statement.setString(1, nomeDeTeste)
                statement.executeQuery().use { resultado ->
                    if (resultado.next()) resultado.getInt(1) else 0
                }
            }
        }

    // Limpeza de antes/depois: sem banco não há o que apagar, e quem avisa que o
    // teste foi ignorado é o exigirBanco() lá de cima.
    private fun apagarJogadorDeTeste() {
        if (!bancoDisponivel) return

        Database.criarConexao().use { conexao ->
            val apagarPartidas = """
                DELETE p FROM partidas p
                INNER JOIN jogadores j ON p.id_jogador = j.id
                WHERE j.nome = ?
            """.trimIndent()

            listOf(apagarPartidas, "DELETE FROM jogadores WHERE nome = ?").forEach { sql ->
                conexao.prepareStatement(sql).use { statement ->
                    statement.setString(1, nomeDeTeste)
                    statement.executeUpdate()
                }
            }
        }
    }
}
