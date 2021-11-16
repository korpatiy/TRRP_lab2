package com.trrp.client.controller

import com.trrp.client.service.SendReceiveService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/api")
class MessageSendController(
    private val senderReceive: SendReceiveService
) {

    @GetMapping("/{type}")
    fun sendServletMessage(@PathVariable type: String){
        senderReceive.sendMessageByType(type)
    }
}