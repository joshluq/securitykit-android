package es.joshluq.securitykit.sdk

import es.joshluq.foundationkit.usecase.NoneOutput
import es.joshluq.securitykit.di.SecurityKitComponent
import es.joshluq.securitykit.domain.usecase.ReadSecureDataUseCase
import es.joshluq.securitykit.domain.usecase.SaveSecureDataUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class SecurityKitTest {

    private lateinit var manager: SecurityKit
    private lateinit var component: SecurityKitComponent
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
        val config = mockk<SecurityKitConfig>(relaxed = true)
        manager = SecurityKit(config) { component }
        manager.initialize()
    }

    @Test
    fun `save should execute SaveSecureDataUseCase`() = runBlocking {
        // Given
        val key = "test_key"
        val value = "test_value"
        val expectedInput = SaveSecureDataUseCase.Input(key, value, String::class.java)
        coEvery { saveUseCase(expectedInput) } returns Result.success(NoneOutput)

        // When
        manager.save(key, value)

        // Then
        coVerify { saveUseCase(expectedInput) }
    }

    @Test
    fun `save should execute SaveSecureDataUseCase with complex object`() = runBlocking {
        // Given
        val key = "test_key"
        val value = TestData("test")
        val expectedInput = SaveSecureDataUseCase.Input(key, value, TestData::class.java)
        coEvery { saveUseCase(expectedInput) } returns Result.success(NoneOutput)

        // When
        manager.save(key, value)

        // Then
        coVerify { saveUseCase(expectedInput) }
    }

    @Test
    fun `read should execute ReadSecureDataUseCase with complex object and return success result`() = runBlocking {
        // Given
        val key = "test_key"
        val expectedValue = TestData("decrypted")
        val expectedInput = ReadSecureDataUseCase.Input(key, TestData::class.java)
        val mockResult = Result.success(ReadSecureDataUseCase.Output(expectedValue))
        coEvery { readUseCase(expectedInput) } returns mockResult

        // When
        val result = manager.read<TestData>(key)

        // Then
        coVerify { readUseCase(expectedInput) }
        assertEquals(expectedValue, result.getOrNull())
    }

    data class TestData(val name: String)
}
