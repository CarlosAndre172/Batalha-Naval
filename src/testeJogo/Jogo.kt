package testeJogo

import Jogador
import strategy.TipoMapa
import bot.Bot
import database.ConnectionDataBase
import repository.PartidaRepository
import service.VerificacaoPartida

class Jogo(private val tipoMapa: TipoMapa, nomeJogador: String) {
    private val jogador = Jogador(nomeJogador, tipoMapa);
    private val computador = Jogador("Computador", tipoMapa);

    private val botAdversario = Bot()
    private val tamanho = tipoMapa.tamanho;

    private val tabuleiroVisivel = Array(tamanho) { Array(tamanho) { "?" } };

    private var tempoInicio: Long = 0
    private var tempoFim: Long = 0

    fun iniciar() {

        println("Modo de jogo: ${tipoMapa.nome} (${tamanho}x${tamanho})\n");

        tempoInicio = System.currentTimeMillis()

        var jogando = true;
        var seusAcertos = 0

        while (jogando) {

            var turnoJogador = true

            while (turnoJogador && jogando) {
                println("Tabuleiro do jogador");
                jogador.tabuleiro.imprimir();

                println("\nTabuleiro do computador");
                for (linha in tabuleiroVisivel) {
                    for (valor in linha) {
                        print("$valor ");
                    }
                    println();
                }


                val linha = lerCoordenada("Linha:");
                val coluna = lerCoordenada("Coluna:");


                if (computador.tabuleiro.atacar(linha, coluna)) {

                    tabuleiroVisivel[linha][coluna] = "!";
                    println("Acertou!");
                    seusAcertos++;

                    // só faz a verificacao se acertar algum navio
                    if (!computador.tabuleiro.existeNavio()) {
                        println("\nParabéns! Você afundou todos os navios do computador e venceu!");

                        val naviosSobreviventes = jogador.tabuleiro.contarNaviosVivos()
                        finalizarPartida(true, seusAcertos, naviosSobreviventes)
                        jogando = false
                        break;
                    }
                } else {
                    tabuleiroVisivel[linha][coluna] = "X";
                    println("Errou!");
                    turnoJogador = false;
                }
            }

            // Turno do computador
            var turnoComputador = true

            while(turnoComputador && jogando) {

                Thread.sleep(2000)
                val tiroBot = botAdversario.escolherTiro(jogador.tabuleiro)
                val linhaComputador = tiroBot.x
                val colunaComputador = tiroBot.y

                println("\nO computador atacou a posição (${linhaComputador + 1}, ${colunaComputador + 1})...");

                if (jogador.tabuleiro.atacar(linhaComputador, colunaComputador)) {
                    println("O computador acertou um dos seus navios!");
                    botAdversario.registrarAcerto(tiroBot, jogador.tabuleiro)
                    Thread.sleep(2000)

                    //faz a verificacao somente se o bot acertar um navio
                    if (!jogador.tabuleiro.existeNavio()) {
                        println("\nO computador afundou todos os seus navios. Você perdeu!");
                        finalizarPartida(false, 0,0)
                        jogando = false;
                    }
                } else {
                    println("O computador errou!");
                    turnoComputador = false
                    Thread.sleep(2000)
                }
            }
                println();
        }
    }

    private fun finalizarPartida(venceu: Boolean, acertos: Int, naviosRestantes: Int) {
        tempoFim = System.currentTimeMillis()
        val duracaoSegundos = ((tempoFim - tempoInicio) / 1000).toInt()

        println("Tempo de partida: $duracaoSegundos segundos.")

        if (venceu) {
            println("Calculando sua pontuação para o Ranking...")

            val verificacao = VerificacaoPartida()
            val scoreFinal = verificacao.calcularScoreFinal(
                acertos = acertos,
                naviosVivos = naviosRestantes,
                tempoSegundos = duracaoSegundos,
                tabuleiro = tipoMapa
            )

            println("Sua pontuação final foi de: $scoreFinal pontos!")

            val database = ConnectionDataBase()
            val repositorio = PartidaRepository(database)

            val jogadorBd = model.Jogador(nome = jogador.nome)
            val partida = model.Partida(jogador = jogadorBd, pontuacao = scoreFinal, tempo = duracaoSegundos, tabuleiro = tipoMapa)
            repositorio.salvarPartida(partida)
        }
    }

    private fun lerCoordenada(mensagem: String): Int {
        var valor: Int;

        do {
            println(mensagem);
            valor = (readln().toIntOrNull() ?: 0) - 1;

            if (valor !in 0 until tamanho) {
                println("Valor inválido! Digite um número entre 1 e $tamanho.");
            }

        } while (valor !in 0 until tamanho);

        return valor;
    }
}