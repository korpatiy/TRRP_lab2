package com.trrp.client.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.trrp.client.model.DataMessageDTO
import org.slf4j.LoggerFactory
import org.springframework.amqp.core.Message
import org.springframework.amqp.core.MessageBuilder
import org.springframework.amqp.core.MessageProperties
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.stereotype.Service
import java.io.DataInputStream
import java.net.Socket
import java.nio.charset.StandardCharsets
import java.security.KeyFactory
import java.security.spec.X509EncodedKeySpec
import java.sql.DriverManager
import java.util.*
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.spec.SecretKeySpec

import java.io.DataOutputStream

@Service
class MessageSendService(
    private val rabbitTemplate: RabbitTemplate,
    private val objectMapper: ObjectMapper
) {

    companion object {
        private const val REQUEST_QUEUE = "request-queue"
        private const val DATA_QUEUE = "data-queue"
        private val logger = LoggerFactory.getLogger(MessageSendService::class.java)
        private const val DB_URL = "jdbc:sqlite:client/src/main/resources/biathlon-lite.db"
    }

    private fun extractData(): JsonArray {
        Class.forName("org.sqlite.JDBC")
        val conn =
            DriverManager.getConnection(DB_URL)
        val createStatement = conn.createStatement()
        val resultSet = createStatement.executeQuery("select * from t_biathlon")

        val jsonArray = JsonArray()
        while (resultSet.next()) {
            val totalColumns = resultSet.metaData.columnCount
            val obj = JsonObject()
            for (i in 0 until totalColumns) {
                val toJsonTree = Gson().toJsonTree(resultSet.getObject(i + 1))
                obj.add(resultSet.metaData.getColumnLabel(i + 1), toJsonTree)
            }
            jsonArray.add(obj)
        }

        resultSet.close()
        conn.close()
        return jsonArray
    }

    private fun encodeMessage(publicRSAKey: String): DataMessageDTO {
        /* Декодирование RSA ключа */
        val keySpecRSA = X509EncodedKeySpec(Base64.getDecoder().decode(publicRSAKey.toByteArray()))
        val publicKey = KeyFactory.getInstance("RSA").generatePublic(keySpecRSA)

        /* Генерация DES ключа */
        val desKey = KeyGenerator.getInstance("DES").generateKey()
        val secretDESKey = Base64.getEncoder().encodeToString(desKey.encoded)

        //logger.info("[CLIENT] : secretDESKey $secretDESKey")

        /* Шифрование DES с помощью RSA */
        val cipherRSA = Cipher.getInstance("RSA")
        cipherRSA.init(Cipher.ENCRYPT_MODE, publicKey)
        val encryptedDES = cipherRSA.doFinal(secretDESKey.toByteArray())

        /* Шифрование данных с помощью DES */
        val cipherDES: Cipher = Cipher.getInstance("DES")
        val decode = Base64.getDecoder().decode(secretDESKey)
        cipherDES.init(Cipher.ENCRYPT_MODE, SecretKeySpec(decode, "DES"))
        val encryptedMessageBytes = cipherDES.doFinal(extractData().toString().toByteArray(StandardCharsets.UTF_8))

        return DataMessageDTO(
            decodeKey = Base64.getEncoder().encodeToString(encryptedDES),
            data = Base64.getEncoder().encodeToString(encryptedMessageBytes)
        )
    }

    fun sendSocketMessage() {
        logger.info("[CLIENT] : отправка запроса на получение RSA ключа")
        val socket = Socket("localhost", 9090)
        socket.soTimeout = 6000000

        val dataOut = DataOutputStream(socket.getOutputStream())
        val dataIn = DataInputStream(socket.getInputStream())

        dataOut.writeInt(1)
        dataOut.writeUTF("Hi from client PC [SOCKET MESSAGE]")
        dataOut.flush()

        val publicRSAKey = dataIn.readUTF()
        logger.info("[CLIENT] : шифрование и отправка данных")
        val encodeMessage = encodeMessage(publicRSAKey)

        dataOut.writeInt(2)
        val toJson = Gson().toJson(encodeMessage)
        dataOut.writeUTF(toJson)
        dataOut.flush()

        dataOut.writeInt(-1)
        dataOut.flush()

        dataIn.close()
        dataOut.close()
        socket.close()
    }


    fun sendReceiveRMQMessage() {
        logger.info("[CLIENT] : отправка запроса на получение RSA ключа")
        val publicRSAKey: String?
        try {
            publicRSAKey =
                rabbitTemplate.convertSendAndReceive(REQUEST_QUEUE, "Hi from client PC! [RMQ MESSAGE]") as String?
        } catch (e: Exception) {
            logger.warn("[CLIENT] : ошибка при отправке или получении запроса к серверу")
            return
        }

        logger.info("[CLIENT] : шифрование и отправка данных")
        val preparedMessage = publicRSAKey?.let { encodeMessage(it) }
        if (preparedMessage != null) {
            val orderJson: String = objectMapper.writeValueAsString(preparedMessage)
            val message1: Message = MessageBuilder
                .withBody(orderJson.toByteArray())
                .setContentType(MessageProperties.CONTENT_TYPE_JSON)
                .build()
            try {
                rabbitTemplate.convertAndSend(DATA_QUEUE, message1)
            } catch (e: Exception) {
                logger.warn("[CLIENT] : ошибка при отправке или получении запроса к серверу")
                return
            }
            logger.info("[CLIENT] : данные отправлены")
        }
    }
}


