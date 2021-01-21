package com.r3.corda.lib.contracts.contractsdk.verifiers

import net.corda.core.contracts.BelongsToContract
import net.corda.core.contracts.Contract
import net.corda.core.contracts.ContractState
import net.corda.core.transactions.LedgerTransaction
import kotlin.reflect.KClass

abstract class SupportingVerifier(val targetStateTypes : Set<KClass<out ContractState>>?) : StandardVerifier {

    fun getStatesInScopeOnInput(contract: KClass<out Contract>, tx: LedgerTransaction) =
        targetStateTypes?.let {
            targetStateTypes.flatMap { targetState -> tx.inputsOfType(targetState.java) }
        } ?: tx.inputsOfContract(contract)

    fun getStatesInScopeOnOutput(contract: KClass<out Contract>, tx: LedgerTransaction) =
            targetStateTypes?.let {
                targetStateTypes.flatMap { targetState -> tx.outputsOfType(targetState.java) }
            } ?: tx.outputsOfContract(contract)

    private fun LedgerTransaction.inputsOfContract(contract: KClass<out Contract>) : List<ContractState> {
        return this.filterInputs { inputState ->
            val belongsToContractAnnotation = inputState.javaClass.getDeclaredAnnotation(BelongsToContract::class.java)
            belongsToContractAnnotation?.value?.equals(contract) ?: false
        }
    }

    private fun LedgerTransaction.outputsOfContract(contract: KClass<out Contract>) : List<ContractState> {
        return this.filterOutputs { outputState ->
            val belongsToContractAnnotation = outputState.javaClass.getDeclaredAnnotation(BelongsToContract::class.java)
            belongsToContractAnnotation?.value?.equals(contract) ?: false
        }
    }
}