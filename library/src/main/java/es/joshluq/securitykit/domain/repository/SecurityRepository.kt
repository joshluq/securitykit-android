package es.joshluq.securitykit.domain.repository

/**
 * Repository interface for secure data operations.
 */
internal interface SecurityRepository {
    /**
     * Saves data securely.
     * @param key The key to store the data.
     * @param value The value to be stored.
     * @param type The class type of the value.
     */
    suspend fun <T : Any> save(key: String, value: T, type: Class<T>)

    /**
     * Reads secure data.
     * @param key The key of the data to retrieve.
     * @param type The class type of the value.
     * @return The retrieved value or null if not found.
     */
    suspend fun <T : Any> read(key: String, type: Class<T>): T?
}
