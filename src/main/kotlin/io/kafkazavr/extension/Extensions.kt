package io.kafkazavr.extension

import io.kafkazavr.feature.Kafka
import io.ktor.application.*
import io.ktor.config.ApplicationConfig
import io.ktor.http.cio.websocket.*
import io.ktor.http.content.*
import io.ktor.routing.*
import io.ktor.webjars.*
import java.time.Duration

operator fun ApplicationConfig.get(key: String): String = property(key).getString()

fun Application.module() {

    //region Reading Kafka configuration

//
//    val kafka: ApplicationConfig = environment.config.config("ktor.kafka")
    val mapBox: ApplicationConfig = environment.config.config("ktor.mapbox")
//    // println("Config: " + kafka.property("bootstrap-servers").getList())
//
//    val consumer: ApplicationConfig = kafka.config("consumer")
//    val producer: ApplicationConfig = kafka.config("producer")
//    val properties: ApplicationConfig = kafka.config("properties")
//
//    println("Consumer group id: ${consumer["group-id"]}")
//    println("Protocol: ${properties["ssl.endpoint.identification.algorithm"]}")
    //endregion

    //region Install features
    install(io.ktor.websocket.WebSockets) {
        pingPeriod = Duration.ofSeconds(15)
        timeout = Duration.ofSeconds(15)
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }
    install(Webjars)

    install(Kafka) {
        someParameterName = "hello"
    }
    //endregion

    //region Configure routing
    routing {
        static("assets") {
            resources("META-INF/resources/assets")
        }
    }
    //endregion
}