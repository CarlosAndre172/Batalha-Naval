package org.example.batalha_naval

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import io.ktor.server.application.Application
import io.ktor.server.engine.EmbeddedServer
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty

// O jogo precisa do servidor Ktor de pé pra salvar partidas e mostrar o ranking
// (é o servidor quem fala com o MySQL). Em vez de depender de alguém lembrar de
// rodar ":server:run" num segundo terminal, subimos o mesmo servidor (server/) aqui
// dentro do processo do jogo — o ApiClient já aponta pra localhost:8080, então nem
// ele precisa saber que o servidor é local.
private fun subirServidorEmbutido(): EmbeddedServer<*, *>? = try {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .apply { start(wait = false) }
} catch (e: Exception) {
    // A causa mais comum daqui é a porta já estar ocupada: outra instância do jogo
    // aberta, ou alguém rodando ":server:run" na mão. Nesse caso não é um erro de
    // verdade — o ApiClient vai simplesmente conversar com o servidor que já existe.
    println("Aviso: servidor embutido não iniciou (${e.message}). Presumindo que já há um em localhost:8080.")
    null
}

fun main() {
    val servidorEmbutido = subirServidorEmbutido()

    fun encerrar() {
        servidorEmbutido?.stop(gracePeriodMillis = 500, timeoutMillis = 1000)
    }

    application {
        Window(
            onCloseRequest = {
                encerrar()
                exitApplication()
            },
            title = "Batalha-Naval",
        ) {
            App(fecharApp = {
                encerrar()
                exitApplication()
            })
        }
    }
}
