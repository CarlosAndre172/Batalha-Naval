import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

dependencies {
    implementation(project(":app:shared"))

    // O jogo sobe o servidor Ktor (server/) dentro do próprio processo, pra salvar
    // partidas e ler o ranking sem precisar de um segundo terminal com ":server:run".
    // ktor-serverCore/Netty entram aqui pra compilar embeddedServer(...) direto no
    // main.kt; o resto (mysql-connector, content negotiation, logback...) já vem
    // pronto no classpath de execução via essa mesma dependência de projeto.
    implementation(project(":server"))
    implementation(libs.ktor.serverCore)
    implementation(libs.ktor.serverNetty)

    implementation(compose.desktop.currentOs)
    implementation(libs.kotlinx.coroutinesSwing)

    implementation(libs.compose.uiToolingPreview)
}

compose.desktop {
    application {
        mainClass = "org.example.batalha_naval.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "org.example.batalha_naval"
            packageVersion = "1.0.0"
        }
    }
}