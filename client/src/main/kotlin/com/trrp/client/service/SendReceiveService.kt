package com.trrp.client.service

import com.trrp.client.config.MessageHandlerContract
import com.trrp.client.config.SimpleSocketMessageHandler
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class SendReceiveService(
    private val encodeMessageService: EncodeMessageService,
    private val messageHandlers: List<MessageHandlerContract>
) {

    companion object {
        private val logger = LoggerFactory.getLogger(SendReceiveService::class.java)
    }

    fun sendMessageByType(type: String) {
        messageHandlers.firstOrNull { it.getType(type) }?.let {
            logger.info("[CLIENT] : подключение к серверу...")
            val publicRSAKey: String?
            try {
                publicRSAKey = it.connect()
            } catch (e: Exception) {
                logger.warn("[CLIENT] : ошибка при отправке или получении запроса к серверу")
                return
            }
            logger.info("[CLIENT] : шифрование и отправка данных")
            val encodeMessage = publicRSAKey?.let { it1 -> encodeMessageService.encodeMessage(it1) }
            if (encodeMessage != null) {
                try {
                    it.sendData(encodeMessage)
                } catch (e: Exception) {
                    logger.warn("[CLIENT] : ошибка при отправке или получении запроса к серверу")
                    return
                }
                logger.info("[CLIENT] : данные отправлены")
            }
            if (it is SimpleSocketMessageHandler)
                it.disconnect()
        }
    }

    /*fun sendSocketMessage() {
        val publicRSAKey = simpleSocketHandler.connect()
        val encodeMessage = encodeMessageService.encodeMessage(publicRSAKey)
        simpleSocketHandler.sendMessage(encodeMessage)
        simpleSocketHandler.disconnect()
    }

    fun sendRMQMessage() {
        logger.info("[CLIENT] : отправка запроса на получение RSA ключа")
        val publicRSAKey: String?
        try {
            publicRSAKey =
                rabbitTemplate.convertSendAndReceive(REQUEST_QUEUE, "Hi from client PC! [RMQ MESSAGE]") as String?
        } catch (e: Exception) {
            logger.warn("[CLIENT] : ошибка при отправке или получении запроса к серверу")
            return
        }

        logger.info("[CLIENT] : шифрование и отправка данных")
        val preparedMessage = publicRSAKey?.let { encodeMessageService.encodeMessage(it) }
        if (preparedMessage != null) {
            val orderJson: String = objectMapper.writeValueAsString(preparedMessage)
            try {
                rabbitTemplate.convertAndSend(
                    DATA_QUEUE, MessageBuilder
                        .withBody(orderJson.toByteArray())
                        .setContentType(MessageProperties.CONTENT_TYPE_JSON)
                        .build()
                )
            } catch (e: Exception) {
                logger.warn("[CLIENT] : ошибка при отправке или получении запроса к серверу")
                return
            }
            logger.info("[CLIENT] : данные отправлены")
        }
    }*/
}


