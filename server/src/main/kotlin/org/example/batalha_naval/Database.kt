package org.example.batalha_naval

import java.sql.Connection
import java.sql.DriverManager

// Conexão com o MySQL local. O schema (banco + tabelas) é criado sozinho na
// primeira vez que o servidor sobe — veja garantirEsquema() — então uma máquina
// nova não precisa rodar sql/batalha_naval.sql na mão.
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
    private const val NOME_BANCO = "batalha_navalbd"

    private val url: String get() = System.getenv("BATALHA_NAVAL_DB_URL") ?: URL_PADRAO
    private val usuario: String get() = System.getenv("BATALHA_NAVAL_DB_USER") ?: "root"
    private val senha: String get() = System.getenv("BATALHA_NAVAL_DB_PASSWORD") ?: ""

    // A mesma URL, mas sem o nome do banco — serve pra criar o banco antes dele
    // existir (não dá pra conectar "dentro" de um banco que ainda não foi criado).
    private val urlServidor: String get() =
        Regex("(jdbc:mysql://[^/]+)/.*").replace(url) { "${it.groupValues[1]}/" }

    fun criarConexao(): Connection = conectar(url)

    private fun conectar(urlAlvo: String): Connection {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver")
            return DriverManager.getConnection(urlAlvo, usuario, senha)
        } catch (e: Exception) {
            throw RuntimeException("Erro ao abrir a conexão com o banco de dados ($urlAlvo)", e)
        }
    }

    // Cria o banco e as tabelas se ainda não existirem. Idempotente: pode ser
    // chamada toda vez que o servidor sobe sem risco de apagar dados existentes,
    // porque é só "CREATE ... IF NOT EXISTS" do começo ao fim.
    fun garantirEsquema() {
        conectar(urlServidor).use { conexao ->
            conexao.createStatement().use { statement ->
                statement.execute("CREATE DATABASE IF NOT EXISTS $NOME_BANCO")
            }
        }

        criarConexao().use { conexao ->
            conexao.createStatement().use { statement ->
                // Mesmo desenho de sql/batalha_naval.sql: nome único por jogador, e
                // uma linha só por jogador em cada mar (é ali que mora o recorde dele).
                statement.execute(
                    """
                    CREATE TABLE IF NOT EXISTS jogadores (
                        id INTEGER AUTO_INCREMENT PRIMARY KEY,
                        nome VARCHAR(20) NOT NULL UNIQUE
                    )
                    """.trimIndent()
                )

                statement.execute(
                    """
                    CREATE TABLE IF NOT EXISTS partidas (
                        id_partida INTEGER AUTO_INCREMENT PRIMARY KEY,
                        id_jogador INTEGER,
                        pontuacao INTEGER NOT NULL,
                        tempo INTEGER NOT NULL,
                        tabuleiro ENUM('POCA', 'LAGO', 'OCEANO') NOT NULL,
                        CONSTRAINT fk_partida_jogador FOREIGN KEY (id_jogador) REFERENCES jogadores(id),
                        CONSTRAINT uk_recorde_por_mapa UNIQUE (id_jogador, tabuleiro)
                    )
                    """.trimIndent()
                )
            }
        }
    }
}
