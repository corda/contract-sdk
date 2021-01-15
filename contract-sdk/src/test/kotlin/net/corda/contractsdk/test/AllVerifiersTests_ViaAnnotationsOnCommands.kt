package net.corda.contractsdk.test

import net.corda.contractsdk.test.statesandcontracts.*
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.identity.CordaX500Name
import net.corda.core.identity.Party
import net.corda.testing.core.TestIdentity
import net.corda.testing.node.MockServices
import net.corda.testing.node.ledger
import org.junit.Test

class AllVerifiersTests_ViaAnnotationsOnCommands {
    private val companyA = TestIdentity(CordaX500Name("Company A", "London", "GB"))
    private val companyB = TestIdentity(CordaX500Name("Company B", "London", "GB"))
    private val companyC = TestIdentity(CordaX500Name("Company C", "London", "GB"))
    private val companyD = TestIdentity(CordaX500Name("Company D", "London", "GB"))
    private val ledgerServices = MockServices(listOf("net.corda.contractsdk.test.statesandcontracts"), companyA, companyB, companyC, companyD)

    @Test
    fun `Number of input states`() {
        ledgerServices.ledger {
            transaction {
                output(TestContract.ID, getTestState())
                command(companyA.publicKey, TestContract.Commands.CommandAllowingNoInputStates())
                verifies()
            }
            transaction {
                input(TestContract.ID, getTestState())
                command(companyA.publicKey, TestContract.Commands.CommandAllowingNoInputStates())
                failsWith("There cannot be any input state(s)")
            }
            transaction {
                input(TestContract.ID, getTestState())
                command(companyA.publicKey, TestContract.Commands.CommandMandatingAtLeastOneInputState())
                verifies()
            }
            transaction {
                output(TestContract.ID, getTestState())
                command(companyA.publicKey, TestContract.Commands.CommandMandatingAtLeastOneInputState())
                failsWith("There must be at least 1 input state(s)")
            }
            transaction {
                input(TestContract.ID, getTestState())
                command(companyA.publicKey, TestContract.Commands.CommandAllowingAtMostOneInputState())
                verifies()
            }
            transaction {
                input(TestContract.ID, getTestState())
                input(TestContract.ID, getTestState())
                command(companyA.publicKey, TestContract.Commands.CommandAllowingAtMostOneInputState())
                failsWith("There must be at most 1 input state(s)")
            }
            transaction {
                input(TestContract.ID, getTestState())
                command(companyA.publicKey, TestContract.Commands.CommandAllowingBetweenOneAndTwoInputStates())
                verifies()
            }
            transaction {
                input(TestContract.ID, getTestState())
                input(TestContract.ID, getTestState())
                command(companyA.publicKey, TestContract.Commands.CommandAllowingBetweenOneAndTwoInputStates())
                verifies()
            }
            transaction {
                output(TestContract.ID, getTestState())
                command(companyA.publicKey, TestContract.Commands.CommandAllowingBetweenOneAndTwoInputStates())
                failsWith("There must be at least 1 input state(s)")
            }
            transaction {
                input(TestContract.ID, getTestState())
                input(TestContract.ID, getTestState())
                input(TestContract.ID, getTestState())
                command(companyA.publicKey, TestContract.Commands.CommandAllowingBetweenOneAndTwoInputStates())
                failsWith("There must be at most 2 input state(s)")
            }
            transaction {
                input(TestContract2.ID, TestContract2.getTestStateB(companyA.party, companyB.party, companyC.party, UniqueIdentifier()))
                command(companyA.publicKey, TestContract2.Commands.CommandAllowingNoInputStatesForTestStateA())
                verifies()
            }
            transaction {
                input(TestContract2.ID, TestContract2.getTestStateA(companyA.party, companyB.party, companyC.party, UniqueIdentifier()))
                command(companyA.publicKey, TestContract2.Commands.CommandAllowingNoInputStatesForTestStateA())
                failsWith("There cannot be any input state(s) of type(s) [TestState_A]")
            }
            transaction {
                input(TestContract2.ID, TestContract2.getTestStateA(companyA.party, companyB.party, companyC.party, UniqueIdentifier()))
                command(companyA.publicKey, TestContract2.Commands.CommandMandatingAtLeastOneInputStateForTestStateA())
                verifies()
            }
            transaction {
                input(TestContract2.ID, TestContract2.getTestStateB(companyA.party, companyB.party, companyC.party, UniqueIdentifier()))
                command(companyA.publicKey, TestContract2.Commands.CommandMandatingAtLeastOneInputStateForTestStateA())
                failsWith("There must be at least 1 input state(s) of type(s) [TestState_A]")
            }
            transaction {
                input(TestContract2.ID, TestContract2.getTestStateB(companyA.party, companyB.party, companyC.party, UniqueIdentifier()))
                input(TestContract2.ID, TestContract2.getTestStateB(companyA.party, companyB.party, companyC.party, UniqueIdentifier()))
                command(companyA.publicKey, TestContract2.Commands.CommandAllowingAtMostOneInputStateForTestStateA())
                verifies()
            }
            transaction {
                input(TestContract2.ID, TestContract2.getTestStateA(companyA.party, companyB.party, companyC.party, UniqueIdentifier()))
                input(TestContract2.ID, TestContract2.getTestStateA(companyA.party, companyB.party, companyC.party, UniqueIdentifier()))
                command(companyA.publicKey, TestContract2.Commands.CommandAllowingAtMostOneInputStateForTestStateA())
                failsWith("There must be at most 1 input state(s) of type(s) [TestState_A]")
            }
            transaction {
                input(TestContract2.ID, TestContract2.getTestStateA(companyA.party, companyB.party, companyC.party, UniqueIdentifier()))
                input(TestContract2.ID, TestContract2.getTestStateB(companyA.party, companyB.party, companyC.party, UniqueIdentifier()))
                input(TestContract2.ID, TestContract2.getTestStateB(companyA.party, companyB.party, companyC.party, UniqueIdentifier()))
                input(TestContract2.ID, TestContract2.getTestStateB(companyA.party, companyB.party, companyC.party, UniqueIdentifier()))
                command(companyA.publicKey, TestContract2.Commands.CommandAllowingBetweenOneAndTwoInputStatesForTestStateA())
                verifies()
            }
            transaction {
                input(TestContract2.ID, TestContract2.getTestStateA(companyA.party, companyB.party, companyC.party, UniqueIdentifier()))
                input(TestContract2.ID, TestContract2.getTestStateA(companyA.party, companyB.party, companyC.party, UniqueIdentifier()))
                input(TestContract2.ID, TestContract2.getTestStateA(companyA.party, companyB.party, companyC.party, UniqueIdentifier()))
                input(TestContract2.ID, TestContract2.getTestStateB(companyA.party, companyB.party, companyC.party, UniqueIdentifier()))
                command(companyA.publicKey, TestContract2.Commands.CommandAllowingBetweenOneAndTwoInputStatesForTestStateA())
                failsWith("There must be at most 2 input state(s) of type(s) [TestState_A]")
            }
        }
    }

    @Test
    fun `Number of output states`() {
        ledgerServices.ledger {
            transaction {
                input(TestContract.ID, getTestState())
                command(companyA.publicKey, TestContract.Commands.CommandAllowingNoOutputStates())
                verifies()
            }
            transaction {
                output(TestContract.ID, getTestState())
                command(companyA.publicKey, TestContract.Commands.CommandAllowingNoOutputStates())
                failsWith("There cannot be any output state(s)")
            }
            transaction {
                output(TestContract.ID, getTestState())
                command(companyA.publicKey, TestContract.Commands.CommandMandatingAtLeastOneOutputState())
                verifies()
            }
            transaction {
                input(TestContract.ID, getTestState())
                command(companyA.publicKey, TestContract.Commands.CommandMandatingAtLeastOneOutputState())
                failsWith("There must be at least 1 output state(s)")
            }
            transaction {
                output(TestContract.ID, getTestState())
                command(companyA.publicKey, TestContract.Commands.CommandAllowingAtMostOneOutputState())
                verifies()
            }
            transaction {
                output(TestContract.ID, getTestState())
                output(TestContract.ID, getTestState())
                command(companyA.publicKey, TestContract.Commands.CommandAllowingAtMostOneOutputState())
                failsWith("There must be at most 1 output state(s)")
            }
            transaction {
                output(TestContract.ID, getTestState())
                command(companyA.publicKey, TestContract.Commands.CommandAllowingBetweenOneAndTwoOutputStates())
                verifies()
            }
            transaction {
                output(TestContract.ID, getTestState())
                output(TestContract.ID, getTestState())
                command(companyA.publicKey, TestContract.Commands.CommandAllowingBetweenOneAndTwoOutputStates())
                verifies()
            }
            transaction {
                input(TestContract.ID, getTestState())
                command(companyA.publicKey, TestContract.Commands.CommandAllowingBetweenOneAndTwoOutputStates())
                failsWith("There must be at least 1 output state(s)")
            }
            transaction {
                output(TestContract.ID, getTestState())
                output(TestContract.ID, getTestState())
                output(TestContract.ID, getTestState())
                command(companyA.publicKey, TestContract.Commands.CommandAllowingBetweenOneAndTwoOutputStates())
                failsWith("There must be at most 2 output state(s)")
            }
            transaction {
                output(TestContract2.ID, TestContract2.getTestStateB(companyA.party, companyB.party, companyC.party, UniqueIdentifier()))
                command(companyA.publicKey, TestContract2.Commands.CommandAllowingNoOutputStatesForTestStateA())
                verifies()
            }
            transaction {
                output(TestContract2.ID, TestContract2.getTestStateA(companyA.party, companyB.party, companyC.party, UniqueIdentifier()))
                command(companyA.publicKey, TestContract2.Commands.CommandAllowingNoOutputStatesForTestStateA())
                failsWith("There cannot be any output state(s) of type(s) [TestState_A]")
            }
            transaction {
                output(TestContract2.ID, TestContract2.getTestStateA(companyA.party, companyB.party, companyC.party, UniqueIdentifier()))
                command(companyA.publicKey, TestContract2.Commands.CommandMandatingAtLeastOneOutputStateForTestStateA())
                verifies()
            }
            transaction {
                output(TestContract2.ID, TestContract2.getTestStateB(companyA.party, companyB.party, companyC.party, UniqueIdentifier()))
                command(companyA.publicKey, TestContract2.Commands.CommandMandatingAtLeastOneOutputStateForTestStateA())
                failsWith("There must be at least 1 output state(s) of type(s) [TestState_A]")
            }
            transaction {
                output(TestContract2.ID, TestContract2.getTestStateB(companyA.party, companyB.party, companyC.party, UniqueIdentifier()))
                output(TestContract2.ID, TestContract2.getTestStateB(companyA.party, companyB.party, companyC.party, UniqueIdentifier()))
                command(companyA.publicKey, TestContract2.Commands.CommandAllowingAtMostOneOutputStateForTestStateA())
                verifies()
            }
            transaction {
                output(TestContract2.ID, TestContract2.getTestStateA(companyA.party, companyB.party, companyC.party, UniqueIdentifier()))
                output(TestContract2.ID, TestContract2.getTestStateA(companyA.party, companyB.party, companyC.party, UniqueIdentifier()))
                command(companyA.publicKey, TestContract2.Commands.CommandAllowingAtMostOneOutputStateForTestStateA())
                failsWith("There must be at most 1 output state(s) of type(s) [TestState_A]")
            }
            transaction {
                output(TestContract2.ID, TestContract2.getTestStateA(companyA.party, companyB.party, companyC.party, UniqueIdentifier()))
                output(TestContract2.ID, TestContract2.getTestStateB(companyA.party, companyB.party, companyC.party, UniqueIdentifier()))
                output(TestContract2.ID, TestContract2.getTestStateB(companyA.party, companyB.party, companyC.party, UniqueIdentifier()))
                output(TestContract2.ID, TestContract2.getTestStateB(companyA.party, companyB.party, companyC.party, UniqueIdentifier()))
                command(companyA.publicKey, TestContract2.Commands.CommandAllowingBetweenOneAndTwoOutputStatesForTestStateA())
                verifies()
            }
            transaction {
                output(TestContract2.ID, TestContract2.getTestStateA(companyA.party, companyB.party, companyC.party, UniqueIdentifier()))
                output(TestContract2.ID, TestContract2.getTestStateA(companyA.party, companyB.party, companyC.party, UniqueIdentifier()))
                output(TestContract2.ID, TestContract2.getTestStateA(companyA.party, companyB.party, companyC.party, UniqueIdentifier()))
                output(TestContract2.ID, TestContract2.getTestStateB(companyA.party, companyB.party, companyC.party, UniqueIdentifier()))
                command(companyA.publicKey, TestContract2.Commands.CommandAllowingBetweenOneAndTwoOutputStatesForTestStateA())
                failsWith("There must be at most 2 output state(s) of type(s) [TestState_A]")
            }
        }
    }

    @Test
    fun `Allowed statuses on input`() {
        ledgerServices.ledger {
            transaction {
                input(TestContract.ID, getTestStateWithStatus("X"))
                command(companyA.publicKey, TestContract.Commands.CommandAllowingOnlyStatusXOnInput())
                verifies()
            }
            transaction {
                input(TestContract.ID, getTestStateWithStatus("Y"))
                command(companyA.publicKey, TestContract.Commands.CommandAllowingOnlyStatusXOnInput())
                failsWith("The input state must be in status X")
            }
            transaction {
                input(TestContract.ID, getTestStateWithStatus("X"))
                command(companyA.publicKey, TestContract.Commands.CommandAllowingOnlyStatusesXAndYOnInput())
                verifies()
            }
            transaction {
                input(TestContract.ID, getTestStateWithStatus("Y"))
                command(companyA.publicKey, TestContract.Commands.CommandAllowingOnlyStatusesXAndYOnInput())
                verifies()
            }
            transaction {
                input(TestContract.ID, getTestStateWithStatus("X"))
                input(TestContract.ID, getTestStateWithStatus("Y"))
                command(companyA.publicKey, TestContract.Commands.CommandAllowingOnlyStatusesXAndYOnInput())
                verifies()
            }
            transaction {
                input(TestContract.ID, getTestStateWithStatus("Z"))
                command(companyA.publicKey, TestContract.Commands.CommandAllowingOnlyStatusesXAndYOnInput())
                failsWith("The input state must be in one of these statuses: [X, Y]")
            }
            transaction {
                input(TestContract.ID, getTestStateWithStatus("X"))
                input(TestContract.ID, getTestStateWithStatus("Z"))
                command(companyA.publicKey, TestContract.Commands.CommandAllowingOnlyStatusesXAndYOnInput())
                failsWith("The input state must be in one of these statuses: [X, Y]")
            }
            transaction {
                input(TestContract2.ID, TestContract2.getTestStateB(companyA.party, companyB.party, companyC.party, UniqueIdentifier(), "Y"))
                command(companyA.publicKey, TestContract2.Commands.CommandAllowingOnlyStatusXOnInputForTestStateA())
                verifies()
            }
            transaction {
                input(TestContract2.ID, TestContract2.getTestStateA(companyA.party, companyB.party, companyC.party, UniqueIdentifier(), "Y"))
                command(companyA.publicKey, TestContract2.Commands.CommandAllowingOnlyStatusXOnInputForTestStateA())
                failsWith("The input state must be in status X if the state is of type(s) [TestState_A]")
            }
            transaction {
                input(TestContract2.ID, TestContract2.getTestStateA(companyA.party, companyB.party, companyC.party, UniqueIdentifier(), "Y"))
                input(TestContract2.ID, TestContract2.getTestStateB(companyA.party, companyB.party, companyC.party, UniqueIdentifier(), null))
                command(companyA.publicKey, TestContract2.Commands.CommandAllowingOnlyStatusesXAndYOnInputForTestStateA())
                verifies()
            }
            transaction {
                input(TestContract2.ID, TestContract2.getTestStateA(companyA.party, companyB.party, companyC.party, UniqueIdentifier(), null))
                command(companyA.publicKey, TestContract2.Commands.CommandAllowingOnlyStatusesXAndYOnInputForTestStateA())
                failsWith("The input state must be in one of these statuses: [X, Y] if the state is of type(s) [TestState_A]")
            }
        }
    }

    @Test
    fun `Allowed statuses on output`() {
        ledgerServices.ledger {
            transaction {
                output(TestContract.ID, getTestStateWithStatus("X"))
                command(companyA.publicKey, TestContract.Commands.CommandAllowingOnlyStatusXOnOutput())
                verifies()
            }
            transaction {
                output(TestContract.ID, getTestStateWithStatus("Y"))
                command(companyA.publicKey, TestContract.Commands.CommandAllowingOnlyStatusXOnOutput())
                failsWith("The output state must be in status X")
            }
            transaction {
                output(TestContract.ID, getTestStateWithStatus("X"))
                command(companyA.publicKey, TestContract.Commands.CommandAllowingOnlyStatusesXAndYOnOutput())
                verifies()
            }
            transaction {
                output(TestContract.ID, getTestStateWithStatus("Y"))
                command(companyA.publicKey, TestContract.Commands.CommandAllowingOnlyStatusesXAndYOnOutput())
                verifies()
            }
            transaction {
                output(TestContract.ID, getTestStateWithStatus("X"))
                output(TestContract.ID, getTestStateWithStatus("Y"))
                command(companyA.publicKey, TestContract.Commands.CommandAllowingOnlyStatusesXAndYOnOutput())
                verifies()
            }
            transaction {
                output(TestContract.ID, getTestStateWithStatus("Z"))
                command(companyA.publicKey, TestContract.Commands.CommandAllowingOnlyStatusesXAndYOnOutput())
                failsWith("The output state must be in one of these statuses: [X, Y]")
            }
            transaction {
                output(TestContract.ID, getTestStateWithStatus("X"))
                output(TestContract.ID, getTestStateWithStatus("Z"))
                command(companyA.publicKey, TestContract.Commands.CommandAllowingOnlyStatusesXAndYOnOutput())
                failsWith("The output state must be in one of these statuses: [X, Y]")
            }
            transaction {
                output(TestContract2.ID, TestContract2.getTestStateB(companyA.party, companyB.party, companyC.party, UniqueIdentifier(), "Y"))
                command(companyA.publicKey, TestContract2.Commands.CommandAllowingOnlyStatusXOnOutputForTestStateA())
                verifies()
            }
            transaction {
                output(TestContract2.ID, TestContract2.getTestStateA(companyA.party, companyB.party, companyC.party, UniqueIdentifier(), "Y"))
                command(companyA.publicKey, TestContract2.Commands.CommandAllowingOnlyStatusXOnOutputForTestStateA())
                failsWith("The output state must be in status X if the state is of type(s) [TestState_A]")
            }
            transaction {
                output(TestContract2.ID, TestContract2.getTestStateA(companyA.party, companyB.party, companyC.party, UniqueIdentifier(), "Y"))
                output(TestContract2.ID, TestContract2.getTestStateB(companyA.party, companyB.party, companyC.party, UniqueIdentifier(), null))
                command(companyA.publicKey, TestContract2.Commands.CommandAllowingOnlyStatusesXAndYOnOutputForTestStateA())
                verifies()
            }
            transaction {
                output(TestContract2.ID, TestContract2.getTestStateA(companyA.party, companyB.party, companyC.party, UniqueIdentifier(), null))
                command(companyA.publicKey, TestContract2.Commands.CommandAllowingOnlyStatusesXAndYOnOutputForTestStateA())
                failsWith("The output state must be in one of these statuses: [X, Y] if the state is of type(s) [TestState_A]")
            }
        }
    }

    @Test
    fun `Required signers on input`() {
        ledgerServices.ledger {
            transaction {
                input(TestContract.ID, getTestStateWithRoles(roleX = companyA.party, roleY = companyB.party, roleZ = companyC.party))
                command(companyA.publicKey, TestContract.Commands.CommandRequiringRoleXFromInputToBeASigner())
                verifies()
            }
            transaction {
                input(TestContract.ID, getTestStateWithRoles(roleX = companyA.party, roleY = companyB.party, roleZ = companyC.party))
                command(listOf(companyA.publicKey, companyB.publicKey), TestContract.Commands.CommandRequiringRoleXFromInputToBeASigner())
                verifies()
            }
            transaction {
                input(TestContract.ID, getTestStateWithRoles(roleX = companyA.party, roleY = companyB.party, roleZ = companyC.party))
                command(companyB.publicKey, TestContract.Commands.CommandRequiringRoleXFromInputToBeASigner())
                failsWith("The X from the input must sign the transaction.")
            }
            transaction {
                input(TestContract.ID, getTestStateWithRoles(roleX = companyB.party, roleY = companyB.party, roleZ = companyC.party))
                output(TestContract.ID, getTestStateWithRoles(roleX = companyA.party, roleY = companyB.party, roleZ = companyC.party))
                command(companyA.publicKey, TestContract.Commands.CommandRequiringRoleXFromInputToBeASigner())
                failsWith("The X from the input must sign the transaction.")
            }
            transaction {
                input(TestContract2.ID, TestContract2.getTestStateA(companyA.party, companyB.party, companyC.party, UniqueIdentifier()))
                input(TestContract2.ID, TestContract2.getTestStateB(companyB.party, companyB.party, companyC.party, UniqueIdentifier()))
                command(companyA.publicKey, TestContract2.Commands.CommandRequiringRoleXFromTestStateAOnInputToBeASigner())
                verifies()
            }
            transaction {
                input(TestContract2.ID, TestContract2.getTestStateA(companyB.party, companyB.party, companyC.party, UniqueIdentifier()))
                input(TestContract2.ID, TestContract2.getTestStateB(companyA.party, companyB.party, companyC.party, UniqueIdentifier()))
                command(companyA.publicKey, TestContract2.Commands.CommandRequiringRoleXFromTestStateAOnInputToBeASigner())
                failsWith("The X from [TestState_A] on the input must sign the transaction.")
            }
        }
    }

    @Test
    fun `Required signers on output`() {
        ledgerServices.ledger {
            transaction {
                output(TestContract.ID, getTestStateWithRoles(roleX = companyA.party, roleY = companyB.party, roleZ = companyC.party))
                command(companyA.publicKey, TestContract.Commands.CommandRequiringRoleXFromOutputToBeASigner())
                verifies()
            }
            transaction {
                output(TestContract.ID, getTestStateWithRoles(roleX = companyA.party, roleY = companyB.party, roleZ = companyC.party))
                command(listOf(companyA.publicKey, companyB.publicKey), TestContract.Commands.CommandRequiringRoleXFromOutputToBeASigner())
                verifies()
            }
            transaction {
                output(TestContract.ID, getTestStateWithRoles(roleX = companyA.party, roleY = companyB.party, roleZ = companyC.party))
                command(companyB.publicKey, TestContract.Commands.CommandRequiringRoleXFromOutputToBeASigner())
                failsWith("The X from the output must sign the transaction.")
            }
            transaction {
                input(TestContract.ID, getTestStateWithRoles(roleX = companyA.party, roleY = companyB.party, roleZ = companyC.party))
                output(TestContract.ID, getTestStateWithRoles(roleX = companyB.party, roleY = companyB.party, roleZ = companyC.party))
                command(companyA.publicKey, TestContract.Commands.CommandRequiringRoleXFromOutputToBeASigner())
                failsWith("The X from the output must sign the transaction.")
            }
            transaction {
                output(TestContract2.ID, TestContract2.getTestStateA(companyA.party, companyB.party, companyC.party, UniqueIdentifier()))
                output(TestContract2.ID, TestContract2.getTestStateB(companyB.party, companyB.party, companyC.party, UniqueIdentifier()))
                command(companyA.publicKey, TestContract2.Commands.CommandRequiringRoleXFromTestStateAOnOutputToBeASigner())
                verifies()
            }
            transaction {
                output(TestContract2.ID, TestContract2.getTestStateA(companyB.party, companyB.party, companyC.party, UniqueIdentifier()))
                output(TestContract2.ID, TestContract2.getTestStateB(companyA.party, companyB.party, companyC.party, UniqueIdentifier()))
                command(companyA.publicKey, TestContract2.Commands.CommandRequiringRoleXFromTestStateAOnOutputToBeASigner())
                failsWith("The X from [TestState_A] on the output must sign the transaction.")
            }
        }
    }

    @Test
    fun `Required status change in coupled linear states`() {
        val linearId1 = UniqueIdentifier()
        val linearId2 = UniqueIdentifier()
        ledgerServices.ledger {
            transaction {
                input(TestContract.ID, getTestLinearStateWithStatus(linearId1, "X"))
                output(TestContract.ID, getTestLinearStateWithStatus(linearId1, "Y"))
                command(companyA.publicKey, TestContract.Commands.CommandAllowingOnlyChangeFromStatusXtoYOnCoupledLinearStates())
                verifies()
            }
            transaction {
                input(TestContract.ID, getTestLinearStateWithStatus(linearId1, "X"))
                output(TestContract.ID, getTestLinearStateWithStatus(linearId1, "Z"))
                command(companyA.publicKey, TestContract.Commands.CommandAllowingOnlyChangeFromStatusXtoYOnCoupledLinearStates())
                failsWith("The status must be transitioning from X to Y")
            }
            transaction {
                input(TestContract.ID, getTestLinearStateWithStatus(linearId1, "Z"))
                output(TestContract.ID, getTestLinearStateWithStatus(linearId1, "Y"))
                command(companyA.publicKey, TestContract.Commands.CommandAllowingOnlyChangeFromStatusXtoYOnCoupledLinearStates())
                failsWith("The status must be transitioning from X to Y")
            }
            transaction {
                input(TestContract.ID, getTestStateWithStatus("X"))
                output(TestContract.ID, getTestStateWithStatus("Y"))
                command(companyA.publicKey, TestContract.Commands.CommandAllowingOnlyChangeFromStatusXtoYOnCoupledLinearStates())
                failsWith("All input states must be linear and with a status")
            }
            transaction {
                input(TestContract.ID, getTestState())
                output(TestContract.ID, getTestState())
                command(companyA.publicKey, TestContract.Commands.CommandAllowingOnlyChangeFromStatusXtoYOnCoupledLinearStates())
                failsWith("All input states must be linear and with a status")
            }
            transaction {
                input(TestContract.ID, getTestLinearStateWithStatus(linearId1, "X"))
                input(TestContract.ID, getTestLinearStateWithStatus(linearId2, "X"))
                output(TestContract.ID, getTestLinearStateWithStatus(linearId1, "Y"))
                command(companyA.publicKey, TestContract.Commands.CommandAllowingOnlyChangeFromStatusXtoYOnCoupledLinearStates())
                failsWith("Number of input states and output states must be the same")
            }
            transaction {
                input(TestContract.ID, getTestLinearStateWithStatus(linearId1, "X"))
                output(TestContract.ID, getTestLinearStateWithStatus(linearId1, "Y"))
                output(TestContract.ID, getTestLinearStateWithStatus(linearId2, "X"))
                command(companyA.publicKey, TestContract.Commands.CommandAllowingOnlyChangeFromStatusXtoYOnCoupledLinearStates())
                failsWith("Number of input states and output states must be the same")
            }
            transaction {
                input(TestContract.ID, getTestLinearStateWithStatus(linearId1, "X"))
                output(TestContract.ID, getTestLinearStateWithStatus(linearId2, "Y"))
                command(companyA.publicKey, TestContract.Commands.CommandAllowingOnlyChangeFromStatusXtoYOnCoupledLinearStates())
                failsWith("Each input state must have a corresponding output state (with same linear id)")
            }
            transaction {
                input(TestContract2.ID, TestContract2.getTestStateA(companyA.party, companyB.party, companyC.party, linearId1, status = "X"))
                output(TestContract2.ID, TestContract2.getTestStateA(companyA.party, companyB.party, companyC.party, linearId1, status = "Y"))
                input(TestContract2.ID, TestContract2.getTestStateB(companyA.party, companyB.party, companyC.party, linearId2, status = "A"))
                output(TestContract2.ID, TestContract2.getTestStateB(companyA.party, companyB.party, companyC.party, linearId2, status = "B"))
                command(companyA.publicKey, TestContract2.Commands.CommandAllowingOnlyChangeFromStatusXtoYOnCoupledLinearStatesForTestStateA())
                verifies()
            }
            transaction {
                input(TestContract2.ID, TestContract2.getTestStateA(companyA.party, companyB.party, companyC.party, linearId1, status = "A"))
                output(TestContract2.ID, TestContract2.getTestStateA(companyA.party, companyB.party, companyC.party, linearId1, status = "B"))
                input(TestContract2.ID, TestContract2.getTestStateB(companyA.party, companyB.party, companyC.party, linearId2, status = "A"))
                output(TestContract2.ID, TestContract2.getTestStateB(companyA.party, companyB.party, companyC.party, linearId2, status = "B"))
                command(companyA.publicKey, TestContract2.Commands.CommandAllowingOnlyChangeFromStatusXtoYOnCoupledLinearStatesForTestStateA())
                failsWith("The status must be transitioning from X to Y for type(s) [TestState_A]")
            }
        }
    }

    @Test
    fun `Limited change in coupled linear states`() {
        val linearId1 = UniqueIdentifier()
        val linearId2 = UniqueIdentifier()
        ledgerServices.ledger {
            transaction {
                input(TestContract.ID, getTestLinearStateWithProperties(linearId1,"a", "b", "c"))
                output(TestContract.ID, getTestLinearStateWithProperties(linearId1,"A", "b", "c"))
                command(companyA.publicKey, TestContract.Commands.CommandAllowingOnlyChangeInPropertyXOnCoupledLinearStates())
                verifies()
            }
            transaction {
                input(TestContract.ID, getTestLinearStateWithProperties(linearId1,"a", "b", "c"))
                output(TestContract.ID, getTestLinearStateWithProperties(linearId1,"A", "B", "c"))
                command(companyA.publicKey, TestContract.Commands.CommandAllowingOnlyChangeInPropertyXOnCoupledLinearStates())
                failsWith("Property 'y' is not allowed to change between input and output")
            }
            transaction {
                input(TestContract.ID, getTestStateWithProperties("a", "b", "c"))
                output(TestContract.ID, getTestStateWithProperties("A", "b", "c"))
                command(companyA.publicKey, TestContract.Commands.CommandAllowingOnlyChangeInPropertyXOnCoupledLinearStates())
                failsWith("All input states must be linear")
            }
            transaction {
                input(TestContract.ID, getTestLinearStateWithProperties(linearId1,"a", "b", "c"))
                output(TestContract.ID, getTestLinearStateWithProperties(linearId2,"a", "b", "c"))
                command(companyA.publicKey, TestContract.Commands.CommandAllowingOnlyChangeInPropertyXOnCoupledLinearStates())
                failsWith("Each input state must have a corresponding output state (with same linear id)")
            }
            transaction {
                input(TestContract.ID, getTestLinearStateWithProperties(linearId1,"a", "b", "c"))
                output(TestContract.ID, getTestLinearStateWithProperties(linearId1,"A", "b", "c"))
                command(companyA.publicKey, TestContract.Commands.CommandAllowingOnlyChangeInPropertyXAndYOnCoupledLinearStates())
                verifies()
            }
            transaction {
                input(TestContract.ID, getTestLinearStateWithProperties(linearId1,"a", "b", "c"))
                output(TestContract.ID, getTestLinearStateWithProperties(linearId1,"A", "B", "c"))
                command(companyA.publicKey, TestContract.Commands.CommandAllowingOnlyChangeInPropertyXAndYOnCoupledLinearStates())
                verifies()
            }
            transaction {
                input(TestContract.ID, getTestLinearStateWithProperties(linearId1,"a", "b", "c"))
                output(TestContract.ID, getTestLinearStateWithProperties(linearId1,"A", "B", "C"))
                command(companyA.publicKey, TestContract.Commands.CommandAllowingOnlyChangeInPropertyXAndYOnCoupledLinearStates())
                failsWith("Property 'z' is not allowed to change between input and output")
            }
            transaction {
                input(TestContract2.ID, TestContract2.getTestStateA(companyA.party, companyB.party, companyC.party, linearId1, participants = listOf(companyA.party)))
                output(TestContract2.ID, TestContract2.getTestStateA(companyB.party, companyB.party, companyC.party, linearId1, participants = listOf(companyA.party)))
                input(TestContract2.ID, TestContract2.getTestStateB(companyA.party, companyB.party, companyC.party, linearId2, participants = listOf(companyA.party)))
                output(TestContract2.ID, TestContract2.getTestStateB(companyB.party, companyC.party, companyA.party, linearId2, participants = listOf(companyA.party)))
                command(companyA.publicKey, TestContract2.Commands.CommandAllowingOnlyChangeInPropertyXOnCoupledLinearStatesForTestStateA())
                verifies()
            }
            transaction {
                input(TestContract2.ID, TestContract2.getTestStateA(companyA.party, companyB.party, companyC.party, linearId1, participants = listOf(companyA.party)))
                output(TestContract2.ID, TestContract2.getTestStateA(companyB.party, companyC.party, companyA.party, linearId1, participants = listOf(companyA.party)))
                input(TestContract2.ID, TestContract2.getTestStateB(companyA.party, companyB.party, companyC.party, linearId2, participants = listOf(companyA.party)))
                output(TestContract2.ID, TestContract2.getTestStateB(companyB.party, companyC.party, companyA.party, linearId2, participants = listOf(companyA.party)))
                command(companyA.publicKey, TestContract2.Commands.CommandAllowingOnlyChangeInPropertyXOnCoupledLinearStatesForTestStateA())
                failsWith("Property 'y' is not allowed to change between input and output on type(s) [TestState_A]")
            }
        }
    }

    @Test
    fun `Required distinct parties in input states`() {
        val linearId1 = UniqueIdentifier()
        ledgerServices.ledger {
            transaction {
                input(TestContract.ID, getTestStateWithRoles(roleX = companyA.party, roleY = companyB.party, roleZ = companyA.party))
                command(companyA.publicKey, TestContract.Commands.CommandMandatingDistinctPartiesForRolesXAndYOnEachInputState())
                verifies()
            }
            transaction {
                input(TestContract.ID, getTestStateWithRoles(roleX = companyA.party, roleY = companyA.party, roleZ = companyB.party))
                command(companyA.publicKey, TestContract.Commands.CommandMandatingDistinctPartiesForRolesXAndYOnEachInputState())
                failsWith("Each of these roles must be a different party on input, [X, Y]")
            }
            transaction {
                input(TestContract.ID, getTestStateWithRoles(roleX = companyA.party, roleY = companyB.party, roleZ = companyA.party))
                input(TestContract.ID, getTestStateWithRoles(roleX = companyA.party, roleY = companyA.party, roleZ = companyB.party))
                command(companyA.publicKey, TestContract.Commands.CommandMandatingDistinctPartiesForRolesXAndYOnEachInputState())
                failsWith("Each of these roles must be a different party on input, [X, Y]")
            }
            transaction {
                input(TestContract.ID, getTestLinearStateWithRoles(linearId = linearId1, roleX = companyA.party, roleY = companyB.party, roleZ = companyA.party))
                command(companyA.publicKey, TestContract.Commands.CommandMandatingDistinctPartiesForRolesXAndYOnEachInputState())
                verifies()
            }
            transaction {
                input(TestContract.ID, getTestLinearStateWithRoles(linearId = linearId1, roleX = companyA.party, roleY = companyA.party, roleZ = companyB.party))
                command(companyA.publicKey, TestContract.Commands.CommandMandatingDistinctPartiesForRolesXAndYOnEachInputState())
                failsWith("Each of these roles must be a different party on input, [X, Y]")
            }
            transaction {
                input(TestContract.ID, getTestLinearStateWithRoles(linearId = linearId1, roleX = companyA.party, roleY = companyB.party, roleZ = companyA.party))
                input(TestContract.ID, getTestLinearStateWithRoles(linearId = linearId1, roleX = companyA.party, roleY = companyA.party, roleZ = companyB.party))
                command(companyA.publicKey, TestContract.Commands.CommandMandatingDistinctPartiesForRolesXAndYOnEachInputState())
                failsWith("Each of these roles must be a different party on input, [X, Y]")
            }
            transaction {
                input(TestContract.ID, getTestStateWithRoles(roleX = companyA.party, roleY = companyB.party, roleZ = companyC.party))
                command(companyA.publicKey, TestContract.Commands.CommandMandatingRolesXAndYAndZToBeDistinctOnEachInputState())
                verifies()
            }
            transaction {
                input(TestContract.ID, getTestStateWithRoles(roleX = companyA.party, roleY = companyA.party, roleZ = companyC.party))
                command(companyA.publicKey, TestContract.Commands.CommandMandatingRolesXAndYAndZToBeDistinctOnEachInputState())
                failsWith("Each of these roles must be a different party on input, [X, Y].")
            }
            transaction {
                input(TestContract.ID, getTestStateWithRoles(roleX = companyA.party, roleY = companyB.party, roleZ = companyB.party))
                command(companyA.publicKey, TestContract.Commands.CommandMandatingRolesXAndYAndZToBeDistinctOnEachInputState())
                failsWith("Each of these roles must be a different party on input, [Y, Z].")
            }
            transaction {
                input(TestContract2.ID, TestContract2.getTestStateA(companyA.party, companyB.party, companyB.party, UniqueIdentifier()))
                input(TestContract2.ID, TestContract2.getTestStateB(companyA.party, companyA.party, companyB.party, UniqueIdentifier()))
                command(companyA.publicKey, TestContract2.Commands.CommandMandatingDistinctPartiesForRolesXAndYOnEachInputStateForTestStateA())
                verifies()
            }
            transaction {
                input(TestContract2.ID, TestContract2.getTestStateA(companyA.party, companyA.party, companyB.party, UniqueIdentifier()))
                input(TestContract2.ID, TestContract2.getTestStateB(companyA.party, companyA.party, companyB.party, UniqueIdentifier()))
                command(companyA.publicKey, TestContract2.Commands.CommandMandatingDistinctPartiesForRolesXAndYOnEachInputStateForTestStateA())
                failsWith("Each of these roles must be a different party on input of type(s) [TestState_A], [X, Y].")
            }
        }
    }

    @Test
    fun `Required distinct parties in output states`() {
        val linearId1 = UniqueIdentifier()
        ledgerServices.ledger {
            transaction {
                output(TestContract.ID, getTestStateWithRoles(roleX = companyA.party, roleY = companyB.party, roleZ = companyA.party))
                command(companyA.publicKey, TestContract.Commands.CommandMandatingDistinctPartiesForRolesXAndYOnEachOutputState())
                verifies()
            }
            transaction {
                output(TestContract.ID, getTestStateWithRoles(roleX = companyA.party, roleY = companyA.party, roleZ = companyB.party))
                command(companyA.publicKey, TestContract.Commands.CommandMandatingDistinctPartiesForRolesXAndYOnEachOutputState())
                failsWith("Each of these roles must be a different party on output, [X, Y]")
            }
            transaction {
                output(TestContract.ID, getTestStateWithRoles(roleX = companyA.party, roleY = companyB.party, roleZ = companyA.party))
                output(TestContract.ID, getTestStateWithRoles(roleX = companyA.party, roleY = companyA.party, roleZ = companyB.party))
                command(companyA.publicKey, TestContract.Commands.CommandMandatingDistinctPartiesForRolesXAndYOnEachOutputState())
                failsWith("Each of these roles must be a different party on output, [X, Y]")
            }
            transaction {
                output(TestContract.ID, getTestLinearStateWithRoles(linearId = linearId1, roleX = companyA.party, roleY = companyB.party, roleZ = companyA.party))
                command(companyA.publicKey, TestContract.Commands.CommandMandatingDistinctPartiesForRolesXAndYOnEachOutputState())
                verifies()
            }
            transaction {
                output(TestContract.ID, getTestLinearStateWithRoles(linearId = linearId1, roleX = companyA.party, roleY = companyA.party, roleZ = companyB.party))
                command(companyA.publicKey, TestContract.Commands.CommandMandatingDistinctPartiesForRolesXAndYOnEachOutputState())
                failsWith("Each of these roles must be a different party on output, [X, Y]")
            }
            transaction {
                output(TestContract.ID, getTestLinearStateWithRoles(linearId = linearId1, roleX = companyA.party, roleY = companyB.party, roleZ = companyA.party))
                output(TestContract.ID, getTestLinearStateWithRoles(linearId = linearId1, roleX = companyA.party, roleY = companyA.party, roleZ = companyB.party))
                command(companyA.publicKey, TestContract.Commands.CommandMandatingDistinctPartiesForRolesXAndYOnEachOutputState())
                failsWith("Each of these roles must be a different party on output, [X, Y]")
            }
            transaction {
                output(TestContract.ID, getTestStateWithRoles(roleX = companyA.party, roleY = companyB.party, roleZ = companyC.party))
                command(companyA.publicKey, TestContract.Commands.CommandMandatingRolesXAndYAndZToBeDistinctOnEachOutputState())
                verifies()
            }
            transaction {
                output(TestContract.ID, getTestStateWithRoles(roleX = companyA.party, roleY = companyA.party, roleZ = companyC.party))
                command(companyA.publicKey, TestContract.Commands.CommandMandatingRolesXAndYAndZToBeDistinctOnEachOutputState())
                failsWith("Each of these roles must be a different party on output, [X, Y].")
            }
            transaction {
                output(TestContract.ID, getTestStateWithRoles(roleX = companyA.party, roleY = companyB.party, roleZ = companyB.party))
                command(companyA.publicKey, TestContract.Commands.CommandMandatingRolesXAndYAndZToBeDistinctOnEachOutputState())
                failsWith("Each of these roles must be a different party on output, [Y, Z].")
            }
            transaction {
                output(TestContract2.ID, TestContract2.getTestStateA(companyA.party, companyB.party, companyB.party, UniqueIdentifier()))
                output(TestContract2.ID, TestContract2.getTestStateB(companyA.party, companyA.party, companyB.party, UniqueIdentifier()))
                command(companyA.publicKey, TestContract2.Commands.CommandMandatingDistinctPartiesForRolesXAndYOnEachOutputStateForTestStateA())
                verifies()
            }
            transaction {
                output(TestContract2.ID, TestContract2.getTestStateA(companyA.party, companyA.party, companyB.party, UniqueIdentifier()))
                output(TestContract2.ID, TestContract2.getTestStateB(companyA.party, companyA.party, companyB.party, UniqueIdentifier()))
                command(companyA.publicKey, TestContract2.Commands.CommandMandatingDistinctPartiesForRolesXAndYOnEachOutputStateForTestStateA())
                failsWith("Each of these roles must be a different party on output of type(s) [TestState_A], [X, Y].")
            }
        }
    }

    @Test
    fun `Required same party assuming at least one other role in input states`() {
        val linearId1 = UniqueIdentifier()
        ledgerServices.ledger {
            transaction {
                input(TestContract.ID, getTestStateWithRoles(roleX = companyA.party, roleY = companyB.party, roleZ = companyA.party))
                command(companyA.publicKey, TestContract.Commands.CommandMandatingRoleXAssumingAtLeastAlsoRoleYorZOnEachInputState())
                verifies()
            }
            transaction {
                input(TestContract.ID, getTestStateWithRoles(roleX = companyA.party, roleY = companyA.party, roleZ = companyB.party))
                command(companyA.publicKey, TestContract.Commands.CommandMandatingRoleXAssumingAtLeastAlsoRoleYorZOnEachInputState())
                verifies()
            }
            transaction {
                input(TestContract.ID, getTestStateWithRoles(roleX = companyA.party, roleY = companyA.party, roleZ = companyA.party))
                command(companyA.publicKey, TestContract.Commands.CommandMandatingRoleXAssumingAtLeastAlsoRoleYorZOnEachInputState())
                verifies()
            }
            transaction {
                input(TestContract.ID, getTestStateWithRoles(roleX = companyA.party, roleY = companyB.party, roleZ = companyC.party))
                command(companyA.publicKey, TestContract.Commands.CommandMandatingRoleXAssumingAtLeastAlsoRoleYorZOnEachInputState())
                failsWith("The X must also be one of [Y, Z] roles on input.")
            }
            transaction {
                input(TestContract.ID, getTestStateWithRoles(roleX = companyA.party, roleY = companyB.party, roleZ = companyC.party, roleU = companyA.party))
                command(companyA.publicKey, TestContract.Commands.CommandMandatingRoleXAssumingAtLeastAlsoRoleYorZOnEachInputState())
                failsWith("The X must also be one of [Y, Z] roles on input.")
            }
            transaction {
                input(TestContract.ID, getTestStateWithRoles(roleX = companyA.party, roleY = companyB.party, roleZ = companyA.party))
                input(TestContract.ID, getTestStateWithRoles(roleX = companyA.party, roleY = companyB.party, roleZ = companyC.party))
                command(companyA.publicKey, TestContract.Commands.CommandMandatingRoleXAssumingAtLeastAlsoRoleYorZOnEachInputState())
                failsWith("The X must also be one of [Y, Z] roles on input.")
            }
            transaction {
                input(TestContract.ID, getTestLinearStateWithRoles(linearId1, roleX = companyA.party, roleY = companyB.party, roleZ = companyA.party))
                command(companyA.publicKey, TestContract.Commands.CommandMandatingRoleXAssumingAtLeastAlsoRoleYorZOnEachInputState())
                verifies()
            }
            transaction {
                input(TestContract.ID, getTestLinearStateWithRoles(linearId1, roleX = companyA.party, roleY = companyA.party, roleZ = companyB.party))
                command(companyA.publicKey, TestContract.Commands.CommandMandatingRoleXAssumingAtLeastAlsoRoleYorZOnEachInputState())
                verifies()
            }
            transaction {
                input(TestContract.ID, getTestLinearStateWithRoles(linearId1, roleX = companyA.party, roleY = companyA.party, roleZ = companyA.party))
                command(companyA.publicKey, TestContract.Commands.CommandMandatingRoleXAssumingAtLeastAlsoRoleYorZOnEachInputState())
                verifies()
            }
            transaction {
                input(TestContract.ID, getTestLinearStateWithRoles(linearId1, roleX = companyA.party, roleY = companyB.party, roleZ = companyC.party))
                command(companyA.publicKey, TestContract.Commands.CommandMandatingRoleXAssumingAtLeastAlsoRoleYorZOnEachInputState())
                failsWith("The X must also be one of [Y, Z] roles on input.")
            }
            transaction {
                input(TestContract.ID, getTestLinearStateWithRoles(linearId1, roleX = companyA.party, roleY = companyB.party, roleZ = companyC.party, roleU = companyA.party))
                command(companyA.publicKey, TestContract.Commands.CommandMandatingRoleXAssumingAtLeastAlsoRoleYorZOnEachInputState())
                failsWith("The X must also be one of [Y, Z] roles on input.")
            }
            transaction {
                input(TestContract.ID, getTestLinearStateWithRoles(linearId1, roleX = companyA.party, roleY = companyB.party, roleZ = companyA.party))
                input(TestContract.ID, getTestLinearStateWithRoles(linearId1, roleX = companyA.party, roleY = companyB.party, roleZ = companyC.party))
                command(companyA.publicKey, TestContract.Commands.CommandMandatingRoleXAssumingAtLeastAlsoRoleYorZOnEachInputState())
                failsWith("The X must also be one of [Y, Z] roles on input.")
            }
            transaction {
                input(TestContract.ID, getTestStateWithRoles(roleX = companyA.party, roleY = companyA.party, roleZ = companyA.party))
                command(companyA.publicKey, TestContract.Commands.CommandMandatingRoleXToBeAlsoRoleYAndRoleYToBeAlsoRoleZOnEachInputState())
                verifies()
            }
            transaction {
                input(TestContract.ID, getTestStateWithRoles(roleX = companyA.party, roleY = companyB.party, roleZ = companyB.party))
                command(companyA.publicKey, TestContract.Commands.CommandMandatingRoleXToBeAlsoRoleYAndRoleYToBeAlsoRoleZOnEachInputState())
                failsWith("The X must also be one of [Y] roles on input.")
            }
            transaction {
                input(TestContract.ID, getTestStateWithRoles(roleX = companyA.party, roleY = companyA.party, roleZ = companyB.party))
                command(companyA.publicKey, TestContract.Commands.CommandMandatingRoleXToBeAlsoRoleYAndRoleYToBeAlsoRoleZOnEachInputState())
                failsWith("The Y must also be one of [Z] roles on input.")
            }
            transaction {
                input(TestContract2.ID, TestContract2.getTestStateA(companyA.party, companyA.party, companyC.party, UniqueIdentifier()))
                input(TestContract2.ID, TestContract2.getTestStateB(companyA.party, companyB.party, companyC.party, UniqueIdentifier()))
                command(companyA.publicKey, TestContract2.Commands.CommandMandatingRoleXAssumingAtLeastAlsoRoleYorZOnEachInputStateForTestStateA())
                verifies()
            }
            transaction {
                input(TestContract2.ID, TestContract2.getTestStateA(companyA.party, companyB.party, companyC.party, UniqueIdentifier()))
                input(TestContract2.ID, TestContract2.getTestStateB(companyA.party, companyB.party, companyC.party, UniqueIdentifier()))
                command(companyA.publicKey, TestContract2.Commands.CommandMandatingRoleXAssumingAtLeastAlsoRoleYorZOnEachInputStateForTestStateA())
                failsWith("The X must also be one of [Y, Z] roles on input for type(s) [TestState_A].")
            }
        }
    }

    @Test
    fun `Required same party assuming at least one other role in output states`() {
        val linearId1 = UniqueIdentifier()
        ledgerServices.ledger {
            transaction {
                output(TestContract.ID, getTestStateWithRoles(roleX = companyA.party, roleY = companyB.party, roleZ = companyA.party))
                command(companyA.publicKey, TestContract.Commands.CommandMandatingRoleXAssumingAtLeastAlsoRoleYorZOnEachOutputState())
                verifies()
            }
            transaction {
                output(TestContract.ID, getTestStateWithRoles(roleX = companyA.party, roleY = companyA.party, roleZ = companyB.party))
                command(companyA.publicKey, TestContract.Commands.CommandMandatingRoleXAssumingAtLeastAlsoRoleYorZOnEachOutputState())
                verifies()
            }
            transaction {
                output(TestContract.ID, getTestStateWithRoles(roleX = companyA.party, roleY = companyA.party, roleZ = companyA.party))
                command(companyA.publicKey, TestContract.Commands.CommandMandatingRoleXAssumingAtLeastAlsoRoleYorZOnEachOutputState())
                verifies()
            }
            transaction {
                output(TestContract.ID, getTestStateWithRoles(roleX = companyA.party, roleY = companyB.party, roleZ = companyC.party))
                command(companyA.publicKey, TestContract.Commands.CommandMandatingRoleXAssumingAtLeastAlsoRoleYorZOnEachOutputState())
                failsWith("The X must also be one of [Y, Z] roles on output.")
            }
            transaction {
                output(TestContract.ID, getTestStateWithRoles(roleX = companyA.party, roleY = companyB.party, roleZ = companyC.party, roleU = companyA.party))
                command(companyA.publicKey, TestContract.Commands.CommandMandatingRoleXAssumingAtLeastAlsoRoleYorZOnEachOutputState())
                failsWith("The X must also be one of [Y, Z] roles on output.")
            }
            transaction {
                output(TestContract.ID, getTestStateWithRoles(roleX = companyA.party, roleY = companyB.party, roleZ = companyA.party))
                output(TestContract.ID, getTestStateWithRoles(roleX = companyA.party, roleY = companyB.party, roleZ = companyC.party))
                command(companyA.publicKey, TestContract.Commands.CommandMandatingRoleXAssumingAtLeastAlsoRoleYorZOnEachOutputState())
                failsWith("The X must also be one of [Y, Z] roles on output.")
            }
            transaction {
                output(TestContract.ID, getTestLinearStateWithRoles(linearId1, roleX = companyA.party, roleY = companyB.party, roleZ = companyA.party))
                command(companyA.publicKey, TestContract.Commands.CommandMandatingRoleXAssumingAtLeastAlsoRoleYorZOnEachOutputState())
                verifies()
            }
            transaction {
                output(TestContract.ID, getTestLinearStateWithRoles(linearId1, roleX = companyA.party, roleY = companyA.party, roleZ = companyB.party))
                command(companyA.publicKey, TestContract.Commands.CommandMandatingRoleXAssumingAtLeastAlsoRoleYorZOnEachOutputState())
                verifies()
            }
            transaction {
                output(TestContract.ID, getTestLinearStateWithRoles(linearId1, roleX = companyA.party, roleY = companyA.party, roleZ = companyA.party))
                command(companyA.publicKey, TestContract.Commands.CommandMandatingRoleXAssumingAtLeastAlsoRoleYorZOnEachOutputState())
                verifies()
            }
            transaction {
                output(TestContract.ID, getTestLinearStateWithRoles(linearId1, roleX = companyA.party, roleY = companyB.party, roleZ = companyC.party))
                command(companyA.publicKey, TestContract.Commands.CommandMandatingRoleXAssumingAtLeastAlsoRoleYorZOnEachOutputState())
                failsWith("The X must also be one of [Y, Z] roles on output.")
            }
            transaction {
                output(TestContract.ID, getTestLinearStateWithRoles(linearId1, roleX = companyA.party, roleY = companyB.party, roleZ = companyC.party, roleU = companyA.party))
                command(companyA.publicKey, TestContract.Commands.CommandMandatingRoleXAssumingAtLeastAlsoRoleYorZOnEachOutputState())
                failsWith("The X must also be one of [Y, Z] roles on output.")
            }
            transaction {
                output(TestContract.ID, getTestLinearStateWithRoles(linearId1, roleX = companyA.party, roleY = companyB.party, roleZ = companyA.party))
                output(TestContract.ID, getTestLinearStateWithRoles(linearId1, roleX = companyA.party, roleY = companyB.party, roleZ = companyC.party))
                command(companyA.publicKey, TestContract.Commands.CommandMandatingRoleXAssumingAtLeastAlsoRoleYorZOnEachOutputState())
                failsWith("The X must also be one of [Y, Z] roles on output.")
            }
            transaction {
                output(TestContract.ID, getTestStateWithRoles(roleX = companyA.party, roleY = companyA.party, roleZ = companyA.party))
                command(companyA.publicKey, TestContract.Commands.CommandMandatingRoleXToBeAlsoRoleYAndRoleYToBeAlsoRoleZOnEachOutputState())
                verifies()
            }
            transaction {
                output(TestContract.ID, getTestStateWithRoles(roleX = companyA.party, roleY = companyB.party, roleZ = companyB.party))
                command(companyA.publicKey, TestContract.Commands.CommandMandatingRoleXToBeAlsoRoleYAndRoleYToBeAlsoRoleZOnEachOutputState())
                failsWith("The X must also be one of [Y] roles on output.")
            }
            transaction {
                output(TestContract.ID, getTestStateWithRoles(roleX = companyA.party, roleY = companyA.party, roleZ = companyB.party))
                command(companyA.publicKey, TestContract.Commands.CommandMandatingRoleXToBeAlsoRoleYAndRoleYToBeAlsoRoleZOnEachOutputState())
                failsWith("The Y must also be one of [Z] roles on output.")
            }
            transaction {
                output(TestContract2.ID, TestContract2.getTestStateA(companyA.party, companyA.party, companyC.party, UniqueIdentifier()))
                output(TestContract2.ID, TestContract2.getTestStateB(companyA.party, companyB.party, companyC.party, UniqueIdentifier()))
                command(companyA.publicKey, TestContract2.Commands.CommandMandatingRoleXAssumingAtLeastAlsoRoleYorZOnEachOutputStateForTestStateA())
                verifies()
            }
            transaction {
                output(TestContract2.ID, TestContract2.getTestStateA(companyA.party, companyB.party, companyC.party, UniqueIdentifier()))
                output(TestContract2.ID, TestContract2.getTestStateB(companyA.party, companyB.party, companyC.party, UniqueIdentifier()))
                command(companyA.publicKey, TestContract2.Commands.CommandMandatingRoleXAssumingAtLeastAlsoRoleYorZOnEachOutputStateForTestStateA())
                failsWith("The X must also be one of [Y, Z] roles on output for type(s) [TestState_A].")
            }
        }
    }

    @Test
    fun `Required properties set on input`() {
        ledgerServices.ledger {
            transaction {
                input(TestContract.ID, getTestStateWithProperties(x = "a", y = null, z = null))
                command(companyA.publicKey, TestContract.Commands.CommandMandatingPropertyXToBeSetOnEachInputState())
                verifies()
            }
            transaction {
                input(TestContract.ID, getTestStateWithProperties(x = null, y = "b", z = "c"))
                command(companyA.publicKey, TestContract.Commands.CommandMandatingPropertyXToBeSetOnEachInputState())
                failsWith("Property 'x' cannot be null on the input.")
            }
            transaction {
                input(TestContract.ID, getTestStateWithProperties(x = "a", y = "b", z = null))
                command(companyA.publicKey, TestContract.Commands.CommandMandatingPropertiesXAndYToBeSetOnEachInputState())
                verifies()
            }
            transaction {
                input(TestContract.ID, getTestStateWithProperties(x = null, y = "b", z = "c"))
                command(companyA.publicKey, TestContract.Commands.CommandMandatingPropertiesXAndYToBeSetOnEachInputState())
                failsWith("Properties [x, y] cannot be null on the input but these are null: [x].")
            }
            transaction {
                input(TestContract.ID, getTestStateWithProperties(x = "a", y = null, z = "c"))
                command(companyA.publicKey, TestContract.Commands.CommandMandatingPropertiesXAndYToBeSetOnEachInputState())
                failsWith("Properties [x, y] cannot be null on the input but these are null: [y].")
            }
            transaction {
                input(TestContract.ID, getTestStateWithProperties(x = null, y = null, z = "c"))
                command(companyA.publicKey, TestContract.Commands.CommandMandatingPropertiesXAndYToBeSetOnEachInputState())
                failsWith("Properties [x, y] cannot be null on the input but these are null: [x, y].")
            }
            transaction {
                input(TestContract2.ID, TestContract2.getTestStateA(companyA.party, companyB.party, companyC.party, UniqueIdentifier()))
                input(TestContract2.ID, TestContract2.getTestStateB(null, companyB.party, companyC.party, UniqueIdentifier()))
                command(companyA.publicKey, TestContract2.Commands.CommandMandatingPropertyXToBeSetOnEachInputStateForTestStateA())
                verifies()
            }
            transaction {
                input(TestContract2.ID, TestContract2.getTestStateA(null, companyB.party, companyC.party, UniqueIdentifier()))
                input(TestContract2.ID, TestContract2.getTestStateB(null, companyB.party, companyC.party, UniqueIdentifier()))
                command(companyA.publicKey, TestContract2.Commands.CommandMandatingPropertyXToBeSetOnEachInputStateForTestStateA())
                failsWith("Property 'x' cannot be null on the input for type(s) [TestState_A].")
            }
        }
    }

    @Test
    fun `Required properties set on output`() {
        ledgerServices.ledger {
            transaction {
                output(TestContract.ID, getTestStateWithProperties(x = "a", y = null, z = null))
                command(companyA.publicKey, TestContract.Commands.CommandMandatingPropertyXToBeSetOnEachOutputState())
                verifies()
            }
            transaction {
                output(TestContract.ID, getTestStateWithProperties(x = null, y = "b", z = "c"))
                command(companyA.publicKey, TestContract.Commands.CommandMandatingPropertyXToBeSetOnEachOutputState())
                failsWith("Property 'x' cannot be null on the output.")
            }
            transaction {
                output(TestContract.ID, getTestStateWithProperties(x = "a", y = "b", z = null))
                command(companyA.publicKey, TestContract.Commands.CommandMandatingPropertiesXAndYToBeSetOnEachOutputState())
                verifies()
            }
            transaction {
                output(TestContract.ID, getTestStateWithProperties(x = null, y = "b", z = "c"))
                command(companyA.publicKey, TestContract.Commands.CommandMandatingPropertiesXAndYToBeSetOnEachOutputState())
                failsWith("Properties [x, y] cannot be null on the output but these are null: [x].")
            }
            transaction {
                output(TestContract.ID, getTestStateWithProperties(x = "a", y = null, z = "c"))
                command(companyA.publicKey, TestContract.Commands.CommandMandatingPropertiesXAndYToBeSetOnEachOutputState())
                failsWith("Properties [x, y] cannot be null on the output but these are null: [y].")
            }
            transaction {
                output(TestContract.ID, getTestStateWithProperties(x = null, y = null, z = "c"))
                command(companyA.publicKey, TestContract.Commands.CommandMandatingPropertiesXAndYToBeSetOnEachOutputState())
                failsWith("Properties [x, y] cannot be null on the output but these are null: [x, y].")
            }
            transaction {
                output(TestContract2.ID, TestContract2.getTestStateA(companyA.party, companyB.party, companyC.party, UniqueIdentifier()))
                output(TestContract2.ID, TestContract2.getTestStateB(null, companyB.party, companyC.party, UniqueIdentifier()))
                command(companyA.publicKey, TestContract2.Commands.CommandMandatingPropertyXToBeSetOnEachOutputStateForTestStateA())
                verifies()
            }
            transaction {
                output(TestContract2.ID, TestContract2.getTestStateA(null, companyB.party, companyC.party, UniqueIdentifier()))
                output(TestContract2.ID, TestContract2.getTestStateB(null, companyB.party, companyC.party, UniqueIdentifier()))
                command(companyA.publicKey, TestContract2.Commands.CommandMandatingPropertyXToBeSetOnEachOutputStateForTestStateA())
                failsWith("Property 'x' cannot be null on the output for type(s) [TestState_A].")
            }
        }
    }

    @Test
    fun `Required properties not set on input`() {
        ledgerServices.ledger {
            transaction {
                input(TestContract.ID, getTestStateWithProperties(x = null, y = "b", z = "c"))
                command(companyA.publicKey, TestContract.Commands.CommandMandatingPropertyXToNotBeSetOnEachInputState())
                verifies()
            }
            transaction {
                input(TestContract.ID, getTestStateWithProperties(x = "a", y = null, z = null))
                command(companyA.publicKey, TestContract.Commands.CommandMandatingPropertyXToNotBeSetOnEachInputState())
                failsWith("Property 'x' must be null on the input.")
            }
            transaction {
                input(TestContract.ID, getTestStateWithProperties(x = null, y = null, z = "c"))
                command(companyA.publicKey, TestContract.Commands.CommandMandatingPropertiesXAndYToNotBeSetOnEachInputState())
                verifies()
            }
            transaction {
                input(TestContract.ID, getTestStateWithProperties(x = "a", y = null, z = null))
                command(companyA.publicKey, TestContract.Commands.CommandMandatingPropertiesXAndYToNotBeSetOnEachInputState())
                failsWith("Properties [x, y] must be null on the input but these are not: [x].")
            }
            transaction {
                input(TestContract.ID, getTestStateWithProperties(x = null, y = "b", z = null))
                command(companyA.publicKey, TestContract.Commands.CommandMandatingPropertiesXAndYToNotBeSetOnEachInputState())
                failsWith("Properties [x, y] must be null on the input but these are not: [y].")
            }
            transaction {
                input(TestContract.ID, getTestStateWithProperties(x = "a", y = "b", z = null))
                command(companyA.publicKey, TestContract.Commands.CommandMandatingPropertiesXAndYToNotBeSetOnEachInputState())
                failsWith("Properties [x, y] must be null on the input but these are not: [x, y].")
            }
            transaction {
                input(TestContract2.ID, TestContract2.getTestStateA(null, companyB.party, companyC.party, UniqueIdentifier()))
                input(TestContract2.ID, TestContract2.getTestStateB(companyA.party, companyB.party, companyC.party, UniqueIdentifier()))
                command(companyA.publicKey, TestContract2.Commands.CommandMandatingPropertyXToNotBeSetOnEachInputStateForTestStateA())
                verifies()
            }
            transaction {
                input(TestContract2.ID, TestContract2.getTestStateA(companyA.party, companyB.party, companyC.party, UniqueIdentifier()))
                input(TestContract2.ID, TestContract2.getTestStateB(companyA.party, companyB.party, companyC.party, UniqueIdentifier()))
                command(companyA.publicKey, TestContract2.Commands.CommandMandatingPropertyXToNotBeSetOnEachInputStateForTestStateA())
                failsWith("Property 'x' must be null on the input for type(s) [TestState_A].")
            }
        }
    }

    @Test
    fun `Required properties not set on output`() {
        ledgerServices.ledger {
            transaction {
                output(TestContract.ID, getTestStateWithProperties(x = null, y = "b", z = "c"))
                command(companyA.publicKey, TestContract.Commands.CommandMandatingPropertyXToNotBeSetOnEachOutputState())
                verifies()
            }
            transaction {
                output(TestContract.ID, getTestStateWithProperties(x = "a", y = null, z = null))
                command(companyA.publicKey, TestContract.Commands.CommandMandatingPropertyXToNotBeSetOnEachOutputState())
                failsWith("Property 'x' must be null on the output.")
            }
            transaction {
                output(TestContract.ID, getTestStateWithProperties(x = null, y = null, z = "c"))
                command(companyA.publicKey, TestContract.Commands.CommandMandatingPropertiesXAndYToNotBeSetOnEachOutputState())
                verifies()
            }
            transaction {
                output(TestContract.ID, getTestStateWithProperties(x = "a", y = null, z = null))
                command(companyA.publicKey, TestContract.Commands.CommandMandatingPropertiesXAndYToNotBeSetOnEachOutputState())
                failsWith("Properties [x, y] must be null on the output but these are not: [x].")
            }
            transaction {
                output(TestContract.ID, getTestStateWithProperties(x = null, y = "b", z = null))
                command(companyA.publicKey, TestContract.Commands.CommandMandatingPropertiesXAndYToNotBeSetOnEachOutputState())
                failsWith("Properties [x, y] must be null on the output but these are not: [y].")
            }
            transaction {
                output(TestContract.ID, getTestStateWithProperties(x = "a", y = "b", z = null))
                command(companyA.publicKey, TestContract.Commands.CommandMandatingPropertiesXAndYToNotBeSetOnEachOutputState())
                failsWith("Properties [x, y] must be null on the output but these are not: [x, y].")
            }
            transaction {
                output(TestContract2.ID, TestContract2.getTestStateA(null, companyB.party, companyC.party, UniqueIdentifier()))
                output(TestContract2.ID, TestContract2.getTestStateB(companyA.party, companyB.party, companyC.party, UniqueIdentifier()))
                command(companyA.publicKey, TestContract2.Commands.CommandMandatingPropertyXToNotBeSetOnEachOutputStateForTestStateA())
                verifies()
            }
            transaction {
                output(TestContract2.ID, TestContract2.getTestStateA(companyA.party, companyB.party, companyC.party, UniqueIdentifier()))
                output(TestContract2.ID, TestContract2.getTestStateB(companyA.party, companyB.party, companyC.party, UniqueIdentifier()))
                command(companyA.publicKey, TestContract2.Commands.CommandMandatingPropertyXToNotBeSetOnEachOutputStateForTestStateA())
                failsWith("Property 'x' must be null on the output for type(s) [TestState_A].")
            }
        }
    }

    private fun getTestState() = TestState(participants = listOf(companyA.party))
    private fun getTestStateWithStatus(status : String) = TestStateWithStatus(status = status, participants = listOf(companyA.party))
    private fun getTestLinearStateWithStatus(linearId : UniqueIdentifier, status : String) = TestLinearStateWithStatus(status = status, linearId = linearId, participants = listOf(companyA.party))
    private fun getTestStateWithRoles(roleX : Party, roleY : Party, roleZ : Party, roleU : Party? = null) = TestStateWithRoles(x = roleX, y = roleY, z = roleZ, u = roleU)
    private fun getTestLinearStateWithRoles(linearId : UniqueIdentifier, roleX : Party, roleY : Party, roleZ : Party, roleU : Party? = null) = TestLinearStateWithRoles(x = roleX, y = roleY, z = roleZ, u = roleU, linearId = linearId)
    private fun getTestStateWithProperties(x : String?, y : String?, z : String?) = TestStateWithProperties(x = x, y = y, z = z, participants = listOf(companyA.party))
    private fun getTestLinearStateWithProperties(linearId : UniqueIdentifier, x : String?, y : String?, z : String?) = TestLinearStateWithProperties(x = x, y = y, z = z, linearId = linearId, participants = listOf(companyA.party))
}