package com.r3.corda.lib.contracts.contractsdk.test.statesandcontracts

import com.r3.corda.lib.contracts.contractsdk.StandardContract
import com.r3.corda.lib.contracts.contractsdk.annotations.RequirePropertySetOnInput
import com.r3.corda.lib.contracts.contractsdk.annotations.RequirePropertySetOnOutput
import net.corda.core.contracts.*
import net.corda.core.identity.AbstractParty
import net.corda.core.identity.Party

@RequirePropertySetOnInput("x")
@RequirePropertySetOnOutput("x")
class TestContract3 : StandardContract(), Contract {

    interface Commands : CommandData {
        class CommandA : Commands
    }

    companion object {
        val ID = "com.r3.corda.lib.contracts.contractsdk.test.statesandcontracts.TestContract3"
        fun getTestState(x : Party?, y : Party?, participants: List<AbstractParty> = listOfNotNull(x, y)) = TestState(x, y, participants)
    }

    @BelongsToContract(TestContract3::class)
    class TestState(val x : Party?, val y : Party?, override val participants: List<AbstractParty>) : ContractState

}


