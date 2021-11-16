package com.trrp.server.module.service

import com.trrp.server.model.RSAGen
import com.trrp.server.model.dtos.DataMessageDTO
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.util.*


@Component
class MessageHandleService(
    private val migrationService: MigrationService,
    private val messageDecoderService: MessageDecoderService
) {

    companion object {
        private val logger = LoggerFactory.getLogger(MessageHandleService::class.java)
        private lateinit var rsaGen: RSAGen
    }

    fun receiveRequestMessage(message: String): String? {
        logger.info("[SERVER] : принято сообщение $message")
        rsaGen = RSAGen()
        val encodeToString = Base64.getEncoder().encodeToString(rsaGen.publicKey?.encoded)
        logger.info("[SERVER] : генерация и отправка RSA ключа... $encodeToString")
        return encodeToString
    }

    fun receiveDataMessage(dataMessageDTO: DataMessageDTO) {
        logger.info("[SERVER] : приняты данные")
        var decodeMessage = ""
        rsaGen.privateKey?.let {
            try {
                decodeMessage = messageDecoderService.decodeMessage(dataMessageDTO, rsaGen)
            } catch (e: Exception) {
                logger.warn("[SERVER] : возникла ошибка при расшифровке сообщения")
            }
        }
        logger.info("[SERVER] : расшифрованное сообщение $decodeMessage")
        if (decodeMessage.isNotEmpty()) {
            try {
                migrationService.migrate(decodeMessage)
            } catch (e: Exception) {
                logger.warn("[SERVER] : возникла ошибка при миграции данных")
            }
        }
        logger.info("[SERVER] : данные успешно перенесены")
    }
}