package es.joshluq.securitykit.domain.repository

/**
 * Repository interface for secure data operations.
 */
internal interface SecurityRepository {
    /**
     * Saves data securely.
     * @param key The key to store the data.
     * @param value The value to be stored.
     */
    suspend fun save(key: String, value: String)

    /**
     * Reads secure data.
     * @param key The key of the data to retrieve.
     * @return A Flow emitting the retrieved value or null if not found.
     */
    fun read(key: String): String?
}
