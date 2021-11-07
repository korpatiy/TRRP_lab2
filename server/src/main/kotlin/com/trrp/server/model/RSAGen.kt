package com.trrp.server.model

import java.security.KeyPairGenerator
import java.security.PrivateKey
import java.security.PublicKey

class RSAGen(
    var privateKey: PrivateKey? = null,
    var publicKey: PublicKey? = null
) {

    init {
        val generator = KeyPairGenerator.getInstance("RSA")
        generator.initialize(512)
        val pair = generator.generateKeyPair()
        privateKey = pair.private
        publicKey = pair.public
    }
}