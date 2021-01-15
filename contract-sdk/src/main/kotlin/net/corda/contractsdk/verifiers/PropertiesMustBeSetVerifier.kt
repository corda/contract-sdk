package net.corda.contractsdk.verifiers

import net.corda.core.contracts.Contract
import net.corda.core.contracts.ContractState
import net.corda.core.contracts.Requirements.using
import net.corda.core.transactions.LedgerTransaction
import java.security.PublicKey
import kotlin.reflect.KClass
import kotlin.reflect.full.memberProperties

class PropertiesMustBeSetVerifier(val names : Set<String>, val verificationScope : VerificationScope, targetStateTypes : Set<KClass<out ContractState>>?) : SupportingVerifier(targetStateTypes) {
    override fun verify(contract: KClass<out Contract>, tx: LedgerTransaction, signingKeys: List<PublicKey>) {
        when (verificationScope) {
            VerificationScope.INPUT_STATES -> verifyStates(getStatesInScopeOnInput(contract, tx), "input")
            VerificationScope.OUTPUT_STATES -> verifyStates(getStatesInScopeOnOutput(contract, tx), "output")
        }
    }

    fun verifyStates(states : List<ContractState>, stateType : String) {
        states.forEach { state ->
            val statePropertiesThatMustBeSet = state::class.memberProperties.filter { it.name in names }
            val offendingProperties = statePropertiesThatMustBeSet.filter { property ->
                val valueOfProperty = property.getter.call(state)
                valueOfProperty == null
            }

            if (statePropertiesThatMustBeSet.size > 1) {
                "Properties ${statePropertiesThatMustBeSet.map { it.name }} cannot be null on the ${stateType}${targetStateTypesQualifyingDescription()} but these are null: ${offendingProperties.map { it.name }}." using (offendingProperties.isEmpty())
            } else if (statePropertiesThatMustBeSet.size == 1) {
                "Property '${statePropertiesThatMustBeSet.single().name}' cannot be null on the ${stateType}${targetStateTypesQualifyingDescription()}." using (offendingProperties.isEmpty())
            }
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