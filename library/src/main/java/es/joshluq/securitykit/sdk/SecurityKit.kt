package es.joshluq.securitykit.sdk

import es.joshluq.foundationkit.manager.Manager
import es.joshluq.foundationkit.manager.ManagerBuilder
import es.joshluq.securitykit.di.SecurityKitComponent
import es.joshluq.securitykit.domain.usecase.ReadSecureDataUseCase
import es.joshluq.securitykit.domain.usecase.SaveSecureDataUseCase

/**
 * [SecurityKit] is the primary entry point for the SecurityKit SDK.
 *
 * This manager coordinates hardware-backed encryption and secure data persistence
 * by implementing the FoundationKit storage contracts. It orchestrates the internal
 * dependency graph and provides a high-level API for secure operations.
 *
 * @property componentFactory A functional factory to create the [SecurityKitComponent].
 * Primarily used for providing testing backdoors as defined in the governance guidelines.
 */
class SecurityKit internal constructor(
    config: SecurityKitConfig,
    private val componentFactory: (SecurityKitConfig) -> SecurityKitComponent = { SecurityKitComponent(it) }
) : Manager<SecurityKitConfig>() {

    companion object {
        private const val TAG = "SecurityKit"
    }

    private lateinit var component: SecurityKitComponent

    init {
        this.config = config
    }

    /**
     * Initializes the manager with the provided configuration.
     * This method sets up the internal component and dependency graph required for the SDK's operation.
     */
    internal fun initialize() {
        this.component = componentFactory(config)
        component.logger.i(TAG, "SecurityKit initialized successfully.")
    }

    /**
     * Persists data securely using a hardware-backed encryption pipeline.
     *
     * The data is serialized (if necessary) and encrypted before being stored
     * in the underlying persistence layer to ensure zero-trust storage.
     *
     * @param T The type of the data to be stored.
     * @param key The identifier for the stored data.
     * @param value The data to be encrypted and stored.
     * @param type The class type of the value for serialization purposes.
     * @return A [Result] indicating success or failure of the save operation.
     */
    suspend fun <T : Any> save(key: String, value: T, type: Class<T>): Result<Unit> {
        if (!isConfigInitialized()) {
            return Result.failure(Exception("Config not initialized"))
        }
        component.logger.d(TAG, "Saving data for key: $key")
        val input = SaveSecureDataUseCase.Input(key, value, type)
        return component.saveSecureDataUseCase(input).onSuccess {
            component.logger.i(TAG, "Data saved successfully for key: $key")
        }.onFailure {
            component.logger.e(TAG, "Failed to save data for key: $key", it)
        }.map { }
    }

    /**
     * Persists data securely using a hardware-backed encryption pipeline.
     * This is a Kotlin-friendly extension that automatically infers the type.
     *
     * @param T The type of the data to be stored.
     * @param key The identifier for the stored data.
     * @param value The data to be encrypted and stored.
     * @return A [Result] indicating success or failure of the save operation.
     */
    suspend inline fun <reified T : Any> save(key: String, value: T): Result<Unit> =
        save(key, value, T::class.java)

    /**
     * Retrieves and decrypts data previously stored securely.
     *
     * The data is read from storage, decrypted, and then deserialized back
     * into the specified type.
     *
     * @param T The expected type of the data.
     * @param key The identifier for the data to retrieve.
     * @param type The class type of the value for deserialization.
     * @return A [Result] containing the decrypted and deserialized data on success,
     * or a failure if decryption/deserialization fails or the key is not found.
     */
    suspend fun <T : Any> read(key: String, type: Class<T>): Result<T> {
        if (!isConfigInitialized()) {
            return Result.failure(Exception("Config not initialized"))
        }
        component.logger.d(TAG, "Reading data for key: $key")
        val input = ReadSecureDataUseCase.Input(key, type)
        @Suppress("UNCHECKED_CAST")
        return component.readSecureDataUseCase(input).map { it.value as T }.onSuccess {
            component.logger.i(TAG, "Data retrieved and decrypted for key: $key")
        }.onFailure {
            component.logger.w(TAG, "Failed to retrieve data for key: $key", it)
        }
    }

    /**
     * Retrieves and decrypts data previously stored securely.
     * This is a Kotlin-friendly extension that automatically infers the type.
     *
     * @param T The expected type of the data.
     * @param key The identifier for the data to retrieve.
     * @return A [Result] containing the decrypted and deserialized data on success,
     * or a failure if decryption/deserialization fails or the key is not found.
     */
    suspend inline fun <reified T : Any> read(key: String): Result<T> =
        read(key, T::class.java)

    /**
     * Class that fulfills the [ManagerBuilder] contract for [SecurityKit].
     */
    class Builder : ManagerBuilder<SecurityKitConfig> {

        /**
         * Builds and initializes a new instance of [SecurityKit].
         *
         * @param config The [SecurityKitConfig] required to configure the SDK.
         * @return A fully initialized [SecurityKit] instance.
         */
        override fun build(config: SecurityKitConfig): SecurityKit {
            return SecurityKit(config).apply { initialize() }
        }
    }
}
