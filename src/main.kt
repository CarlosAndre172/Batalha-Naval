import strategy.*;

fun main() {

    val tipoMapa = escolherTipoDeMapa();

    val jogo = Jogo(tipoMapa);
    jogo.iniciar();

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