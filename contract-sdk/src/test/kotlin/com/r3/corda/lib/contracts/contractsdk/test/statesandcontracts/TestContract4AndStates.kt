package com.r3.corda.lib.contracts.contractsdk.test.statesandcontracts

import com.r3.corda.lib.contracts.contractsdk.StandardContract
import net.corda.core.contracts.*
import net.corda.core.contracts.Requirements.using
import net.corda.core.identity.AbstractParty
import net.corda.core.identity.Party
import net.corda.core.transactions.LedgerTransaction

class TestContract4 : StandardContract(), Contract {

    interface Commands : CommandData {
        class CommandA : Commands
    }

    override fun verifyFurther(tx: LedgerTransaction) {
        tx.outputsOfType(TestState::class.java).forEach {
            "X cannot be located in London" using (it.x.name.locality != "London")
        }
    }

    companion object {
        val ID = "com.r3.corda.lib.contracts.contractsdk.test.statesandcontracts.TestContract4"
        fun getTestState(x : Party, participants: List<AbstractParty> = listOf(x)) = TestState(x, participants)
    }

    @BelongsToContract(TestContract4::class)
    class TestState(val x : Party, override val participants: List<AbstractParty>) : ContractState

}


