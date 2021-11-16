package com.trrp.server.module.service.listeners

import com.trrp.server.model.dtos.DataMessageDTO
import com.trrp.server.module.service.MessageHandleService
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Component

@Component
class RMQListener(
    private val messageHandleService: MessageHandleService
) {

    @RabbitListener(queues = ["request-queue"])
    fun receiveRequestMessage(message: String): String? {
        return messageHandleService.receiveRequestMessage(message)
    }

    @RabbitListener(queues = ["data-queue"])
    fun receiveDataMessage(dataMessageDTO: DataMessageDTO) {
        messageHandleService.receiveDataMessage(dataMessageDTO)
    }
}