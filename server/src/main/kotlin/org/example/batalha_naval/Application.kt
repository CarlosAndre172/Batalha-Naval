package org.example.batalha_naval

import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.example.batalha_naval.dto.SalvarPartidaRequest

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    install(ContentNegotiation) {
        json()
    }

    // Cria o banco e as tabelas se ainda não existirem, pra máquina nova funcionar
    // sem precisar rodar sql/batalha_naval.sql na mão. Se o MySQL nem estiver de
    // pé, o servidor sobe do mesmo jeito: as rotas abaixo já respondem 500 quando
    // tentam falar com o banco, em vez de derrubar o processo inteiro.
    try {
        Database.garantirEsquema()
    } catch (e: Exception) {
        log.warn("Não foi possível preparar o banco de dados: ${e.message}")
    }

    val partidaRepository = PartidaRepository()

    routing {
        get("/") {
            call.respondText(sayHello("Ktor"))
        }

        post("/partidas") {
            try {
                val request = call.receive<SalvarPartidaRequest>()
                partidaRepository.salvarPartida(request)
                call.respond(HttpStatusCode.Created)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, "Erro ao salvar partida: ${e.message}")
            }
        }

        get("/ranking") {
            try {
                val tabuleiro = call.request.queryParameters["tabuleiro"]
                if (tabuleiro == null) {
                    call.respond(HttpStatusCode.BadRequest, "Parâmetro 'tabuleiro' é obrigatório")
                    return@get
                }
                call.respond(partidaRepository.rankingPorMapa(tabuleiro))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, "Erro ao buscar ranking: ${e.message}")
            }
        }
    }
}