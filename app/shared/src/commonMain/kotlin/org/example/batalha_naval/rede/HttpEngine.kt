package org.example.batalha_naval.rede

import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.HttpClientEngineConfig

// Cada plataforma escolhe seu próprio engine de HTTP (mesmo padrão do Platform.kt).
expect fun engineHttp(): HttpClientEngineFactory<HttpClientEngineConfig>
