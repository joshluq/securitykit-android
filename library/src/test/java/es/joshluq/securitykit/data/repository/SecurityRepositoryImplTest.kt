package es.joshluq.securitykit.data.repository

import es.joshluq.foundationkit.log.Loggerkit
import es.joshluq.foundationkit.provider.StorageProvider
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class SecurityRepositoryImplTest {

    private lateinit var storageProvider: StorageProvider
    private lateinit var logger: Loggerkit
    private lateinit var repository: SecurityRepositoryImpl

    @Before
    fun setup() {
        storageProvider = mockk(relaxed = true)
        logger = mockk(relaxed = true)
        repository = SecurityRepositoryImpl(storageProvider, logger)
    }

    @Test
    fun `save should delegate to storageProvider`() = runBlocking {
        // Given
        val key = "key"
        val value = "value"
        
        // When
        repository.save(key, value, String::class.java)

        // Then
        coVerify { storageProvider.save(key, value, String::class.java) }
    }

    @Test
    fun `save with generic object should delegate to storageProvider`() = runBlocking {
        // Given
        val key = "key"
        val value = TestData("test")
        
        // When
        repository.save(key, value, TestData::class.java)

        // Then
        coVerify { storageProvider.save(key, value, TestData::class.java) }
    }

    @Test
    fun `read should delegate to storageProvider`() = runBlocking {
        // Given
        val key = "key"
        
        // When
        repository.read(key, String::class.java)

        // Then
        coVerify { storageProvider.read(key, String::class.java) }
    }

    data class TestData(val name: String)
}
