package com.trrp.client.config

import com.rabbitmq.client.ConnectionFactory
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter

import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.amqp.support.converter.MessageConverter
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@ConfigurationPropertiesScan
@Configuration
class RabbitConfig(
    private val rabbitMQProperties: RabbitMQProperties
) {

    @Bean
    fun connectionFactory(): org.springframework.amqp.rabbit.connection.ConnectionFactory? {
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
    fun rabbitTemplate(connectionFactory: org.springframework.amqp.rabbit.connection.ConnectionFactory): RabbitTemplate? {
        val rabbitTemplate = RabbitTemplate(connectionFactory)
        rabbitTemplate.messageConverter = jsonMessageConverter()
        return rabbitTemplate
    }
}