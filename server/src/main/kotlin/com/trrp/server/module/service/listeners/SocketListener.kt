package com.trrp.server.module.service.listeners

import com.google.gson.Gson
import com.trrp.server.model.RSAGen
import com.trrp.server.model.dtos.DataMessageDTO
import com.trrp.server.module.service.MessageReceiveService
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import java.io.DataInputStream
import java.io.DataOutputStream
import java.net.ServerSocket
import java.net.Socket

@Component
class SocketListener(
    private val messageReceiveService: MessageReceiveService
) {

    private val logger = LoggerFactory.getLogger(SocketListener::class.java)

    @Bean
    fun serverBuild() {
        val serverPort = 9091
        val serverSocket = ServerSocket(serverPort)
        serverSocket.soTimeout = 600000
        logger.info("[SERVER] : слушаю сообщения через сокеты...")
        val socket: Socket = serverSocket.accept()
        val dataIn = DataInputStream(socket.getInputStream())
        val dataOut = DataOutputStream(socket.getOutputStream())
        while (true) {
            when (dataIn.readInt()) {
                1 -> {
                    dataOut.writeUTF(messageReceiveService.receiveRequestMessage(dataIn.readUTF()) ?: "")
                    dataOut.flush()
                }
                2 -> {
                    messageReceiveService.receiveDataMessage(
                        Gson().fromJson(
                            dataIn.readUTF(),
                            DataMessageDTO::class.java
                        )
                    )
                }
                else -> break
            }
        }
        dataIn.close()
        dataOut.close()
        socket.close()
    }
}