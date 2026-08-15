package org.example.batalha_naval.jogo

import androidx.compose.runtime.mutableStateListOf
import kotlin.random.Random

// Guarda TUDO que acontece em um tabuleiro: onde estão os navios,
// quais casas já foram reveladas e quais embarcações já afundaram.
// A lógica de sorteio dos navios é a mesma do Tabuleiro.kt do projeto original.
//
// O "sortearNoInicio" existe por causa do posicionamento manual: quando o jogador
// vai escolher onde cada navio fica, o tabuleiro nasce vazio e as embarcações
// entram uma a uma pelo colocar().
class EstadoTabuleiro(val tipoMapa: TipoMapa, sortearNoInicio: Boolean = true) {

    val tamanho: Int = tipoMapa.tamanho

    // Transforma a lista de tamanhos (ex: [4, 3, 2]) em embarcações com id (1, 2, 3...).
    val embarcacoes: List<Embarcacao> =
        tipoMapa.navios.mapIndexed { indice, tamanhoNavio -> Embarcacao(id = indice + 1, tamanho = tamanhoNavio) }

    // A grade "secreta": 0 = água, qualquer outro número = id da embarcação que está ali.
    private val grade = Array(tamanho) { IntArray(tamanho) }

    // Quantas casas de cada embarcação ainda não foram acertadas.
    private val vidaDasEmbarcacoes = HashMap<Int, Int>()

    // O que o jogador já descobriu. É uma lista "observável": quando muda, a tela se redesenha sozinha.
    private val celulas = mutableStateListOf<EstadoCelula>().apply {
        repeat(tamanho * tamanho) { add(EstadoCelula.DESCONHECIDA) }
    }

    // Ids das embarcações que já foram totalmente destruídas (também é observável).
    val afundadas = mutableStateListOf<Int>()

    // Ids das embarcações que já estão na grade. Observável, pra tela de posicionamento
    // se redesenhar sozinha a cada navio colocado.
    val posicionadas = mutableStateListOf<Int>()

    init {
        if (sortearNoInicio) posicionarNavios() else limpar()
    }

    // A tela usa isso pra saber qual cor desenhar em cada botãozinho.
    fun estadoDe(linha: Int, coluna: Int): EstadoCelula = celulas[linha * tamanho + coluna]

    fun dentroDoTabuleiro(linha: Int, coluna: Int): Boolean =
        linha in 0 until tamanho && coluna in 0 until tamanho

    // Atira em uma casa e devolve o que aconteceu.
    fun atacar(linha: Int, coluna: Int): ResultadoTiro {
        if (!dentroDoTabuleiro(linha, coluna)) return ResultadoTiro.JA_ATACADA
        if (estadoDe(linha, coluna) != EstadoCelula.DESCONHECIDA) return ResultadoTiro.JA_ATACADA

        val idDaEmbarcacao = grade[linha][coluna]

        // Não tinha nada ali: só água.
        if (idDaEmbarcacao == 0) {
            celulas[linha * tamanho + coluna] = EstadoCelula.AGUA
            return ResultadoTiro.AGUA
        }

        // Acertou! Tira uma "vida" da embarcação.
        celulas[linha * tamanho + coluna] = EstadoCelula.ACERTO
        val vidaRestante = (vidaDasEmbarcacoes[idDaEmbarcacao] ?: 1) - 1
        vidaDasEmbarcacoes[idDaEmbarcacao] = vidaRestante

        if (vidaRestante <= 0) {
            afundadas.add(idDaEmbarcacao)
            marcarComoAfundada(idDaEmbarcacao)
            return ResultadoTiro.AFUNDOU
        }

        return ResultadoTiro.ACERTOU
    }

    // Quando a embarcação afunda, todas as casas dela mudam de cor.
    private fun marcarComoAfundada(idDaEmbarcacao: Int) {
        for (linha in 0 until tamanho) {
            for (coluna in 0 until tamanho) {
                if (grade[linha][coluna] == idDaEmbarcacao) {
                    celulas[linha * tamanho + coluna] = EstadoCelula.AFUNDADO
                }
            }
        }
    }

    fun acabou(): Boolean = afundadas.size == embarcacoes.size

    // Soma as casas de navio que ainda não foram acertadas. Usado no cálculo do score final.
    fun celulasDeNaviosRestantes(): Int = vidaDasEmbarcacoes.values.sum()

    // Embarcações que já levaram tiro mas ainda estão flutuando. A tela de resultado
    // mostra isso separado das que afundaram de vez.
    fun embarcacoesDanificadas(): Int = embarcacoes.count { embarcacao ->
        val vida = vidaDasEmbarcacoes[embarcacao.id] ?: embarcacao.tamanho
        vida in 1 until embarcacao.tamanho
    }

    // ---------- Posicionamento manual ----------

    // A próxima embarcação que ainda falta colocar. Null = a frota inteira já está no mar.
    fun proximaEmbarcacao(): Embarcacao? = embarcacoes.firstOrNull { it.id !in posicionadas }

    fun frotaCompleta(): Boolean = posicionadas.size == embarcacoes.size

    // As casas que um navio ocuparia a partir de uma casa inicial. Pode devolver casas
    // fora do tabuleiro: quem desenha a prévia simplesmente ignora essas.
    fun casasDoNavio(linha: Int, coluna: Int, tamanhoNavio: Int, horizontal: Boolean): List<Pair<Int, Int>> =
        (0 until tamanhoNavio).map { k ->
            if (horizontal) linha to (coluna + k) else (linha + k) to coluna
        }

    fun podeColocar(linha: Int, coluna: Int, tamanhoNavio: Int, horizontal: Boolean): Boolean =
        cabeAqui(linha, coluna, tamanhoNavio, horizontal)

    // Coloca uma embarcação na grade. Devolve false se ela não couber ali (ou se já foi colocada).
    fun colocar(embarcacao: Embarcacao, linha: Int, coluna: Int, horizontal: Boolean): Boolean {
        if (embarcacao.id in posicionadas) return false
        if (!cabeAqui(linha, coluna, embarcacao.tamanho, horizontal)) return false

        casasDoNavio(linha, coluna, embarcacao.tamanho, horizontal).forEach { (l, c) ->
            grade[l][c] = embarcacao.id
        }
        posicionadas.add(embarcacao.id)
        return true
    }

    // Apaga a frota inteira pra começar a distribuição de novo.
    fun limparPosicionamento() = limpar()

    // Todas as casas que hoje têm navio. A tela de posicionamento usa isso pra pintar a grade.
    fun casasOcupadas(): Set<Pair<Int, Int>> = buildSet {
        for (linha in 0 until tamanho) {
            for (coluna in 0 until tamanho) {
                if (grade[linha][coluna] != 0) add(linha to coluna)
            }
        }
    }

    // ---------- Sorteio dos navios ----------

    private fun posicionarNavios() {
        var deuCerto: Boolean
        // Se algum navio não couber, limpa tudo e tenta o tabuleiro inteiro de novo.
        do {
            limpar()
            deuCerto = embarcacoes.all { adicionarNavio(it) }
        } while (!deuCerto)
    }

    private fun limpar() {
        for (linha in grade) linha.fill(0)
        afundadas.clear()
        posicionadas.clear()
        vidaDasEmbarcacoes.clear()
        embarcacoes.forEach { vidaDasEmbarcacoes[it.id] = it.tamanho }
    }

    private fun adicionarNavio(embarcacao: Embarcacao): Boolean {
        val maxTentativas = tamanho * tamanho * 20

        repeat(maxTentativas) {
            val horizontal = Random.nextBoolean()
            val linha = Random.nextInt(tamanho)
            val coluna = Random.nextInt(tamanho)

            if (colocar(embarcacao, linha, coluna, horizontal)) return true
        }

        return false
    }

    // Um navio só pode entrar se couber no tabuleiro E se não estiver encostando em outro.
    private fun cabeAqui(linha: Int, coluna: Int, tamanhoNavio: Int, horizontal: Boolean): Boolean {
        for (k in 0 until tamanhoNavio) {
            val l = if (horizontal) linha else linha + k
            val c = if (horizontal) coluna + k else coluna

            if (!dentroDoTabuleiro(l, c)) return false

            // Olha as 8 casas em volta (e a própria) pra garantir que não tem navio grudado.
            for (x in l - 1..l + 1) {
                for (y in c - 1..c + 1) {
                    if (dentroDoTabuleiro(x, y) && grade[x][y] != 0) return false
                }
            }
        }
        return true
    }
}
