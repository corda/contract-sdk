package net.corda.contractsdk.test.statesandcontracts

import net.corda.contractsdk.StandardContract
import net.corda.contractsdk.verifiers.StandardCommand
import net.corda.core.contracts.*
import net.corda.core.contracts.Requirements.using
import net.corda.core.identity.AbstractParty
import net.corda.core.identity.Party
import net.corda.core.transactions.LedgerTransaction

class TestContract5 : StandardContract(), Contract {

    interface Commands : CommandData {
        class CommandA : Commands, StandardCommand {
            override fun verifyFurther(tx: LedgerTransaction) {
                tx.outputsOfType(TestState::class.java).forEach {
                    "X cannot be located in London" using (it.x.name.locality != "London")
                }
            }
        }
    }

    companion object {
        val ID = "net.corda.contractsdk.test.statesandcontracts.TestContract5"
        fun getTestState(x : Party, participants: List<AbstractParty> = listOf(x)) = TestState(x, participants)
    }

    @BelongsToContract(TestContract5::class)
    class TestState(val x : Party, override val participants: List<AbstractParty>) : ContractState

}


