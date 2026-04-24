package es.joshluq.securitykit.manager

import es.joshluq.foundationkit.usecase.NoneOutput
import es.joshluq.securitykit.di.SecuritykitComponent
import es.joshluq.securitykit.domain.usecase.ReadSecureDataUseCase
import es.joshluq.securitykit.domain.usecase.SaveSecureDataUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class SecuritykitManagerTest {

    private lateinit var manager: SecuritykitManager
    private lateinit var component: SecuritykitComponent
    private lateinit var saveUseCase: SaveSecureDataUseCase
    private lateinit var readUseCase: ReadSecureDataUseCase

    @Before
    fun setup() {
        component = mockk(relaxed = true)
        saveUseCase = mockk()
        readUseCase = mockk()

        coEvery { component.saveSecureDataUseCase } returns saveUseCase
        coEvery { component.readSecureDataUseCase } returns readUseCase

        // Using the internal constructor for testing backdoor
        manager = SecuritykitManager { component }
        manager.initialize(mockk(relaxed = true))
    }

    @Test
    fun `save should execute SaveSecureDataUseCase`() = runBlocking {
        // Given
        val key = "test_key"
        val value = "test_value"
        coEvery { saveUseCase(SaveSecureDataUseCase.Input(key, value)) } returns Result.success(NoneOutput)

        // When
        manager.save(key, value)

        // Then
        coVerify { saveUseCase(SaveSecureDataUseCase.Input(key, value)) }
    }

    @Test
    fun `read should execute ReadSecureDataUseCase and return success result`() = runBlocking {
        // Given
        val key = "test_key"
        val expectedValue = "decrypted_value"
        val mockResult = Result.success(ReadSecureDataUseCase.Output(expectedValue))
        coEvery { readUseCase(ReadSecureDataUseCase.Input(key)) } returns mockResult

        // When
        val result = manager.read(key)

        // Then
        coVerify { readUseCase(ReadSecureDataUseCase.Input(key)) }
        assertEquals(expectedValue, result.getOrNull())
    }
}
