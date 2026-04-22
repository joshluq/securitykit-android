package es.joshluq.securitykit.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import es.joshluq.foundationkit.log.Loggerkit
import es.joshluq.foundationkit.provider.EncryptionProvider
import es.joshluq.securitykit.data.defaults.SecuritykitDefaults
import es.joshluq.securitykit.domain.repository.SecurityRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class SecurityRepositoryImpl(
    private val context: Context,
    private val encryptionProvider: EncryptionProvider,
    storeName: String,
    private val logger: Loggerkit
) : SecurityRepository {

    companion object {
        private const val TAG = "SecurityRepository"
    }

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = storeName)

    override suspend fun save(key: String, value: String) {
        logger.d(TAG, "Encrypting and saving data for key: $key")
        val encryptedValue = encryptionProvider.encrypt(value)
        val dataStoreKey = stringPreferencesKey(key)
        context.dataStore.edit { preferences ->
            preferences[dataStoreKey] = encryptedValue
        }
        logger.i(TAG, "Data securely persisted for key: $key")
    }

    override fun read(key: String): Flow<String?> {
        val dataStoreKey = stringPreferencesKey(key)
        return context.dataStore.data.map { preferences ->
            val encryptedValue = preferences[dataStoreKey]
            if (encryptedValue != null) {
                logger.d(TAG, "Encrypted data found for key: $key. Decrypting...")
                val decrypted = encryptionProvider.decrypt(encryptedValue)
                logger.i(TAG, "Data successfully decrypted for key: $key")
                decrypted
            } else {
                logger.w(TAG, "No data found for key: $key")
                null
            }
        }
    }
}
