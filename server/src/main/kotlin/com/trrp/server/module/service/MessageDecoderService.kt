package com.trrp.server.module.service

import com.trrp.server.model.RSAGen
import com.trrp.server.model.dtos.DataMessageDTO
import org.springframework.stereotype.Service
import java.nio.charset.StandardCharsets
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

@Service
class MessageDecoderService {

    fun decodeMessage(dataMessageDTO: DataMessageDTO, rsaGen: RSAGen): String {
        val decodeKey = Base64.getDecoder().decode(dataMessageDTO.decodeKey)
        val cipherRSA = Cipher.getInstance("RSA")
        cipherRSA.init(Cipher.DECRYPT_MODE, rsaGen.privateKey)
        val decryptedDES = cipherRSA.doFinal(decodeKey)

        val decryptCipher = Cipher.getInstance("DES")
        val decodeDES = Base64.getDecoder().decode(decryptedDES)
        val secretKeySpec = SecretKeySpec(decodeDES, "DES")
        decryptCipher.init(Cipher.DECRYPT_MODE, secretKeySpec)

        val decryptedMessageBytes = decryptCipher.doFinal(Base64.getDecoder().decode(dataMessageDTO.data))
        return String(decryptedMessageBytes, StandardCharsets.UTF_8)
    }
}