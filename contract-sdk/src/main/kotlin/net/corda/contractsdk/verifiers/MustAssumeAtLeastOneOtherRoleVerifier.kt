package net.corda.contractsdk.verifiers

import net.corda.core.contracts.Contract
import net.corda.core.contracts.ContractState
import net.corda.core.contracts.Requirements.using
import net.corda.core.transactions.LedgerTransaction
import java.security.PublicKey
import kotlin.reflect.KClass

class MustAssumeAtLeastOneOtherRoleVerifier(val role : String, val otherRoles : Set<String>, val verificationScope : VerificationScope, targetStateTypes : Set<KClass<out ContractState>>?) : SupportingVerifier(targetStateTypes) {

    override fun verify(contract: KClass<out Contract>, tx: LedgerTransaction, signingKeys: List<PublicKey>) {
        when (verificationScope) {
            VerificationScope.INPUT_STATES -> verifyStates(getStatesInScopeOnInput(contract, tx), "input")
            VerificationScope.OUTPUT_STATES -> verifyStates(getStatesInScopeOnOutput(contract, tx), "output")
        }
    }

    fun verifyStates(states : List<ContractState>, statesType : String) {
        states.forEach { state ->
            val partyForRole = (state as StateWithRoles).getParty(role)
            val partiesForTheOtherRoles = otherRoles.map { (state as StateWithRoles).getParty(it) }

            "The $role must also be one of $otherRoles roles on ${statesType}${targetStateTypesQualifyingDescription()}." using (partiesForTheOtherRoles.contains(partyForRole))
        }
    }

    private fun targetStateTypesQualifyingDescription() : String {
        return if (targetStateTypes != null) {
            " for type(s) ${targetStateTypes.joinToString(separator = ",", prefix = "[", postfix = "]") { it.simpleName ?: "unknown" }}"
        } else {
            ""
        }
    }

}