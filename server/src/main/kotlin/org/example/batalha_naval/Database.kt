package org.example.batalha_naval

import java.sql.Connection
import java.sql.DriverManager

// Conexão com o MySQL local. Espera que o schema de sql/batalha_naval.sql já
// tenha sido aplicado (banco batalha_navalbd).
//
// Os dados de acesso saem de variáveis de ambiente, com o padrão antigo (root sem
// senha) como valor de reserva. Assim quem tem senha no MySQL não precisa mexer no
// código nem deixar a senha versionada aqui:
//
//   BATALHA_NAVAL_DB_URL      (padrão: jdbc:mysql://localhost/batalha_navalbd)
//   BATALHA_NAVAL_DB_USER     (padrão: root)
//   BATALHA_NAVAL_DB_PASSWORD (padrão: vazio)
object Database {

    private const val URL_PADRAO = "jdbc:mysql://localhost/batalha_navalbd"

    private val url: String get() = System.getenv("BATALHA_NAVAL_DB_URL") ?: URL_PADRAO
    private val usuario: String get() = System.getenv("BATALHA_NAVAL_DB_USER") ?: "root"
    private val senha: String get() = System.getenv("BATALHA_NAVAL_DB_PASSWORD") ?: ""

    fun criarConexao(): Connection {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver")
            return DriverManager.getConnection(url, usuario, senha)
        } catch (e: Exception) {
            throw RuntimeException("Erro ao abrir a conexão com o banco de dados ($url)", e)
        }
    }
}
