//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
fun main() {
    println("Testando a conexão com o banco de dados...")

    try {
        // Cria a fábrica que você construiu
        val database = ConnectionDataBase()

        // Pede a conexão para o banco
        val conexao = database.criaConexao()

        println("Conexão realizada com sucesso!")

        // Fecha a conexão após o teste
        conexao.close()

    } catch (e: Exception) {
        println("Não foi possível conectar.")
        e.printStackTrace()
    }
}