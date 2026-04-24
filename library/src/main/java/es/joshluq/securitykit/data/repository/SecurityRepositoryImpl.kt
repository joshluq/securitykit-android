package es.joshluq.securitykit.data.repository

import es.joshluq.foundationkit.log.Loggerkit
import es.joshluq.foundationkit.provider.EncryptionProvider
import es.joshluq.foundationkit.provider.StorageProvider
import es.joshluq.foundationkit.provider.read
import es.joshluq.foundationkit.provider.save
import es.joshluq.securitykit.domain.repository.SecurityRepository

internal class SecurityRepositoryImpl(
    private val encryptionProvider: EncryptionProvider,
    private val storageProvider: StorageProvider,
    private val logger: Loggerkit
) : SecurityRepository {

    companion object {
        private const val TAG = "SecurityRepository"
    }

    override suspend fun save(key: String, value: String) {
        logger.d(TAG, "Encrypting and saving data for key: $key")
        val encryptedValue = encryptionProvider.encrypt(value)
        storageProvider.save(key, encryptedValue)
        logger.i(TAG, "Data securely persisted for key: $key")
    }

    override fun read(key: String): String? {
        logger.d(TAG, "Checking storage for key: $key")
        val encryptedValue: String = storageProvider.read(key) ?: return null

        logger.d(TAG, "Encrypted data found for key: $key. Decrypting...")
        val decrypted = encryptionProvider.decrypt(encryptedValue)
        logger.i(TAG, "Data successfully decrypted for key: $key")
        return decrypted
    }
}
