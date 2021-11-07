package com.trrp.server.module.service.listeners

import com.trrp.server.model.dtos.DataMessageDTO
import com.trrp.server.module.service.MessageReceiveService
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Component

@Component
class RMQListener(
    private val messageReceiveService: MessageReceiveService
) {

    @RabbitListener(queues = ["request-queue"])
    fun receiveRequestMessage(message: String): String? {
        return messageReceiveService.receiveRequestMessage(message)
    }

    @RabbitListener(queues = ["data-queue"])
    fun receiveDataMessage(dataMessageDTO: DataMessageDTO) {
        messageReceiveService.receiveDataMessage(dataMessageDTO)
    }
}