package es.joshluq.securitykit.domain.usecase

import es.joshluq.securitykit.domain.repository.SecurityRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ReadSecureDataUseCaseTest {

    private lateinit var repository: SecurityRepository
    private lateinit var useCase: ReadSecureDataUseCase

    @Before
    fun setup() {
        repository = mockk()
        useCase = ReadSecureDataUseCase(repository, logger)
    }

    @Test
    fun `invoke should return success when repository has data`() = runBlocking {
        // Given
        val key = "key"
        val value = "decrypted_value"
        val input = ReadSecureDataUseCase.Input(key)
        coEvery { repository.read(key) } returns flowOf(value)

        // When
        val result = useCase(input)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(value, result.getOrNull()?.value)
    }

    @Test
    fun `invoke should return failure when repository returns null`() = runBlocking {
        // Given
        val key = "key"
        val input = ReadSecureDataUseCase.Input(key)
        coEvery { repository.read(key) } returns flowOf(null)

        // When
        val result = useCase(input)

        // Then
        assertTrue(result.isFailure)
        assertEquals("Value not found", result.exceptionOrNull()?.message)
    }
}
