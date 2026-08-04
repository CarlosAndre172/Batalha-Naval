package repository

import database.ConnectionDataBase
import model.Jogador

class JogadorRepository {

    fun salvarJogador(jogador: Jogador) {
        val conexao = ConnectionDataBase().criaConexao()
        val sql = "INSERT INTO jogadores(nome) VALUES (?)"
        val statement = conexao.prepareStatement(sql)

        statement.setString(1, jogador.nome)
        statement.executeUpdate()
        statement.close()
        conexao.close()
    }
}