package es.joshluq.securitykit.manager

import android.content.Context
import es.joshluq.foundationkit.manager.ManagerConfig
import es.joshluq.foundationkit.provider.EncryptionProvider

import es.joshluq.securitykit.data.defaults.SecuritykitDefaults

/**
 * Configuration for SecurityKit.
 *
 * @property context Application context for DataStore initialization.
 * @property encryptionProvider The provider used for data encryption/decryption.
 * @property storeName Optional name for the secure storage.
 */
data class SecuritykitConfig(
    val context: Context,
    val encryptionProvider: EncryptionProvider,
    val storeName: String = "security_kit_store"
) : ManagerConfig {
    /**
     * Builder class for [SecuritykitConfig].
     */

    companion object {
        /**
         * DSL entry point for creating an [SecuritykitConfig] instance.
         */
        inline fun build(block: Builder.() -> Unit): SecuritykitConfig =
            Builder().apply(block).build()
    }

    class Builder {
        var context: Context? = null
        var encryptionProvider: EncryptionProvider = SecuritykitDefaults.encryptionProvider
        var storeName: String = "security_kit_store"
        fun build() = SecuritykitConfig(
            context = context ?: throw IllegalStateException("Context is required"),
            encryptionProvider = encryptionProvider,
            storeName = storeName
        )
    }
}
