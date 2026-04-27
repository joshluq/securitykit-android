package es.joshluq.securitykit.data.repository

import es.joshluq.foundationkit.log.Loggerkit
import es.joshluq.foundationkit.provider.StorageProvider
import es.joshluq.securitykit.domain.repository.SecurityRepository

internal class SecurityRepositoryImpl(
    private val storageProvider: StorageProvider,
    private val logger: Loggerkit
) : SecurityRepository {

    companion object {
        private const val TAG = "SecurityRepository"
    }

    override suspend fun <T : Any> save(key: String, value: T, type: Class<T>) {
        logger.d(TAG, "Encrypting and saving data for key: $key")
        storageProvider.save(key, value, type)
    }

    override suspend fun <T : Any> read(key: String, type: Class<T>): T? {
        logger.d(TAG, "Checking storage for key: $key")
        val value = storageProvider.read(key, type)
        return value
    }
}
