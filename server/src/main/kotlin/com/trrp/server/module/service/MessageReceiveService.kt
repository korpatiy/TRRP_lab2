package com.trrp.server.module.service

import com.trrp.server.model.dtos.DataMessageDTO
import org.slf4j.LoggerFactory
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Service
import java.nio.charset.StandardCharsets
import java.security.KeyPairGenerator
import java.security.PrivateKey
import java.security.PublicKey
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

@Service
class MessageReceiveService {

    companion object {
        private val logger = LoggerFactory.getLogger(MessageReceiveService::class.java)
        private var privateKey: PrivateKey? = null
        private var publicKey: PublicKey? = null
    }

    /*fun receiveServletMessage(message: String): String {
        logger.info("received message: $message")
        return "hi from server"
    }*/

    @RabbitListener(queues = ["request-queue"])
    fun receiveRequestRabbitMessage(message: String): String? {
        logger.info("[SERVER] : принято сообщение $message")
        generateKeys()
        publicKey?.let {
            val encodeToString = Base64.getEncoder().encodeToString(publicKey?.encoded)
            logger.info("[SERVER] : генерация и отправка RSA ключа... $encodeToString")
            return encodeToString
        }
        return null
    }

    fun generateKeys() {
        val generator = KeyPairGenerator.getInstance("RSA")
        generator.initialize(512)
        val pair = generator.generateKeyPair()
        privateKey = pair.private
        publicKey = pair.public
    }

    @RabbitListener(queues = ["data-queue"])
    fun receiveDataRabbitMessage(dataMessageDTO: DataMessageDTO) {
        logger.info("[SERVER] : приняты данные")

        privateKey?.let {
            try {
                decodeMessage(dataMessageDTO)
            } catch (e: Exception) {
                logger.warn("[SERVER] : возникла ошибка при расшифровке сообщения")
            }
        }
    }

    private fun decodeMessage(dataMessageDTO: DataMessageDTO) {
        val decodeKey = Base64.getDecoder().decode(dataMessageDTO.decodeKey)
        val cipherRSA = Cipher.getInstance("RSA")
        cipherRSA.init(Cipher.DECRYPT_MODE, privateKey)
        val decryptedDES = cipherRSA.doFinal(decodeKey)

        val decryptCipher = Cipher.getInstance("DES")
        val decodeDES = Base64.getDecoder().decode(decryptedDES)
        val secretKeySpec = SecretKeySpec(decodeDES, "DES")
        decryptCipher.init(Cipher.DECRYPT_MODE, secretKeySpec)

        val decryptedMessageBytes = decryptCipher.doFinal(Base64.getDecoder().decode(dataMessageDTO.data))
        val decryptedMessage = String(decryptedMessageBytes, StandardCharsets.UTF_8)
        logger.info("[SERVER] : расшифрованное сообщение $decryptedMessage")
    }

}