package com.r3.corda.lib.contracts.contractsdk.verifiers

import net.corda.core.contracts.Contract
import net.corda.core.contracts.ContractState
import net.corda.core.contracts.Requirements.using
import net.corda.core.transactions.LedgerTransaction
import java.security.PublicKey
import kotlin.reflect.KClass

class BoundedNumberOfStatesVerifier(val min : Int?, val max : Int?, val verificationScope : VerificationScope, targetStateTypes : Set<KClass<out ContractState>>?) : SupportingVerifier(targetStateTypes) {
    override fun verify(contract: KClass<out Contract>, tx: LedgerTransaction, signingKeys: List<PublicKey>) {
        when (verificationScope) {
            VerificationScope.INPUT_STATES -> verifyInputSide(contract, tx)
            VerificationScope.OUTPUT_STATES -> verifyOutputSide(contract, tx)
        }
    }

    fun verifyInputSide(contract: KClass<out Contract>, tx: LedgerTransaction) {
        val inputStates = getStatesInScopeOnInput(contract, tx)
        "There must be at least $min input state(s)${targetStateTypesQualifyingDescription()}" using (min == null || inputStates.size >= min)
        "There ${if (max == 0) {"cannot be any"} else {"must be at most $max"}} input state(s)${targetStateTypesQualifyingDescription()}" using (max == null || inputStates.size <= max)
    }

    fun verifyOutputSide(contract: KClass<out Contract>, tx: LedgerTransaction) {
        val outputStates = getStatesInScopeOnOutput(contract, tx)
        "There must be at least $min output state(s)${targetStateTypesQualifyingDescription()}" using (min == null || outputStates.size >= min)
        "There ${if (max == 0) {"cannot be any"} else {"must be at most $max"}} output state(s)${targetStateTypesQualifyingDescription()}" using (max == null || outputStates.size <= max)
    }

    private fun targetStateTypesQualifyingDescription() : String {
        return if (targetStateTypes != null) {
            " of type(s) ${targetStateTypes.joinToString(separator = ",", prefix = "[", postfix = "]") { it.simpleName ?: "unknown" }}"
        } else {
            ""
        }
    }
}