package com.trrp.client.controller

import com.trrp.client.config.ClientStompSessionHandler
import com.trrp.client.service.MessageSendService
import org.springframework.messaging.converter.MappingJackson2MessageConverter
import org.springframework.messaging.converter.StringMessageConverter
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.socket.client.standard.StandardWebSocketClient
import org.springframework.web.socket.messaging.WebSocketStompClient
import org.springframework.web.socket.sockjs.client.RestTemplateXhrTransport
import org.springframework.web.socket.sockjs.client.SockJsClient
import org.springframework.web.socket.sockjs.client.Transport
import org.springframework.web.socket.sockjs.client.WebSocketTransport
import java.util.ArrayList


@RestController
@RequestMapping("/api")
class MessageSendController(
    private val messageSender: MessageSendService,
    private val clientStompSessionHandler: ClientStompSessionHandler
) {

    @GetMapping("/v1")
    fun sendServletMessage() {
        val transports: MutableList<Transport> = ArrayList<Transport>()
        transports.add(WebSocketTransport(StandardWebSocketClient()))
        transports.add(RestTemplateXhrTransport())
        val webSocketStompClient = WebSocketStompClient(SockJsClient(transports))
        webSocketStompClient.messageConverter = StringMessageConverter()
        webSocketStompClient.connect("ws://localhost:9090/websocket-sockjs-stomp", clientStompSessionHandler)
        clientStompSessionHandler.send()
    }

    @GetMapping("/v2")
    fun sendRabbitMessage() {
        messageSender.sendReceiveRMQMessage()
    }
}