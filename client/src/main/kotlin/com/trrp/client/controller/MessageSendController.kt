package com.trrp.client.controller

import com.trrp.client.service.MessageSendService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
class MessageSendController(
    private val messageSender: MessageSendService
) {

    @GetMapping("/v1")
    fun sendServletMessage() {
        messageSender.sendSocketMessage()
    }

    @GetMapping("/v2")
    fun sendRabbitMessage() {
        messageSender.sendReceiveRMQMessage()
    }
}