package main

import database.ConnectionDataBase
import repository.PartidaRepository
import strategy.Lagoa
import strategy.Oceano
import strategy.Poca
import strategy.TipoMapa
import testeJogo.Jogo

fun main() {

    val tipoDeMapa = escolherTipoDeMapa();

    print("\nDigite o seu nome: ");
    val nomeJogador = readln().ifBlank { "Misterioso" }

    val jogo = Jogo(tipoDeMapa, nomeJogador);
    jogo.iniciar();

    // testando a busca do ranking
    val database = ConnectionDataBase()
    val repositorio = PartidaRepository(database)
    val ranking = repositorio.rankingPorMapa(tipoDeMapa.nome)

    if (ranking.isEmpty()) {
        println("Ainda não há jogadores no ranking deste mapa.")
    } else {
        for ((posicao, item) in ranking.withIndex()) {
            println("${posicao + 1}º Lugar | Capitão: ${item.nomeJogador} | Pontos: ${item.pontuacao} | Tempo: ${item.tempo}s")
        }
    }

}

private fun escolherTipoDeMapa(): TipoMapa {

    println("Escolha o tipo de mapa:");
    println("1 - Poça  (5x5,  3 navios: 1 grande, 1 médio, 1 pequeno)");
    println("2 - Lagoa (8x8,  5 navios: 1 grande, 2 médios, 2 pequenos)");
    println("3 - Oceano(10x10, 7 navios: 2 grandes, 2 médios, 3 pequenos)");

    while (true) {
        print("Opção: ");

        when (readln().toIntOrNull()) {
            1 -> return Poca();
            2 -> return Lagoa();
            3 -> return Oceano();
            else -> println("Opção inválida, tente novamente.");
        }
    }
}