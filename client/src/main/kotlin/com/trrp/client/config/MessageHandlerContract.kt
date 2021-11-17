package com.trrp.client.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.gson.Gson
import com.trrp.client.model.DataMessageDTO
import org.springframework.amqp.core.MessageBuilder
import org.springframework.amqp.core.MessageProperties
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.stereotype.Component
import java.io.DataInputStream
import java.io.DataOutputStream
import java.net.Socket

interface MessageHandlerContract {

    fun getType(type: String): Boolean

    fun connect(): String?

    fun sendData(encodeMessage: DataMessageDTO)
}

@Component
class RMQMessageHandler(
    private val rabbitTemplate: RabbitTemplate,
    private val objectMapper: ObjectMapper
) : MessageHandlerContract {

    companion object {
        private const val REQUEST_QUEUE = "request-queue"
        private const val DATA_QUEUE = "data-queue"
    }

    override fun getType(type: String): Boolean {
        return type == "rabbit"
    }

    override fun connect(): String? {
        return rabbitTemplate
            .convertSendAndReceive(
                REQUEST_QUEUE,
                "Hi from client PC! [RMQ MESSAGE]"
            ) as String?
    }

    override fun sendData(encodeMessage: DataMessageDTO) {
        val orderJson: String = objectMapper.writeValueAsString(encodeMessage)
        rabbitTemplate.convertAndSend(
            DATA_QUEUE, MessageBuilder
                .withBody(orderJson.toByteArray())
                .setContentType(MessageProperties.CONTENT_TYPE_JSON)
                .build()
        )
    }
}

@Component
class SimpleSocketMessageHandler(
    private val simpleSocketProperties: SimpleSocketProperties
) : MessageHandlerContract {

    companion object {
        private lateinit var dataIn: DataInputStream
        private lateinit var dataOut: DataOutputStream
        private lateinit var socket: Socket
    }

    override fun getType(type: String): Boolean {
        return type == "socket"
    }

    override fun connect(): String? {
        socket = with(simpleSocketProperties) {
            Socket(host, port)
        }
        dataOut = DataOutputStream(socket.getOutputStream())
        dataIn = DataInputStream(socket.getInputStream())

        dataOut.writeInt(1)
        dataOut.writeUTF("Hi from client PC [SOCKET MESSAGE]")
        dataOut.flush()

        return dataIn.readUTF()
    }

    override fun sendData(encodeMessage: DataMessageDTO) {
        dataOut.writeInt(2)
        val toJson = Gson().toJson(encodeMessage)
        dataOut.writeUTF(toJson)
        dataOut.flush()
    }

    fun disconnect() {
        dataOut.writeInt(-1)
        dataOut.flush()
        dataIn.close()
        dataOut.close()
        socket.close()
    }
}