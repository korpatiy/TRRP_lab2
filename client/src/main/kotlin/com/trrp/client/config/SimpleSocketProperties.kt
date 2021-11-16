package com.trrp.client.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties(prefix = "simple-socket")
class SimpleSocketProperties(

    val port: Int = 0,

    val host: String = ""
)
