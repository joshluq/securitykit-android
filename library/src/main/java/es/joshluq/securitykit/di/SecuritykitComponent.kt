package es.joshluq.securitykit.di

import es.joshluq.foundationkit.log.Loggerkit
import es.joshluq.securitykit.data.defaults.SecuritykitDefaults
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
    val logger: Loggerkit by lazy { Loggerkit.Builder().build() }

    // Repository
    val securityRepository: SecurityRepository by lazy {
        SecurityRepositoryImpl(
            context = config.context,
            encryptionProvider = config.encryptionProvider,
            storeName = config.storeName,
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
