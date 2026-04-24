package es.joshluq.securitykit.domain.usecase

import es.joshluq.securitykit.domain.repository.SecurityRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class SaveSecureDataUseCaseTest {

    private lateinit var repository: SecurityRepository
    private lateinit var useCase: SaveSecureDataUseCase

    @Before
    fun setup() {
        repository = mockk()
        useCase = SaveSecureDataUseCase(repository, logger)
    }

    @Test
    fun `invoke should call repository save and return success`() = runBlocking {
        // Given
        val key = "key"
        val value = "value"
        val input = SaveSecureDataUseCase.Input(key, value)
        coEvery { repository.save(key, value) } returns Unit

        // When
        val result = useCase(input)

        // Then
        coVerify { repository.save(key, value) }
        assertTrue(result.isSuccess)
    }
}
