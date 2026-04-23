package es.joshluq.securitykit.di

import es.joshluq.foundationkit.log.Loggerkit
import es.joshluq.foundationkit.provider.SerializerProvider
import es.joshluq.foundationkit.provider.StorageProvider
import es.joshluq.foundationkit.storage.SharedPreferencesStorageProvider
import es.joshluq.securitykit.data.repository.SecurityRepositoryImpl
import es.joshluq.securitykit.domain.repository.SecurityRepository
import es.joshluq.securitykit.domain.usecase.ReadSecureDataUseCase
import es.joshluq.securitykit.domain.usecase.SaveSecureDataUseCase
import es.joshluq.securitykit.manager.SecuritykitConfig

/**
 * Internal dependency graph for SecurityKit.
 * Strictly follows the Zero-Dependency DI rule from AGENTS.md.
 */
internal class SecuritykitComponent(
    private val config: SecuritykitConfig
) {
    // Logger
    val logger: Loggerkit by lazy { config.logger }

    // Serializer
    val serializer: SerializerProvider by lazy { config.serializerProvider }

    // Storage
    val storageProvider: StorageProvider by lazy {
        val sharedPrefs = config.context.getSharedPreferences(config.storeName, android.content.Context.MODE_PRIVATE)
        SharedPreferencesStorageProvider(sharedPrefs, serializer)
    }

    // Repository
    val securityRepository: SecurityRepository by lazy {
        SecurityRepositoryImpl(
            encryptionProvider = config.encryptionProvider,
            storageProvider = storageProvider,
            logger = logger
        )
    }

    // Use Cases
    val saveSecureDataUseCase: SaveSecureDataUseCase by lazy {
        SaveSecureDataUseCase(securityRepository, logger)
    }

    val readSecureDataUseCase: ReadSecureDataUseCase by lazy {
        ReadSecureDataUseCase(securityRepository, logger)
    }
}
