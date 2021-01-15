package net.corda.contractsdk.verifiers

import net.corda.core.contracts.Contract
import net.corda.core.contracts.ContractState
import net.corda.core.contracts.Requirements.using
import net.corda.core.transactions.LedgerTransaction
import java.security.PublicKey
import kotlin.reflect.KClass

class RequiredSignersVerifier(val requiredSigners : Set<String>, val verificationScope : VerificationScope, targetStateTypes : Set<KClass<out ContractState>>?) : SupportingVerifier(targetStateTypes) {
    override fun verify(contract: KClass<out Contract>, tx: LedgerTransaction, signingKeys: List<PublicKey>) {
        when (verificationScope) {
            VerificationScope.INPUT_STATES -> verifyStates(getStatesInScopeOnInput(contract, tx), signingKeys, "input")
            VerificationScope.OUTPUT_STATES -> verifyStates(getStatesInScopeOnOutput(contract, tx), signingKeys, "output")
        }
    }

    fun verifyStates(states : List<ContractState>, signingKeys: List<PublicKey>, stateType : String) {
        states.forEach { state ->
            val requiredSignerToParty = requiredSigners.map { it to (state as StateWithRoles).getParty(it) }.toMap()
            val missingRequiredSigners = requiredSignerToParty.filter { !(signingKeys.contains(it.value.owningKey)) }.keys
            if (requiredSigners.size > 1) {
                "Each of these roles on${targetStateTypesQualifyingDescription()} the $stateType must sign the transaction: $requiredSigners. These are missing: ${missingRequiredSigners}." using (missingRequiredSigners.isEmpty())
            } else {
                "The ${requiredSigners.single()} from${targetStateTypesQualifyingDescription()} the $stateType must sign the transaction." using (missingRequiredSigners.isEmpty())
            }
        }
    }

    private fun targetStateTypesQualifyingDescription() : String {
        return if (targetStateTypes != null) {
            " ${targetStateTypes.joinToString(separator = ",", prefix = "[", postfix = "]") { it.simpleName ?: "unknown" }} on"
        } else {
            ""
        }
    }

}