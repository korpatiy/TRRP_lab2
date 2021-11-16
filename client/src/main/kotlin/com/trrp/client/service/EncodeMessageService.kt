package com.trrp.client.service

import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.trrp.client.model.DataMessageDTO
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.nio.charset.StandardCharsets
import java.security.KeyFactory
import java.security.spec.X509EncodedKeySpec
import java.sql.DriverManager
import java.util.*
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.spec.SecretKeySpec

@Service
class EncodeMessageService {

    companion object {
        private const val DB_URL = "jdbc:sqlite:client/src/main/resources/biathlon-lite.db"
    }

    fun extractData(): JsonArray {
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

    fun encodeMessage(publicRSAKey: String): DataMessageDTO {
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
}