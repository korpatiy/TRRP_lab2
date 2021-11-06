package com.trrp.client.model

import java.io.Serializable

/**
 * DTO для передачи сообщения
 */
class DataMessageDTO(
    /** Закодированный DES ключ */
    val decodeKey: String? = null,
    /** Закодированное содержимое */
    val data: String? = null
) : Serializable