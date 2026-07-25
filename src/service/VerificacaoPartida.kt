package service

import model.Jogador
import model.Partida
import repository.JogadorRepository
import repository.PartidaRepository

class VerificacaoPartida {
    fun finalizarPartida(partida: Partida){
        if(partida.pontuacao >= 0 && partida.tempo > 0){ // mudar para verificacao de termino da partida apos a criacao da flag
            val jogador = partida.jogador
            val id = jogador.
            val partidaRepository = PartidaRepository()
            partidaRepository.salvarPartida(partida, id)
        }
        else {

        }
    }
}