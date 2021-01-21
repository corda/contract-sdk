package com.r3.corda.lib.contracts.contractsdk.test.statesandcontracts

import com.r3.corda.lib.contracts.contractsdk.StandardContract
import com.r3.corda.lib.contracts.contractsdk.annotations.*
import com.r3.corda.lib.contracts.contractsdk.verifiers.StandardState
import net.corda.core.contracts.*
import net.corda.core.identity.AbstractParty
import net.corda.core.identity.Party

class TestContract2 : StandardContract(), Contract {

    interface Commands : CommandData {
        @RequireNumberOfStatesOnInput(0, targetClasses = [TestState_A::class])
        class CommandAllowingNoInputStatesForTestStateA : Commands

        @RequireNumberOfStatesOnInputAtLeast(1, targetClasses = [TestState_A::class])
        class CommandMandatingAtLeastOneInputStateForTestStateA : Commands

        @RequireNumberOfStatesOnInputAtMost(1, targetClasses = [TestState_A::class])
        class CommandAllowingAtMostOneInputStateForTestStateA : Commands

        @RequireNumberOfStatesOnInputBetween(1, 2, targetClasses = [TestState_A::class])
        class CommandAllowingBetweenOneAndTwoInputStatesForTestStateA : Commands

        @RequireNumberOfStatesOnOutput(0, targetClasses = [TestState_A::class])
        class CommandAllowingNoOutputStatesForTestStateA : Commands

        @RequireNumberOfStatesOnOutputAtLeast(1, targetClasses = [TestState_A::class])
        class CommandMandatingAtLeastOneOutputStateForTestStateA : Commands

        @RequireNumberOfStatesOnOutputAtMost(1, targetClasses = [TestState_A::class])
        class CommandAllowingAtMostOneOutputStateForTestStateA : Commands

        @RequireNumberOfStatesOnOutputBetween(1, 2, targetClasses = [TestState_A::class])
        class CommandAllowingBetweenOneAndTwoOutputStatesForTestStateA : Commands

        @PermitStatusOnInput("X", targetClasses = [TestState_A::class])
        class CommandAllowingOnlyStatusXOnInputForTestStateA : Commands

        @PermitStatusesOnInput("X", "Y", targetClasses = [TestState_A::class])
        class CommandAllowingOnlyStatusesXAndYOnInputForTestStateA : Commands

        @PermitStatusOnOutput("X", targetClasses = [TestState_A::class])
        class CommandAllowingOnlyStatusXOnOutputForTestStateA : Commands

        @PermitStatusesOnOutput("X", "Y", targetClasses = [TestState_A::class])
        class CommandAllowingOnlyStatusesXAndYOnOutputForTestStateA : Commands

        @RequireSignersFromEachInputState("X", targetClasses = [TestState_A::class])
        class CommandRequiringRoleXFromTestStateAOnInputToBeASigner : Commands

        @RequireSignersFromEachOutputState("X", targetClasses = [TestState_A::class])
        class CommandRequiringRoleXFromTestStateAOnOutputToBeASigner: Commands

        @RequireStatusChangeInCoupledLinearStates("X", "Y", targetClasses = [TestState_A::class])
        class CommandAllowingOnlyChangeFromStatusXtoYOnCoupledLinearStatesForTestStateA : Commands

        @ForbidChangeInCoupledLinearStatesExcept("x", targetClasses = [TestState_A::class])
        class CommandAllowingOnlyChangeInPropertyXOnCoupledLinearStatesForTestStateA : Commands

        @RequireDistinctPartiesWithinEachInputState("X", "Y", targetClasses = [TestState_A::class])
        class CommandMandatingDistinctPartiesForRolesXAndYOnEachInputStateForTestStateA : Commands

        @RequireDistinctPartiesWithinEachOutputState("X", "Y", targetClasses = [TestState_A::class])
        class CommandMandatingDistinctPartiesForRolesXAndYOnEachOutputStateForTestStateA : Commands

        @RequirePartyToAssumeAtLeastOneOtherRoleWithinEachInputState("X", "Y", "Z", targetClasses = [TestState_A::class])
        class CommandMandatingRoleXAssumingAtLeastAlsoRoleYorZOnEachInputStateForTestStateA : Commands

        @RequirePartyToAssumeAtLeastOneOtherRoleWithinEachOutputState("X", "Y", "Z", targetClasses = [TestState_A::class])
        class CommandMandatingRoleXAssumingAtLeastAlsoRoleYorZOnEachOutputStateForTestStateA : Commands

        @RequirePropertySetOnInput("x", targetClasses = [TestState_A::class])
        class CommandMandatingPropertyXToBeSetOnEachInputStateForTestStateA : Commands

        @RequirePropertySetOnOutput("x", targetClasses = [TestState_A::class])
        class CommandMandatingPropertyXToBeSetOnEachOutputStateForTestStateA : Commands

        @RequirePropertyNotSetOnInput("x", targetClasses = [TestState_A::class])
        class CommandMandatingPropertyXToNotBeSetOnEachInputStateForTestStateA : Commands

        @RequirePropertyNotSetOnOutput("x", targetClasses = [TestState_A::class])
        class CommandMandatingPropertyXToNotBeSetOnEachOutputStateForTestStateA : Commands
    }

    companion object {
        val ID = "com.r3.corda.lib.contracts.contractsdk.test.statesandcontracts.TestContract2"
        fun getTestStateA(x : Party?, y : Party?, z : Party?, linearId: UniqueIdentifier, status : String? = null, participants: List<AbstractParty> = listOf(x, y, z).filterNotNull()) = TestState_A(x, y, z, linearId, status, participants)
        fun getTestStateB(x : Party?, y : Party?, z : Party?, linearId: UniqueIdentifier, status : String? = null, participants: List<AbstractParty> = listOf(x, y, z).filterNotNull()) = TestState_B(x, y, z, linearId, status, participants)
    }

    @BelongsToContract(TestContract2::class)
    class TestState_A(val x : Party?, val y : Party?, val z : Party?, override val linearId: UniqueIdentifier, val status : String?, override val participants: List<AbstractParty>) : StandardState, LinearState {

        override fun getParty(role: String): Party {
            return when (role.toUpperCase()) {
                "X" -> x!!
                "Y" -> y!!
                "Z" -> z!!
                else -> throw IllegalArgumentException("Unrecognized role '$role'")
            }
        }

        override fun isInStatus(status: String) = status == this.status

    }

    @BelongsToContract(TestContract2::class)
    class TestState_B(val x : Party?, val y : Party?, val z : Party?, override val linearId: UniqueIdentifier, val status : String?, override val participants: List<AbstractParty>) : StandardState, LinearState {

        override fun getParty(role: String): Party {
            return when (role.toUpperCase()) {
                "X" -> x!!
                "Y" -> y!!
                "Z" -> z!!
                else -> throw IllegalArgumentException("Unrecognized role '$role'")
            }
        }

        override fun isInStatus(status: String) = status == this.status

    }

}


