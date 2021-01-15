package net.corda.contractsdk.verifiers

import net.corda.core.contracts.Contract
import net.corda.core.contracts.ContractState
import net.corda.core.contracts.LinearState
import net.corda.core.contracts.Requirements.using
import net.corda.core.transactions.LedgerTransaction
import java.security.PublicKey
import kotlin.reflect.KClass
import kotlin.reflect.full.memberProperties

class ForbidChangeInCoupledLinearStatesExceptVerifier(val changeAllowedInProperties : Set<String>, targetStateTypes : Set<KClass<out ContractState>>?) : SupportingVerifier(targetStateTypes) {
    override fun verify(contract: KClass<out Contract>, tx: LedgerTransaction, signingKeys: List<PublicKey>) {

        val inputStates = getStatesInScopeOnInput(contract, tx)
        val outputStates = getStatesInScopeOnOutput(contract, tx)

        "All input states must be linear" using (inputStates.filter { !(it is LinearState) }.isEmpty())
        "All output states must be linear" using (outputStates.filter { !(it is LinearState) }.isEmpty())
        "Number of input states and output states must be the same" using (inputStates.size == outputStates.size)

        val pairedUp = inputStates.map { inputState -> inputState to outputStates.find { outputState -> (inputState as LinearState).linearId == (outputState as LinearState).linearId && inputState::class == outputState::class } }.toMap()

        "Each input state must have a corresponding output state (with same linear id)" using (!pairedUp.values.contains(null))

        pairedUp.forEach {
            val inputState = it.key
            val outputState = it.value

            val propertiesThatMustMatch = inputState::class.memberProperties.filter { it.name !in changeAllowedInProperties }
            propertiesThatMustMatch.forEach { property ->
                val valueOnInput = property.getter.call(inputState)
                val valueOnOutput = property.getter.call(outputState)

                "Property '${property.name}' is not allowed to change between input and output${targetStateTypesQualifyingDescription()}" using (valueOnInput == valueOnOutput)
            }
        }
    }

    private fun targetStateTypesQualifyingDescription() : String {
        return if (targetStateTypes != null) {
            " on type(s) ${targetStateTypes.joinToString(separator = ",", prefix = "[", postfix = "]") { it.simpleName ?: "unknown" }}"
        } else {
            ""
        }
    }
}