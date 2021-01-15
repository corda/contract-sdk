package net.corda.contractsdk.verifiers

import net.corda.core.contracts.Contract
import net.corda.core.contracts.ContractState
import net.corda.core.contracts.Requirements.using
import net.corda.core.transactions.LedgerTransaction
import java.security.PublicKey
import kotlin.reflect.KClass

class AllowedStatusesVerifier(val statuses : Set<String>, val verificationScope : VerificationScope, targetStateTypes : Set<KClass<out ContractState>>?) : SupportingVerifier(targetStateTypes) {
    override fun verify(contract: KClass<out Contract>, tx: LedgerTransaction, signingKeys: List<PublicKey>) {
        when (verificationScope) {
            VerificationScope.INPUT_STATES -> verifyInputSide(contract, tx)
            VerificationScope.OUTPUT_STATES -> verifyOutputSide(contract, tx)
        }
    }

    fun verifyInputSide(contract: KClass<out Contract>, tx: LedgerTransaction) {
        val inputStates = getStatesInScopeOnInput(contract, tx)

        inputStates.forEach { inputState ->
            "The input state must be in ${(if (statuses.size > 1) { "one of these statuses: $statuses" } else { "status ${statuses.first()}" })}${targetStateTypesQualifyingDescription()}" using (statuses.any { (inputState as StateWithStatus).isInStatus(it) })
        }
    }

    fun verifyOutputSide(contract: KClass<out Contract>, tx: LedgerTransaction) {
        val outputStates = getStatesInScopeOnOutput(contract, tx)

        outputStates.forEach { outputState ->
            "The output state must be in ${(if (statuses.size > 1) { "one of these statuses: $statuses" } else { "status ${statuses.first()}" })}${targetStateTypesQualifyingDescription()}" using (statuses.any {(outputState as StateWithStatus).isInStatus(it)})
        }
    }

    private fun targetStateTypesQualifyingDescription() : String {
        return if (targetStateTypes != null) {
            " if the state is of type(s) ${targetStateTypes.joinToString(separator = ",", prefix = "[", postfix = "]") { it.simpleName ?: "unknown" }}"
        } else {
            ""
        }
    }
}