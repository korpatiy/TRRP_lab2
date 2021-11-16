package com.trrp.server.module.service.listeners

import com.google.gson.Gson
import com.trrp.server.config.SimpleSocketProperties
import com.trrp.server.model.dtos.DataMessageDTO
import com.trrp.server.module.service.MessageHandleService
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import java.io.DataInputStream
import java.io.DataOutputStream
import java.net.ServerSocket
import java.net.Socket

@ConditionalOnProperty("simple-socket.enabled", havingValue = "true")
@Component
class SocketListener(
    private val simpleSocketProperties: SimpleSocketProperties,
    private val messageHandleService: MessageHandleService
) {
    companion object {
        private lateinit var serverSocket: ServerSocket
    }

    @Bean
    fun start() {
        val serverSocket = ServerSocket(simpleSocketProperties.port)
        while (true) {
            SocketHandler(messageHandleService, serverSocket.accept()).start()
        }
    }

    fun stop() {
        serverSocket.close()
    }
}

class SocketHandler(
    private val messageHandleService: MessageHandleService,
    private val socket: Socket
) : Thread() {

    companion object {
        private lateinit var dataIn: DataInputStream
        private lateinit var dataOut: DataOutputStream
    }

    override fun run() {
        dataIn = DataInputStream(socket.getInputStream())
        while (true)
            when (dataIn.readInt()) {
                1 -> receiveRequestMessage(dataIn, socket)
                2 -> receiveDataMessage(dataIn)
                else -> break
            }
        socket.close()
        dataIn.close()
        dataOut.close()
    }

    private fun receiveRequestMessage(dataIn: DataInputStream, socket: Socket) {
        dataOut = DataOutputStream(socket.getOutputStream())
        dataOut.writeUTF(messageHandleService.receiveRequestMessage(dataIn.readUTF()) ?: "")
        dataOut.flush()
    }

    private fun receiveDataMessage(dataIn: DataInputStream) {
        messageHandleService.receiveDataMessage(
            Gson().fromJson(
                dataIn.readUTF(),
                DataMessageDTO::class.java
            )
        )
    }
}