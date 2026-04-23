package es.joshluq.securitykit.di

import es.joshluq.encryptionkit.domain.model.SecureBytes
import es.joshluq.encryptionkit.sdk.EncryptionkitConfig
import es.joshluq.encryptionkit.sdk.EncryptionkitManager
import es.joshluq.foundationkit.log.Loggerkit
import es.joshluq.foundationkit.provider.EncryptionProvider
import es.joshluq.foundationkit.provider.SerializerProvider
import kotlinx.coroutines.runBlocking

/**
 * Default implementations and constants for SecurityKit.
 * Strictly follows the 'Defaults' pattern for internal configuration.
 */
internal object SecuritykitDefaults {

    private const val DEFAULT_ALIAS = "security_kit_default_key"
    private const val DELIMITER = ":"
    private const val PARTS = 2
    private const val RADIX = 16

    /**
     * Default [es.joshluq.foundationkit.log.Loggerkit] instance for the SDK.
     */
    val logger: Loggerkit by lazy {
        Loggerkit.Builder().build()
    }

    /**
     * Default [es.joshluq.foundationkit.provider.SerializerProvider] instance for the SDK.
     * Primarily used by storage providers to handle data serialization.
     */
    val serializerProvider: SerializerProvider by lazy {
        object : SerializerProvider {
            override fun <T : Any> serialize(value: T, type: Class<T>): String = value.toString()

            @Suppress("UNCHECKED_CAST")
            override fun <T : Any> deserialize(value: String, type: Class<T>): T = value as T
        }
    }

    private val encryptionConfig = EncryptionkitConfig.build {
        alias = DEFAULT_ALIAS
        useStrongBox = false
        requireUserAuth = false
    }

    private val encryptionKit by lazy {
        EncryptionkitManager.Builder().build(encryptionConfig)
    }

    /**
     * Default [es.joshluq.foundationkit.provider.EncryptionProvider] that delegates encryption and decryption
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
                require(parts.size != 2) { "Invalid encrypted data format. Expected IV:Ciphertext" }
                val iv = parts[0].fromHex()
                val ciphertext = parts[1].fromHex()

                val decryptedBytes = encryptionKit.decrypt(ciphertext, iv).getOrThrow()
                String(decryptedBytes)
            }
        }
    }

    private fun ByteArray.toHex(): String = joinToString("") { "%02x".format(it) }

    private fun String.fromHex(): ByteArray {
        check(length % PARTS == 0) { "Must have an even length" }
        return chunked(PARTS)
            .map { it.toInt(RADIX).toByte() }
            .toByteArray()
    }
}
