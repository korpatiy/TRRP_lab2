package com.trrp.server.config

import org.springframework.amqp.core.*
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter
import org.springframework.amqp.support.converter.MessageConverter
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@ConfigurationPropertiesScan
@Configuration
class RabbitConfig(
    private val rabbitMQProperties: RabbitMQProperties
) {

    companion object {
        const val REQUEST_QUEUE = "request-queue"
        const val DATA_QUEUE = "data-queue"
        private const val BASE_EXCHANGE = "base-exchange"
    }

    @Bean
    fun requestQueue(): Queue? {
        return QueueBuilder
            .durable(REQUEST_QUEUE)
            .build()
    }

    @Bean
    fun dataQueue(): Queue? {
        return QueueBuilder
            .durable(DATA_QUEUE)
            .build()
    }

    @Bean
    fun exchange(): Exchange? {
        return ExchangeBuilder
            .topicExchange(BASE_EXCHANGE)
            .build()
    }

    @Bean
    fun requestBinding(): Binding? {
        return BindingBuilder
            .bind(requestQueue())
            .to(exchange())
            .with(REQUEST_QUEUE)
            .noargs()
    }

    @Bean
    fun dataBinding(): Binding? {
        return BindingBuilder
            .bind(dataQueue())
            .to(exchange())
            .with(DATA_QUEUE)
            .noargs()
    }

    @Bean
    fun connectionFactory(): ConnectionFactory? {
        val cachingConnectionFactory = CachingConnectionFactory(rabbitMQProperties.host)
        cachingConnectionFactory.username = rabbitMQProperties.username
        cachingConnectionFactory.setPassword(rabbitMQProperties.password)
        return cachingConnectionFactory
    }

    @Bean
    fun jsonMessageConverter(): MessageConverter {
        return Jackson2JsonMessageConverter()
    }

    @Bean
    fun rabbitTemplate(connectionFactory: ConnectionFactory): RabbitTemplate? {
        val rabbitTemplate = RabbitTemplate(connectionFactory)
        rabbitTemplate.messageConverter = jsonMessageConverter()
        return rabbitTemplate
    }
}