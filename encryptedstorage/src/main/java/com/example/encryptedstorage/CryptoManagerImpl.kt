package com.example.encryptedstorage

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.io.InputStream
import java.io.OutputStream
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec

class CryptoManagerImpl: CryptoManager<com.example.encryptedstorage.Cipher.Builder> {

    private lateinit var algorithm: String
    private lateinit var blockMode: String
    private lateinit var padding: String
    private lateinit var transformation: String

    override fun setupEncryptionKeys(cipher: com.example.encryptedstorage.Cipher.Builder) {
        algorithm = cipher.algorithm
        blockMode = cipher.blockMode
        padding = cipher.padding
        transformation = "$algorithm/$blockMode/$padding"
    }

    override fun encrypt(byteArray: ByteArray, outputStream: OutputStream): ByteArray {
        val encryptedBytes = encryptCipher.doFinal(byteArray)

        outputStream.use {
            it.write(encryptCipher.iv.size)
            it.write(encryptCipher.iv)
            it.write(encryptedBytes.size)
            it.write(encryptedBytes)
        }
        return encryptedBytes
    }

    override fun decrypt(inputStream: InputStream): ByteArray {
        return inputStream.use {
            val ivSize = it.read()
            val iv = ByteArray(ivSize)
            it.read(iv)

            val encryptedByteSize = it.read()
            val encryptedBytes = ByteArray(encryptedByteSize)
            it.read(encryptedBytes)

            decryptCipher(iv).doFinal(encryptedBytes)
        }
    }

    private val keyStore = KeyStore.getInstance("AndroidKeyStore").apply {
        load(null)
    }

    private val encryptCipher = Cipher.getInstance(transformation).apply {
        init(Cipher.ENCRYPT_MODE, getKey())
    }

    private fun decryptCipher(iv: ByteArray) = Cipher.getInstance(transformation).apply {
        init(Cipher.DECRYPT_MODE, getKey(), IvParameterSpec(iv))
    }

    private fun getKey(): SecretKey {
        val existingKey = keyStore.getEntry("secret", null) as? KeyStore.SecretKeyEntry
        return existingKey?.secretKey ?: createKey()
    }

    private fun createKey() = KeyGenerator.getInstance(algorithm).apply {
        init(
            KeyGenParameterSpec.Builder(
                "secret",
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(blockMode)
                .setEncryptionPaddings(padding)
                .setUserAuthenticationRequired(false)
                .setRandomizedEncryptionRequired(true)
                .build()
        )
    }.generateKey()
}