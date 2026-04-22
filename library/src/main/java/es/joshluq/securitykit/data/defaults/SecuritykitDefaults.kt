package es.joshluq.securitykit.data.defaults

import es.joshluq.encryptionkit.domain.model.SecureBytes
import es.joshluq.encryptionkit.sdk.EncryptionConfig
import es.joshluq.encryptionkit.sdk.EncryptionkitManager
import es.joshluq.foundationkit.log.Loggerkit
import es.joshluq.foundationkit.provider.EncryptionProvider
import kotlinx.coroutines.runBlocking

/**
 * Default implementations and constants for SecurityKit.
 * Strictly follows the 'Defaults' pattern for internal configuration.
 */
internal object SecuritykitDefaults {

    private const val DEFAULT_ALIAS = "security_kit_default_key"
    private const val DELIMITER = ":"

    private val encryptionConfig = EncryptionConfig.build {
        alias = DEFAULT_ALIAS
        useStrongBox = false
        requireUserAuth = false
    }

    private val encryptionKit by lazy {
        EncryptionkitManager.Builder().build(encryptionConfig)
    }

    /**
     * Default [EncryptionProvider] that delegates encryption and decryption
     * to the EncryptionKit module.
     *
     * It uses a symmetric AES-GCM scheme provided by EncryptionKit and encodes
     * the result (IV + Ciphertext) into a Hex-delimited string.
     */
    val encryptionProvider: EncryptionProvider by lazy {
        object : EncryptionProvider {

            init {
                encryptionKit
            }

            override fun encrypt(data: String): String = runBlocking {
                val secureBytes = SecureBytes(data.toByteArray())
                val result = encryptionKit.encrypt(secureBytes).getOrThrow()
                
                val ivHex = result.iv.toHex()
                val ciphertextHex = result.ciphertext.toHex()
                
                "$ivHex$DELIMITER$ciphertextHex"
            }

            override fun decrypt(data: String): String = runBlocking {
                val parts = data.split(DELIMITER)
                if (parts.size != 2) {
                    throw IllegalArgumentException("Invalid encrypted data format. Expected IV:Ciphertext")
                }
                
                val iv = parts[0].fromHex()
                val ciphertext = parts[1].fromHex()
                
                val decryptedBytes = encryptionKit.decrypt(ciphertext, iv).getOrThrow()
                String(decryptedBytes)
            }
        }
    }

    private fun ByteArray.toHex(): String = joinToString("") { "%02x".format(it) }

    private fun String.fromHex(): ByteArray {
        check(length % 2 == 0) { "Must have an even length" }
        return chunked(2)
            .map { it.toInt(16).toByte() }
            .toByteArray()
    }
}
