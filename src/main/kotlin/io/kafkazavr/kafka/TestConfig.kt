package io.kafkazavr.kafka

import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import io.kafkazavr.ArgsHolder
import io.kafkazavr.extension.splitPair
import org.apache.kafka.clients.admin.AdminClient
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig.BOOTSTRAP_SERVERS_CONFIG
import java.io.File
import java.util.*

fun main(args: Array<String>) {
    //region workaround https://youtrack.jetbrains.com/issue/KTOR-2318 
    ArgsHolder.args = args
    val configFile = ArgsHolder.args.mapNotNull { it.splitPair('=') }.toMap()["-config"]?.let { File(it) }

    val config: Config = ConfigFactory.parseFile(configFile)

    val bootstrapServers = config.getList("ktor.kafka.bootstrap.servers")
    println(bootstrapServers.unwrapped())
    
    // common config
    val commonConfig = config.getConfig("ktor.kafka.properties").entrySet().associateBy({ it.key }, { it.value.unwrapped() })

    // get consumer config
    val consumerConfig = config.getConfig("ktor.kafka.consumer").entrySet().associateBy({ it.key }, { it.value.unwrapped() })

    // get producer config
    val producerConfig = config.getConfig("ktor.kafka.producer").entrySet().associateBy({ it.key }, { it.value.unwrapped() })
    // creating properties
    val producerProperties: Properties = Properties().apply {
        putAll(producerConfig)
        putAll(commonConfig)
        put(BOOTSTRAP_SERVERS_CONFIG, bootstrapServers.unwrapped())
    }
    KafkaProducer<String, String>(producerProperties)

    val adminProperties: Properties = Properties().apply {
        putAll(commonConfig)
        put(BOOTSTRAP_SERVERS_CONFIG, bootstrapServers.unwrapped())
    }
    AdminClient.create(adminProperties)

    val consumerProperties: Properties = Properties().apply {
        putAll(commonConfig)
        putAll(consumerConfig)
        put(BOOTSTRAP_SERVERS_CONFIG, bootstrapServers.unwrapped())
    }
    KafkaConsumer<String, String>(consumerProperties)
}