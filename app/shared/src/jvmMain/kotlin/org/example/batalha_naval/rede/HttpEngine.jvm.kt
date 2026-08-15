package org.example.batalha_naval.rede

import io.ktor.client.engine.HttpClientEngineConfig
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.cio.CIO

actual fun engineHttp(): HttpClientEngineFactory<HttpClientEngineConfig> = CIO
