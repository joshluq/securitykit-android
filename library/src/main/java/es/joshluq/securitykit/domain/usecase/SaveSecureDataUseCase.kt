package es.joshluq.securitykit.domain.usecase

import es.joshluq.foundationkit.log.Loggerkit
import es.joshluq.foundationkit.usecase.NoneOutput
import es.joshluq.foundationkit.usecase.UseCase
import es.joshluq.foundationkit.usecase.UseCaseInput
import es.joshluq.securitykit.domain.repository.SecurityRepository

/**
 * Use case to save data securely.
 */
internal class SaveSecureDataUseCase(
    private val repository: SecurityRepository,
    private val logger: Loggerkit
) : UseCase<SaveSecureDataUseCase.Input, NoneOutput> {

    companion object {
        private const val TAG = "SaveSecureDataUseCase"
    }

    @Suppress("UNCHECKED_CAST")
    override suspend fun invoke(input: Input): Result<NoneOutput> {
        logger.d(TAG, "Executing save use case for key: ${input.key}")
        repository.save(input.key, input.value, input.type as Class<Any>)
        return Result.success(NoneOutput)
    }

    data class Input(val key: String, val value: Any, val type: Class<out Any>) : UseCaseInput
}
