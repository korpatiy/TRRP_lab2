package com.trrp.server.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding


@ConstructorBinding
@ConfigurationProperties(prefix = "simple-socket")
class SimpleSocketProperties(

    val enabled: Boolean = false,

    val port: Int = 0
)
