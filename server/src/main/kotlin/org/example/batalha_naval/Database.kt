package org.example.batalha_naval

import java.sql.Connection
import java.sql.DriverManager

// Conexão com o MySQL local. Espera que o schema de sql/batalha_naval.sql já
// tenha sido aplicado (banco batalha_navalbd, usuário root sem senha).
object Database {
    fun criarConexao(): Connection {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver")
            return DriverManager.getConnection(
                "jdbc:mysql://localhost/batalha_navalbd",
                "root",
                ""
            )
        } catch (e: Exception) {
            throw RuntimeException("Erro ao abrir a conexão com o banco de dados", e)
        }
    }
}
