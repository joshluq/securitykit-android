package es.joshluq.securitykit.manager

import android.content.Context
import es.joshluq.foundationkit.log.Loggerkit
import es.joshluq.foundationkit.manager.ManagerConfig
import es.joshluq.foundationkit.provider.EncryptionProvider
import es.joshluq.foundationkit.provider.SerializerProvider
import es.joshluq.securitykit.di.SecuritykitDefaults

/**
 * Configuration for SecurityKit.
 *
 * @property context Application context for SDK initialization.
 * @property encryptionProvider The provider used for data encryption/decryption.
 * @property logger The logger instance for the SDK.
 * @property serializerProvider The provider for data serialization.
 * @property storeName Optional name for the secure storage.
 */
data class SecuritykitConfig(
    val context: Context,
    val encryptionProvider: EncryptionProvider,
    val logger: Loggerkit,
    val serializerProvider: SerializerProvider,
    val storeName: String = "security_kit_store"
) : ManagerConfig {
    /**
     * Builder class for [SecuritykitConfig].
     */

    class Builder {
        var context: Context? = null
        var encryptionProvider: EncryptionProvider = SecuritykitDefaults.encryptionProvider
        var logger: Loggerkit = SecuritykitDefaults.logger
        var serializerProvider: SerializerProvider = SecuritykitDefaults.serializerProvider
        var storeName: String = "security_kit_store"

        fun build() = SecuritykitConfig(
            context = checkNotNull(context) { "Context is required for SecurityKit initialization." },
            encryptionProvider = encryptionProvider,
            logger = logger,
            serializerProvider = serializerProvider,
            storeName = storeName
        )
    }

    companion object {
        /**
         * DSL entry point for creating an [SecuritykitConfig] instance.
         */
        inline fun build(context: Context, block: Builder.() -> Unit): SecuritykitConfig =
            Builder().apply { this.context = context }.apply(block).build()
    }
}
