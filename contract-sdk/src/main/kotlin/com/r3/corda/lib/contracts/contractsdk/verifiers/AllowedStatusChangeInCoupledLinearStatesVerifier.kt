package com.r3.corda.lib.contracts.contractsdk.verifiers

import net.corda.core.contracts.Contract
import net.corda.core.contracts.ContractState
import net.corda.core.contracts.LinearState
import net.corda.core.contracts.Requirements.using
import net.corda.core.transactions.LedgerTransaction
import java.security.PublicKey
import kotlin.reflect.KClass

//@todo this should probably also make sure each linear id is only once on each side
class RequireStatusChangeInCoupledLinearStatesVerifier(val statusOnInput : String, val statusOnOutput : String, targetStateTypes : Set<KClass<out ContractState>>?) : SupportingVerifier(targetStateTypes) {
    override fun verify(contract: KClass<out Contract>, tx: LedgerTransaction, signingKeys: List<PublicKey>) {

        val inputStates = getStatesInScopeOnInput(contract, tx)
        val outputStates = getStatesInScopeOnOutput(contract, tx)

        "All input states must be linear and with a status" using (inputStates.filter { !((it is LinearState) && (it is StateWithStatus)) }.isEmpty())
        "All output states must be linear and with a status" using (outputStates.filter { !((it is LinearState) && (it is StateWithStatus)) }.isEmpty())
        "Number of input states and output states must be the same" using (inputStates.size == outputStates.size)

        val pairedUp = inputStates.map { inputState -> inputState to outputStates.find { (inputState as LinearState).linearId == (it as LinearState).linearId } }.toMap()

        "Each input state must have a corresponding output state (with same linear id)" using (!pairedUp.values.contains(null))
        "There must be at least one transition" using (pairedUp.isNotEmpty())

        pairedUp.forEach {
            "The status must be transitioning from $statusOnInput to $statusOnOutput${targetStateTypesQualifyingDescription()}" using ((it.key as StateWithStatus).isInStatus(statusOnInput) && (it.value != null) && (it.value as StateWithStatus).isInStatus(statusOnOutput))
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