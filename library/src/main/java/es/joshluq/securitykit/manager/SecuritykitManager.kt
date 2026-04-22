package es.joshluq.securitykit.manager

import es.joshluq.foundationkit.manager.Manager
import es.joshluq.foundationkit.manager.ManagerBuilder
import es.joshluq.securitykit.data.defaults.SecuritykitDefaults
import es.joshluq.securitykit.di.SecuritykitComponent
import es.joshluq.securitykit.domain.usecase.ReadSecureDataUseCase
import es.joshluq.securitykit.domain.usecase.SaveSecureDataUseCase

/**
 * [SecuritykitManager] is the primary entry point for the SecurityKit SDK.
 *
 * This manager coordinates hardware-backed encryption and secure data persistence
 * by implementing the FoundationKit storage contracts. It orchestrates the internal
 * dependency graph and provides a high-level API for secure operations.
 *
 * @property componentFactory A functional factory to create the [SecuritykitComponent].
 * Primarily used for providing testing backdoors as defined in the governance guidelines.
 */
class SecuritykitManager internal constructor(
    private val componentFactory: (SecuritykitConfig) -> SecuritykitComponent = { SecuritykitComponent(it) }
) : Manager<SecuritykitConfig>() {

    private lateinit var component: SecuritykitComponent

    /**
     * Initializes the manager with the provided configuration.
     * This method sets up the internal component and dependency graph required for the SDK's operation.
     *
     * @param config The [SecuritykitConfig] containing environment and encryption settings.
     */
    fun initialize(config: SecuritykitConfig) {
        this.config = config
        this.component = componentFactory(config)
        component.logger.i(TAG, "SecurityKit initialized successfully.")
    }

    /**
     * Persists data securely using a hardware-backed encryption pipeline.
     *
     * The data is encrypted before being stored in the underlying persistence layer
     * to ensure zero-trust storage.
     *
     * @param key The identifier for the stored data.
     * @param value The plain text data to be encrypted and stored.
     */
    suspend fun save(key: String, value: String) {
        component.logger.d(TAG, "Saving data for key: $key")
        val input = SaveSecureDataUseCase.Input(key, value)
        component.saveSecureDataUseCase(input).onSuccess {
            component.logger.i(TAG, "Data saved successfully for key: $key")
        }.onFailure {
            component.logger.e(TAG, "Failed to save data for key: $key", it)
        }
    }

    /**
     * Retrieves and decrypts data previously stored securely.
     *
     * @param key The identifier for the data to retrieve.
     * @return A [Result] containing the decrypted string on success, or a failure if decryption fails or key is not found.
     */
    suspend fun read(key: String): Result<String> {
        component.logger.d(TAG, "Reading data for key: $key")
        val input = ReadSecureDataUseCase.Input(key)
        return component.readSecureDataUseCase(input).map { it.value }.onSuccess {
            component.logger.i(TAG, "Data retrieved and decrypted for key: $key")
        }.onFailure {
            component.logger.w(TAG, "Failed to retrieve data for key: $key", it)
        }
    }

    /**
     * Companion object that fulfills the [ManagerBuilder] contract for [SecuritykitManager].
     */
    companion object : ManagerBuilder<SecuritykitConfig> {

        private const val TAG = "SecuritykitManager"

        /**
         * Builds and initializes a new instance of [SecuritykitManager].
         *
         * @param config The [SecuritykitConfig] required to configure the SDK.
         * @return A fully initialized [SecuritykitManager] instance.
         */
        override fun build(config: SecuritykitConfig): SecuritykitManager {
            return SecuritykitManager().apply {
                initialize(config)
            }
        }
    }
}
