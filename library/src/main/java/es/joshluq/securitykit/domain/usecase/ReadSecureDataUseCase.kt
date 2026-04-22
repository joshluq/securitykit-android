package es.joshluq.securitykit.domain.usecase

import es.joshluq.foundationkit.log.Loggerkit
import es.joshluq.foundationkit.usecase.UseCase
import es.joshluq.foundationkit.usecase.UseCaseInput
import es.joshluq.foundationkit.usecase.UseCaseOutput
import es.joshluq.securitykit.domain.repository.SecurityRepository
import kotlinx.coroutines.flow.first

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

    override suspend fun invoke(input: Input): Result<Output> {
        logger.d(TAG, "Executing read use case for key: ${input.key}")
        val value = repository.read(input.key).first()
        
        return if (value != null) {
            Result.success(Output(value))
        } else {
            logger.w(TAG, "Value not found for key: ${input.key}")
            Result.failure(Exception("Value not found"))
        }
    }

    data class Input(val key: String) : UseCaseInput

    data class Output(val value: String) : UseCaseOutput
}
