package com.example.encryptedstorage

import java.io.InputStream
import java.io.OutputStream

interface CryptoManager<in T> {
    fun setupEncryptionKeys(cipher: T)
    fun encrypt(byteArray: ByteArray, outputStream: OutputStream): ByteArray
    fun decrypt(inputStream: InputStream): ByteArray
}