package repository


import database.ConnectionDataBase
import model.Partida
import java.sql.Statement


class PartidaRepository (private val bd: ConnectionDataBase){

    fun salvarPartida(partida: Partida) {

        val conexao = bd.criaConexao()

        try {
            conexao.autoCommit = false
            val sql = "INSERT INTO jogadores(nome) VALUES (?)"
            val statement = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
            statement.setString(1, partida.jogador.nome)
            statement.executeUpdate()

            val key = statement.generatedKeys
            var gerarId = 0
            if (key.next()){
                gerarId = key.getInt(1)
            }

            val partidaBd = "INSERT INTO partidas (id_jogador, pontuacao, tempo, tabuleiro) VALUES (?, ?, ?, ?)"
            val statement2 = conexao.prepareStatement(partidaBd)
            statement2.setInt(1, gerarId)
            statement2.setInt(2, partida.pontuacao)
            statement2.setInt(3, partida.tempo)
            statement2.setString(4, partida.tabuleiro.nome)
            statement2.executeUpdate()
            conexao.commit()

        } catch (e: Exception){
            conexao.rollback()
            println("erro ao salvar partida: ${e.message}")
        } finally {
            conexao.close()
        }
    }

    fun rankingPorMapa(nomeMapa: String): List<Ranking> {
        val listaRanking = mutableListOf<Ranking>()
        val sql = """
            SELECT j.nome, p.pontuacao, p.tempo 
            FROM partidas p
            INNER JOIN jogadores j ON p.id_jogador = j.id
            WHERE p.tabuleiro = ?
            ORDER BY p.pontuacao DESC, p.tempo ASC
            LIMIT 10
        """.trimIndent()

        try {
            val conexao = bd.criaConexao()
            if (conexao != null) {
                val stmt = conexao.prepareStatement(sql)

                stmt.setString(1, nomeMapa)

                val resultSet = stmt.executeQuery()

                while (resultSet.next()) {
                    val nome = resultSet.getString("nome")
                    val pontuacao = resultSet.getInt("pontuacao")
                    val tempo = resultSet.getInt("tempo")

                    listaRanking.add(Ranking(nome, pontuacao, tempo))
                }

                stmt.close()
                conexao.close()
            }

        } catch (e: Exception){
            println("Erro ao buscar o ranking no MySQL: ${e.message}")

        }
        return listaRanking
    }
}