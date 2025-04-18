package com.example.caferose.model

import java.security.MessageDigest

class HashHelper {

    private fun hashMessage(message: String, algorithm: String = "SHA-256"): String {
        // Create MessageDigest instance with the specified algorithm (default is SHA-256)
        val digest = MessageDigest.getInstance(algorithm)
        // Convert the message to bytes and hash it
        val hashBytes = digest.digest(message.toByteArray())
        // Convert hash bytes to hexadecimal format
        return hashBytes.joinToString("") { "%02x".format(it) }
    }

    fun getHashCode(message: String): String {
        return hashMessage(message)
    }

}