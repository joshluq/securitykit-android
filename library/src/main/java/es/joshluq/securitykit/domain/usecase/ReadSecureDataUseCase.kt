package es.joshluq.securitykit.domain.usecase

import es.joshluq.foundationkit.log.Loggerkit
import es.joshluq.foundationkit.usecase.UseCase
import es.joshluq.foundationkit.usecase.UseCaseInput
import es.joshluq.foundationkit.usecase.UseCaseOutput
import es.joshluq.securitykit.domain.repository.SecurityRepository

/**
 * Use case to read secure data.
 */
internal class ReadSecureDataUseCase(
    private val repository: SecurityRepository,
    private val logger: Loggerkit
) : UseCase<ReadSecureDataUseCase.Input, ReadSecureDataUseCase.Output> {

    companion object {
        private const val TAG = "ReadSecureDataUseCase"
    }

    @Suppress("UNCHECKED_CAST")
    override suspend fun invoke(input: Input): Result<Output> {
        logger.d(TAG, "Executing read use case for key: ${input.key}")
        val value = repository.read(input.key, input.type as Class<Any>)

        return if (value != null) {
            Result.success(Output(value))
        } else {
            logger.w(TAG, "Value not found for key: ${input.key}")
            Result.failure(Exception("Value not found"))
        }
    }

    data class Input(val key: String, val type: Class<out Any>) : UseCaseInput

    data class Output(val value: Any) : UseCaseOutput
}
