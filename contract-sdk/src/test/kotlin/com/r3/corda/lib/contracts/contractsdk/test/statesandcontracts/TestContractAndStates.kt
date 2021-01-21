package com.r3.corda.lib.contracts.contractsdk.test.statesandcontracts

import com.r3.corda.lib.contracts.contractsdk.StandardContract
import com.r3.corda.lib.contracts.contractsdk.annotations.*
import com.r3.corda.lib.contracts.contractsdk.verifiers.StateWithRoles
import com.r3.corda.lib.contracts.contractsdk.verifiers.StateWithStatus
import net.corda.core.contracts.*
import net.corda.core.identity.AbstractParty
import net.corda.core.identity.Party

class TestContract : StandardContract(), Contract {

    interface Commands : CommandData {
        @RequireNumberOfStatesOnInput(0)
        class CommandAllowingNoInputStates : Commands

        @RequireNumberOfStatesOnInputAtLeast(1)
        class CommandMandatingAtLeastOneInputState : Commands

        @RequireNumberOfStatesOnInputAtMost(1)
        class CommandAllowingAtMostOneInputState : Commands

        @RequireNumberOfStatesOnInputBetween(1, 2)
        class CommandAllowingBetweenOneAndTwoInputStates : Commands

        @RequireNumberOfStatesOnOutput(0)
        class CommandAllowingNoOutputStates : Commands

        @RequireNumberOfStatesOnOutputAtLeast(1)
        class CommandMandatingAtLeastOneOutputState : Commands

        @RequireNumberOfStatesOnOutputAtMost(1)
        class CommandAllowingAtMostOneOutputState : Commands

        @RequireNumberOfStatesOnOutputBetween(1, 2)
        class CommandAllowingBetweenOneAndTwoOutputStates : Commands

        @PermitStatusOnInput("X")
        class CommandAllowingOnlyStatusXOnInput : Commands

        @PermitStatusesOnInput("X", "Y")
        class CommandAllowingOnlyStatusesXAndYOnInput : Commands

        @PermitStatusOnOutput("X")
        class CommandAllowingOnlyStatusXOnOutput : Commands

        @PermitStatusesOnOutput("X", "Y")
        class CommandAllowingOnlyStatusesXAndYOnOutput : Commands

        @RequireSignersFromEachInputState("X")
        class CommandRequiringRoleXFromInputToBeASigner : Commands

        @RequireSignersFromEachOutputState("X")
        class CommandRequiringRoleXFromOutputToBeASigner : Commands

        @RequireStatusChangeInCoupledLinearStates("X", "Y")
        class CommandAllowingOnlyChangeFromStatusXtoYOnCoupledLinearStates : Commands

        @ForbidChangeInCoupledLinearStatesExcept("x")
        class CommandAllowingOnlyChangeInPropertyXOnCoupledLinearStates : Commands

        @ForbidChangeInCoupledLinearStatesExcept("x", "y")
        class CommandAllowingOnlyChangeInPropertyXAndYOnCoupledLinearStates : Commands

        @RequireDistinctPartiesWithinEachInputState("X", "Y")
        class CommandMandatingDistinctPartiesForRolesXAndYOnEachInputState : Commands

        @RequireDistinctPartiesWithinEachOutputState("X", "Y")
        class CommandMandatingDistinctPartiesForRolesXAndYOnEachOutputState : Commands

        @RequirePartyToAssumeAtLeastOneOtherRoleWithinEachInputState("X", "Y", "Z")
        class CommandMandatingRoleXAssumingAtLeastAlsoRoleYorZOnEachInputState : Commands

        @RequirePartyToAssumeAtLeastOneOtherRoleWithinEachOutputState("X", "Y", "Z")
        class CommandMandatingRoleXAssumingAtLeastAlsoRoleYorZOnEachOutputState : Commands

        @RequirePropertySetOnInput("x")
        class CommandMandatingPropertyXToBeSetOnEachInputState : Commands

        @RequirePropertiesSetOnInput("x", "y")
        class CommandMandatingPropertiesXAndYToBeSetOnEachInputState : Commands

        @RequirePropertySetOnOutput("x")
        class CommandMandatingPropertyXToBeSetOnEachOutputState : Commands

        @RequirePropertiesSetOnOutput("x", "y")
        class CommandMandatingPropertiesXAndYToBeSetOnEachOutputState : Commands

        @RequirePropertyNotSetOnInput("x")
        class CommandMandatingPropertyXToNotBeSetOnEachInputState : Commands

        @RequirePropertiesNotSetOnInput("x", "y")
        class CommandMandatingPropertiesXAndYToNotBeSetOnEachInputState : Commands

        @RequirePropertyNotSetOnOutput("x")
        class CommandMandatingPropertyXToNotBeSetOnEachOutputState : Commands

        @RequirePropertiesNotSetOnOutput("x", "y")
        class CommandMandatingPropertiesXAndYToNotBeSetOnEachOutputState : Commands

        @RequirePartyToAssumeAtLeastOneOtherRoleWithinEachInputStateList(
            RequirePartyToAssumeAtLeastOneOtherRoleWithinEachInputState("X", "Y"),
            RequirePartyToAssumeAtLeastOneOtherRoleWithinEachInputState("Y", "Z")
        )
        class CommandMandatingRoleXToBeAlsoRoleYAndRoleYToBeAlsoRoleZOnEachInputState : Commands

        @RequirePartyToAssumeAtLeastOneOtherRoleWithinEachOutputStateList(
            RequirePartyToAssumeAtLeastOneOtherRoleWithinEachOutputState("X", "Y"),
            RequirePartyToAssumeAtLeastOneOtherRoleWithinEachOutputState("Y", "Z")
        )
        class CommandMandatingRoleXToBeAlsoRoleYAndRoleYToBeAlsoRoleZOnEachOutputState : Commands

        @RequireDistinctPartiesWithinEachInputStateList(
            RequireDistinctPartiesWithinEachInputState("X", "Y"),
            RequireDistinctPartiesWithinEachInputState("Y", "Z")
        )
        class CommandMandatingRolesXAndYAndZToBeDistinctOnEachInputState : Commands

        @RequireDistinctPartiesWithinEachOutputStateList(
                RequireDistinctPartiesWithinEachOutputState("X", "Y"),
                RequireDistinctPartiesWithinEachOutputState("Y", "Z")
        )
        class CommandMandatingRolesXAndYAndZToBeDistinctOnEachOutputState : Commands
    }

    companion object {
        val ID = "com.r3.corda.lib.contracts.contractsdk.test.statesandcontracts.TestContract"
    }
}

@BelongsToContract(TestContract::class)
class TestState(override val participants: List<AbstractParty>) : ContractState

@BelongsToContract(TestContract::class)
class TestStateWithStatus(val status : String, override val participants: List<AbstractParty>) : StateWithStatus, ContractState {
    override fun isInStatus(status: String) = this.status == status
}

@BelongsToContract(TestContract::class)
class TestLinearStateWithStatus(val status : String, override val linearId: UniqueIdentifier, override val participants: List<AbstractParty>) : StateWithStatus, LinearState {
    override fun isInStatus(status: String) = this.status == status
}

@BelongsToContract(TestContract::class)
class TestStateWithRoles(val x : Party, val y : Party, val z : Party, val u : Party? = null, override val participants: List<AbstractParty> = listOf(x, y, z)) : StateWithRoles, ContractState {

    override fun getParty(role: String): Party {
        return when (role.toUpperCase()) {
            "X" -> x
            "Y" -> y
            "Z" -> z
            "U" -> u!!
            else -> throw IllegalArgumentException("Unrecognized role '$role'")
        }
    }

}

@BelongsToContract(TestContract::class)
class TestLinearStateWithRoles(val x : Party, val y : Party, val z : Party, val u : Party? = null, override val linearId: UniqueIdentifier, override val participants: List<AbstractParty> = listOf(x, y, z)) : StateWithRoles, LinearState {

    override fun getParty(role: String): Party {
        return when (role.toUpperCase()) {
            "X" -> x
            "Y" -> y
            "Z" -> z
            "U" -> u!!
            else -> throw IllegalArgumentException("Unrecognized role '$role'")
        }
    }

}

@BelongsToContract(TestContract::class)
class TestStateWithProperties(val x : String?, val y : String?, val z : String?, override val participants: List<AbstractParty>) : ContractState

@BelongsToContract(TestContract::class)
class TestLinearStateWithProperties(val x : String?, val y : String?, val z : String?, override val linearId: UniqueIdentifier, override val participants: List<AbstractParty>) : LinearState