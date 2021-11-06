package com.trrp.client.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties(prefix = "spring.rabbitmq")
class RabbitMQProperties(
    val username: String = "",

    val password: String = "",

    val port: String = "",

    val host: String = ""
)

