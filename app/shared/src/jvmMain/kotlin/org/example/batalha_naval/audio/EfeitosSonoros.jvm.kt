package org.example.batalha_naval.audio

import java.io.ByteArrayInputStream
import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioInputStream
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.LineEvent
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.min
import kotlin.math.sin
import kotlin.random.Random

// No desktop os dois efeitos são SINTETIZADOS: montamos a onda sonora amostra por
// amostra e mandamos direto pra placa de som. Assim o jogo não precisa carregar
// nenhum arquivo .wav junto.
//
// A receita dos dois sons é parecida:
//   ruído (chiado) + uma senoide que desce de tom, tudo multiplicado por um
//   envelope que começa alto e some rápido.
// O que muda é o "peso": a água é aguda e curta, a explosão é grave e longa.
actual object EfeitosSonoros {

    private const val TAXA_DE_AMOSTRAGEM = 44100f

    // 16 bits, 1 canal (mono), com sinal, little-endian.
    private val formato = AudioFormat(TAXA_DE_AMOSTRAGEM, 16, 1, true, false)

    // Geramos a onda uma única vez e reaproveitamos em todos os tiros da partida.
    private val ondaDaAgua: ByteArray by lazy { gerarAgua() }
    private val ondaDaExplosao: ByteArray by lazy { gerarExplosao() }

    actual fun tocarAgua() {
        tocar(ondaDaAgua)
    }

    actual fun tocarExplosao() {
        tocar(ondaDaExplosao)
    }

    // Toca sem travar a tela: o start() do Clip volta na hora e o som segue sozinho.
    private fun tocar(onda: ByteArray) {
        try {
            val clipe = AudioSystem.getClip()
            // Quando o som termina, devolvemos a linha de áudio pro sistema.
            clipe.addLineListener { evento ->
                if (evento.type == LineEvent.Type.STOP) clipe.close()
            }
            clipe.open(
                AudioInputStream(
                    ByteArrayInputStream(onda),
                    formato,
                    (onda.size / formato.frameSize).toLong()
                )
            )
            clipe.start()
        } catch (erro: Exception) {
            // Sem placa de som, sem driver, linha ocupada... o jogo continua mudo.
        }
    }

    // ---------- As duas ondas ----------

    // Splash: chiado agudo de espuma + um "plop" que desce de 500 Hz pra 130 Hz.
    internal fun gerarAgua(): ByteArray {
        val duracao = 0.45f
        val ruido = Random(7) // Semente fixa: o efeito sai igualzinho toda vez.
        var chiado = 0f

        return gerarOnda(duracao) { tempo ->
            // Filtro passa-baixa bem leve: tira só o excesso de agudo do ruído branco.
            chiado += ((ruido.nextFloat() * 2f - 1f) - chiado) * 0.35f

            val frequencia = 500f - 370f * (tempo / duracao)
            val plop = sin(2.0 * PI * frequencia * tempo).toFloat() * exp(-14f * tempo)

            // O 0.21 do fim é o volume (metade do original): a água é o som "de fundo"
            // do jogo, então ela fica de propósito mais baixa que a explosão.
            (chiado * 1.6f * exp(-9f * tempo) + plop * 0.5f) * 0.21f
        }
    }

    // Estouro: estrondo grave (ruído muito filtrado) + um corpo de 90 Hz caindo pra 35 Hz.
    internal fun gerarExplosao(): ByteArray {
        val duracao = 0.9f
        val ruido = Random(23)
        var estrondo = 0f

        return gerarOnda(duracao) { tempo ->
            // Filtro bem mais forte que o da água: sobra quase só o grave.
            estrondo += ((ruido.nextFloat() * 2f - 1f) - estrondo) * 0.08f

            val frequencia = 90f - 55f * (tempo / duracao)
            val corpo = sin(2.0 * PI * frequencia * tempo).toFloat() * exp(-3.5f * tempo)

            // Bem mais alta que a água: é o som de recompensa do acerto. 0.4 é a
            // metade do volume original.
            (estrondo * 6f * exp(-4.5f * tempo) + corpo * 0.8f) * 0.4f
        }
    }

    // Percorre a duração inteira chamando a "receita" a cada amostra e converte o
    // resultado (-1..1) em bytes de 16 bits. O corte em -1..1 satura o som de
    // propósito, o que deixa a explosão com aquela textura suja.
    private fun gerarOnda(duracao: Float, receita: (tempo: Float) -> Float): ByteArray {
        val totalDeAmostras = (TAXA_DE_AMOSTRAGEM * duracao).toInt()
        val bytes = ByteArray(totalDeAmostras * 2)

        for (i in 0 until totalDeAmostras) {
            val tempo = i / TAXA_DE_AMOSTRAGEM

            // Sobe o volume nos primeiros 5 ms pra não sair um "clique" no começo.
            val ataque = min(1f, tempo / 0.005f)

            val amostra = (receita(tempo) * ataque).coerceIn(-1f, 1f)
            val valor = (amostra * Short.MAX_VALUE).toInt()

            bytes[i * 2] = (valor and 0xFF).toByte()
            bytes[i * 2 + 1] = ((valor shr 8) and 0xFF).toByte()
        }

        return bytes
    }
}
