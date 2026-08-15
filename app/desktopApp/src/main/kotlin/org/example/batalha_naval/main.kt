package org.example.batalha_naval

import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
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
        // O padrão do Compose Desktop (800x600) é baixo demais pra tela de resultado
        // (título + tabela + placar + botões) caber de pé; nessa janela maior tudo
        // aparece sem precisar rolar. Windows.CenteredPosition centraliza no monitor.
        val estadoDaJanela = rememberWindowState(
            size = DpSize(1000.dp, 720.dp),
            position = WindowPosition.Aligned(Alignment.Center)
        )

        Window(
            onCloseRequest = {
                encerrar()
                exitApplication()
            },
            title = "Batalha-Naval",
            state = estadoDaJanela,
        ) {
            App(fecharApp = {
                encerrar()
                exitApplication()
            })
        }
    }
}
