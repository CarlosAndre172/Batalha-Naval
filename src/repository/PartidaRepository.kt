package repository

import database.ConnectionDataBase
import model.Partida

class PartidaRepository {

    fun salvarPartida(partida: Partida, idJogador: Int){

        val conexao = ConnectionDataBase().criaConexao()
        val sql = "INSERT INTO partidas(pontuacao, tempo, tabuleiro, id_jogador) VALUES (?, ?, ?, ?)"
        val statement = conexao.prepareStatement(sql)
        statement.setInt(1, partida.pontuacao)
        statement.setInt(2, partida.tempo)
        statement.setString(3, partida.tabuleiro.name)
        statement.setInt(4, idJogador)

        statement.executeUpdate()
    }
}