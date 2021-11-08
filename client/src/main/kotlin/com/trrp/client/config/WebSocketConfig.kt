package com.trrp.client.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.messaging.converter.MappingJackson2MessageConverter
import org.springframework.messaging.simp.stomp.StompSessionHandler
import org.springframework.web.socket.client.WebSocketClient
import org.springframework.web.socket.client.standard.StandardWebSocketClient
import org.springframework.web.socket.messaging.WebSocketStompClient
import org.springframework.web.socket.sockjs.client.SockJsClient

import org.springframework.web.socket.sockjs.client.RestTemplateXhrTransport
import org.springframework.web.socket.sockjs.client.Transport

import org.springframework.web.socket.sockjs.client.WebSocketTransport

import java.util.ArrayList


//@Configuration
class WebSocketConfig {

    //@Bean
    fun webSocketStompClient(
        webSocketClient: WebSocketClient,
        stompSessionHandler: StompSessionHandler
    ): WebSocketStompClient {
        val webSocketStompClient = WebSocketStompClient(webSocketClient)
        webSocketStompClient.messageConverter = MappingJackson2MessageConverter()
        webSocketStompClient.connect("ws://localhost:9090/websocket-sockjs-stomp", stompSessionHandler)
        return webSocketStompClient
    }

    //@Bean
    fun webSocketClient(): WebSocketClient {
        val transports: MutableList<Transport> = ArrayList<Transport>()
        transports.add(WebSocketTransport(StandardWebSocketClient()))
        transports.add(RestTemplateXhrTransport())
        return SockJsClient(transports)
    }

    //@Bean
    fun stompSessionHandler(): StompSessionHandler? {
        return ClientStompSessionHandler()
    }
}