package es.joshluq.securitykit.sdk

import android.content.Context
import es.joshluq.foundationkit.log.Loggerkit
import es.joshluq.foundationkit.manager.ManagerConfig
import es.joshluq.foundationkit.provider.EncryptionProvider
import es.joshluq.foundationkit.provider.SerializerProvider
import es.joshluq.securitykit.di.SecurityKitDefaults

/**
 * Configuration for SecurityKit.
 *
 * @property context Application context for SDK initialization.
 * @property encryptionProvider The provider used for data encryption/decryption.
 * @property serializerProvider The provider for data serialization.
 * @property storeName Optional name for the secure storage.
 * @property logger The logger instance for the SDK.
 */
data class SecurityKitConfig(
    val context: Context,
    val encryptionProvider: EncryptionProvider,
    val serializerProvider: SerializerProvider,
    val storeName: String,
    val logger: Loggerkit
) : ManagerConfig {

    /**
     * Builder class for [SecurityKitConfig].
     */
    class Builder {
        var context: Context? = null
        var encryptionProvider: EncryptionProvider = SecurityKitDefaults.encryptionProvider
        var logger: Loggerkit = SecurityKitDefaults.logger
        var serializerProvider: SerializerProvider = SecurityKitDefaults.serializerProvider
        var storeName: String = "security_kit_store"

        fun build() = SecurityKitConfig(
            context = checkNotNull(context) { "Context is required for SecurityKit initialization." },
            encryptionProvider = encryptionProvider,
            logger = logger,
            serializerProvider = serializerProvider,
            storeName = storeName
        )
    }

    companion object {
        /**
         * DSL entry point for creating an [SecurityKitConfig] instance.
         */
        inline fun build(context: Context, block: Builder.() -> Unit): SecurityKitConfig =
            Builder().apply { this.context = context }.apply(block).build()
    }
}
