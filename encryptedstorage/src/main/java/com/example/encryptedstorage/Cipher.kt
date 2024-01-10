package com.example.encryptedstorage

import android.security.keystore.KeyProperties

class Cipher private constructor(
    val algorithm: String,
    val blockMode: String,
    val padding: String
) {

    data class Builder(
        var algorithm: String = KeyProperties.KEY_ALGORITHM_AES,
        var blockMode: String = KeyProperties.BLOCK_MODE_CBC,
        var padding: String = KeyProperties.ENCRYPTION_PADDING_PKCS7
    ) {
        fun setAlgorithm(algorithm: String) = apply { this.algorithm = algorithm }

        fun setBlockMode(blockMode: String) = apply { this.blockMode = blockMode }

        fun setPadding(padding: String) = apply { this.padding = padding }

        fun build() = Cipher(algorithm, blockMode, padding)
    }
}
