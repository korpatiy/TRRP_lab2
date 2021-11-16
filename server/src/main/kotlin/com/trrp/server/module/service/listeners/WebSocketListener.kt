package com.trrp.server.module.service.listeners

import com.trrp.server.model.dtos.DataMessageDTO
import com.trrp.server.module.service.MessageHandleService
import org.slf4j.LoggerFactory
import org.springframework.messaging.handler.annotation.MessageExceptionHandler
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.SendTo
import org.springframework.stereotype.Component
import org.springframework.stereotype.Controller

@Controller
@Component
class WebSocketListener(
    private val messageHandleService: MessageHandleService
) {

    private val logger = LoggerFactory.getLogger(WebSocketListener::class.java)

    @MessageMapping("/request-without-response")
    fun handleMessageWithoutResponse(dataMessageDTO: DataMessageDTO) {
        messageHandleService.receiveDataMessage(dataMessageDTO)
    }

    @MessageMapping("/request")
    @SendTo("/queue/responses")
    fun handleMessageWithResponse(message: String): String? {
        return messageHandleService.receiveRequestMessage(message)
    }

    @MessageExceptionHandler
    @SendTo("/queue/errors")
    fun handleException(exception: Throwable): String? {
        logger.error("[SERVER] : exception", exception)
        return "server exception: " + exception.message
    }
}