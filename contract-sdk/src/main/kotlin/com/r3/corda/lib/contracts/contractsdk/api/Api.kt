package com.r3.corda.lib.contracts.contractsdk.verifiers

import net.corda.core.contracts.Contract
import net.corda.core.identity.Party
import net.corda.core.transactions.LedgerTransaction
import java.security.PublicKey
import kotlin.reflect.KClass

enum class VerificationScope {
    INPUT_STATES,
    OUTPUT_STATES
}

interface StandardVerifier {
    fun verify(contract: KClass<out Contract>, tx : LedgerTransaction, signingKeys: List<PublicKey>)
}

interface StandardCommand {
    fun verifyFurther(tx : LedgerTransaction) {}
}

interface StandardState : StateWithRoles, StateWithStatus

interface StateWithRoles {
    fun getParty(role : String) : Party
}

interface StateWithStatus {
    fun isInStatus(status : String) : Boolean
}