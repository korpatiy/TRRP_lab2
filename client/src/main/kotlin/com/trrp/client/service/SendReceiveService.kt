package com.trrp.client.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.gson.Gson
import org.slf4j.LoggerFactory
import org.springframework.amqp.core.Message
import org.springframework.amqp.core.MessageBuilder
import org.springframework.amqp.core.MessageProperties
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.stereotype.Service
import java.io.DataInputStream
import java.net.Socket

import java.io.DataOutputStream

@Service
class SendReceiveService(
    private val rabbitTemplate: RabbitTemplate,
    private val objectMapper: ObjectMapper,
    private val encodeMessageService: EncodeMessageService
) {

    companion object {
        private const val REQUEST_QUEUE = "request-queue"
        private const val DATA_QUEUE = "data-queue"
        private val logger = LoggerFactory.getLogger(SendReceiveService::class.java)
    }


    fun sendSocketMessage() {
        val socket = Socket("localhost", 9091)
        socket.soTimeout = 6000000

        val dataOut = DataOutputStream(socket.getOutputStream())
        val dataIn = DataInputStream(socket.getInputStream())

        dataOut.writeInt(1)
        dataOut.writeUTF("Hi from client PC [SOCKET MESSAGE]")
        dataOut.flush()

        val publicRSAKey = dataIn.readUTF()
        val encodeMessage = encodeMessageService.encodeMessage(publicRSAKey)

        dataOut.writeInt(2)
        val toJson = Gson().toJson(encodeMessage)
        dataOut.writeUTF(toJson)
        dataOut.flush()

        dataOut.writeInt(-1)
        dataOut.flush()

        dataIn.close()
        dataOut.close()
        socket.close()
    }


    fun sendReceiveRMQMessage() {
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
            val message1: Message = MessageBuilder
                .withBody(orderJson.toByteArray())
                .setContentType(MessageProperties.CONTENT_TYPE_JSON)
                .build()
            try {
                rabbitTemplate.convertAndSend(DATA_QUEUE, message1)
            } catch (e: Exception) {
                logger.warn("[CLIENT] : ошибка при отправке или получении запроса к серверу")
                return
            }
            logger.info("[CLIENT] : данные отправлены")
        }
    }
}


