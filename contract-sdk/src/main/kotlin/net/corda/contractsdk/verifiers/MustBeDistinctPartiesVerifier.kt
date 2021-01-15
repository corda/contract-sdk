package net.corda.contractsdk.verifiers

import net.corda.core.contracts.Contract
import net.corda.core.contracts.ContractState
import net.corda.core.contracts.Requirements.using
import net.corda.core.transactions.LedgerTransaction
import java.security.PublicKey
import kotlin.reflect.KClass

class MustBeDistinctPartiesVerifier(val roles : Set<String>, val verificationScope : VerificationScope, targetStateTypes : Set<KClass<out ContractState>>?) : SupportingVerifier(targetStateTypes) {

    override fun verify(contract: KClass<out Contract>, tx: LedgerTransaction, signingKeys: List<PublicKey>) {
        when (verificationScope) {
            VerificationScope.INPUT_STATES -> verifyStates(getStatesInScopeOnInput(contract, tx), "input")
            VerificationScope.OUTPUT_STATES -> verifyStates(getStatesInScopeOnOutput(contract, tx), "output")
        }
    }

    fun verifyStates(states : List<ContractState>, stateType : String) {
        states.forEach { state ->
            val parties = roles.map { (state as StateWithRoles).getParty(it) }.toSet()
            "Each of these roles must be a different party on ${stateType}${targetStateTypesQualifyingDescription()}, ${roles}." using (roles.size == parties.size)
        }
    }

    private fun targetStateTypesQualifyingDescription() : String {
        return if (targetStateTypes != null) {
            " of type(s) ${targetStateTypes.joinToString(separator = ",", prefix = "[", postfix = "]") { it.simpleName ?: "unknown" }}"
        } else {
            ""
        }
    }

}