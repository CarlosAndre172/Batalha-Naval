package database

import java.sql.Connection
import java.sql.DriverManager

class ConnectionDataBase {
    fun criaConexao(): Connection {
        try {
            // Avisa o programa para usar o Driver que você adicionou
            Class.forName("com.mysql.cj.jdbc.Driver")

            // Retorna a conexão configurada com o SEU banco de dados
            return DriverManager.getConnection(
                "jdbc:mysql://localhost/batalha_navalbd",
                "root",
                "aB0rt4d0@_@" // senha do seu sgbd
            )
        } catch (e: Exception) {
            // Se falhar, interrompe o programa mostrando o erro real
            throw RuntimeException("Erro ao abrir a conexão com o banco de dados", e)
        }
    }
}