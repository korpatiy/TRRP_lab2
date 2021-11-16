package com.trrp.client.config

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.messaging.simp.stomp.StompCommand
import org.springframework.messaging.simp.stomp.StompHeaders
import org.springframework.messaging.simp.stomp.StompSession
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter
import org.springframework.stereotype.Component

@Component
class ClientStompSessionHandler : StompSessionHandlerAdapter() {

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(ClientStompSessionHandler::class.java)
        private var stompSession: StompSession? = null
    }


    override fun afterConnected(session: StompSession, headers: StompHeaders) {
        logger.info("Client connected: headers {}", headers)
        session.subscribe("/queue/responses", this)
        session.subscribe("/queue/errors", this)
        val message = "Hi from client PC!"
        stompSession = session
        session.send("/app/request", message)
    }

    override fun handleFrame(headers: StompHeaders, payload: Any?) {
        val s = payload as String
        logger.info("Client received: payload {}, headers {}", payload.toString(), headers)
    }

    override fun handleException(
        session: StompSession, command: StompCommand?,
        headers: StompHeaders, payload: ByteArray, exception: Throwable
    ) {
        logger.error(
            "Client error: exception {}, command {}, payload {}, headers {}",
            exception.message, command, payload, headers
        )
    }

    override fun handleTransportError(session: StompSession, exception: Throwable) {
        logger.error("Client transport error: error {}", exception.message)
    }
}