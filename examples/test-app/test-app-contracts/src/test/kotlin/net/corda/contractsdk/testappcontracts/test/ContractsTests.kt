package net.corda.contractsdk.testappcontracts.test

import net.corda.contractsdk.testappcontracts.LastingMembershipState
import net.corda.contractsdk.testappcontracts.MembershipContract
import net.corda.contractsdk.testappcontracts.OneUseMembershipState
import net.corda.contractsdk.testappcontracts.Status
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.identity.CordaX500Name
import net.corda.testing.core.TestIdentity
import net.corda.testing.node.MockServices
import net.corda.testing.node.ledger
import org.junit.Test

class ContractsTests {
    private val issuerA = TestIdentity(CordaX500Name("Issuer A", "London", "GB"))
    private val clientA = TestIdentity(CordaX500Name("Client A", "London", "GB"))
    private val clientB = TestIdentity(CordaX500Name("Client B", "London", "GB"))
    private val ledgerServices = MockServices(listOf("net.corda.contractsdk.testappcontracts"), issuerA, clientA, clientB)

    @Test
    fun `Membership can be requested`() {
        ledgerServices.ledger {
            transaction {
                output(MembershipContract.ID, LastingMembershipState(owner = clientA.party, issuer = issuerA.party, linearId = UniqueIdentifier(), status = Status.PENDING))
                command(listOf(issuerA.publicKey, clientA.publicKey), MembershipContract.Commands.Request())
                verifies()
            }
            transaction {
                output(MembershipContract.ID, OneUseMembershipState(owner = clientA.party, issuer = issuerA.party, linearId = UniqueIdentifier(), status = Status.PENDING))
                command(listOf(issuerA.publicKey, clientA.publicKey), MembershipContract.Commands.Request())
                verifies()
            }
            transaction {
                output(MembershipContract.ID, LastingMembershipState(owner = clientA.party, issuer = issuerA.party, linearId = UniqueIdentifier(), status = Status.PENDING))
                output(MembershipContract.ID, OneUseMembershipState(owner = clientA.party, issuer = issuerA.party, linearId = UniqueIdentifier(), status = Status.PENDING))
                command(listOf(issuerA.publicKey, clientA.publicKey), MembershipContract.Commands.Request())
                verifies()
            }
            transaction {
                output(MembershipContract.ID, LastingMembershipState(owner = clientA.party, issuer = clientA.party, linearId = UniqueIdentifier(), status = Status.PENDING))
                command(listOf(issuerA.publicKey, clientA.publicKey), MembershipContract.Commands.Request())
                failsWith("Each of these roles must be a different party on output, [issuer, owner].")
            }
            transaction {
                input(MembershipContract.ID, LastingMembershipState(owner = clientA.party, issuer = issuerA.party, linearId = UniqueIdentifier(), status = Status.PENDING))
                output(MembershipContract.ID, LastingMembershipState(owner = clientA.party, issuer = issuerA.party, linearId = UniqueIdentifier(), status = Status.PENDING))
                command(listOf(issuerA.publicKey, clientA.publicKey), MembershipContract.Commands.Request())
                failsWith("There cannot be any input state(s)")
            }
            transaction {
                output(MembershipContract.ID, LastingMembershipState(owner = clientA.party, issuer = issuerA.party, linearId = UniqueIdentifier(), status = Status.PENDING))
                command(clientA.publicKey, MembershipContract.Commands.Request())
                failsWith("Each of these roles on the output must sign the transaction: [owner, issuer]. These are missing: [issuer].")
            }
            transaction {
                output(MembershipContract.ID, LastingMembershipState(owner = clientA.party, issuer = issuerA.party, linearId = UniqueIdentifier(), status = Status.PENDING))
                command(issuerA.publicKey, MembershipContract.Commands.Request())
                failsWith("Each of these roles on the output must sign the transaction: [owner, issuer]. These are missing: [owner].")
            }
            transaction {
                output(MembershipContract.ID, LastingMembershipState(owner = clientA.party, issuer = issuerA.party, linearId = UniqueIdentifier(), status = Status.ACTIVE))
                command(listOf(issuerA.publicKey, clientA.publicKey), MembershipContract.Commands.Request())
                failsWith("The output state must be in status PENDING")
            }
        }
    }

    @Test
    fun `Membership can be activated`() {
        val linearId1 = UniqueIdentifier()
        ledgerServices.ledger {
            transaction {
                input(MembershipContract.ID, LastingMembershipState(owner = clientA.party, issuer = issuerA.party, linearId = linearId1, status = Status.PENDING))
                output(MembershipContract.ID, LastingMembershipState(owner = clientA.party, issuer = issuerA.party, linearId = linearId1, status = Status.ACTIVE))
                command(issuerA.publicKey, MembershipContract.Commands.Activate())
                verifies()
            }
            transaction {
                input(MembershipContract.ID, OneUseMembershipState(owner = clientA.party, issuer = issuerA.party, linearId = linearId1, status = Status.PENDING))
                output(MembershipContract.ID, OneUseMembershipState(owner = clientA.party, issuer = issuerA.party, linearId = linearId1, status = Status.ACTIVE))
                command(issuerA.publicKey, MembershipContract.Commands.Activate())
                verifies()
            }
            transaction {
                input(MembershipContract.ID, LastingMembershipState(owner = clientA.party, issuer = issuerA.party, linearId = linearId1, status = Status.PENDING))
                output(MembershipContract.ID, LastingMembershipState(owner = clientA.party, issuer = issuerA.party, linearId = linearId1, status = Status.ACTIVE))
                command(clientA.publicKey, MembershipContract.Commands.Activate())
                failsWith("The issuer from the input must sign the transaction.")
            }
            transaction {
                input(MembershipContract.ID, LastingMembershipState(owner = clientA.party, issuer = issuerA.party, linearId = linearId1, status = Status.PENDING))
                output(MembershipContract.ID, LastingMembershipState(owner = clientB.party, issuer = issuerA.party, linearId = linearId1, status = Status.ACTIVE))
                command(issuerA.publicKey, MembershipContract.Commands.Activate())
                failsWith("Property 'participants' is not allowed to change between input and output")
            }
        }
    }

    @Test
    fun `Lasting membership can be revoked`() {
        val linearId1 = UniqueIdentifier()
        ledgerServices.ledger {
            transaction {
                input(MembershipContract.ID, LastingMembershipState(owner = clientA.party, issuer = issuerA.party, linearId = linearId1, status = Status.ACTIVE))
                command(issuerA.publicKey, MembershipContract.Commands.Revoke())
                verifies()
            }
            transaction {
                input(MembershipContract.ID, OneUseMembershipState(owner = clientA.party, issuer = issuerA.party, linearId = linearId1, status = Status.ACTIVE))
                command(issuerA.publicKey, MembershipContract.Commands.Revoke())
                failsWith("There cannot be any input state(s) of type(s) [OneUseMembershipState]")
            }
            transaction {
                input(MembershipContract.ID, LastingMembershipState(owner = clientA.party, issuer = issuerA.party, linearId = linearId1, status = Status.ACTIVE))
                command(clientB.publicKey, MembershipContract.Commands.Revoke())
                failsWith("The issuer from the input must sign the transaction.")
            }
            transaction {
                input(MembershipContract.ID, LastingMembershipState(owner = clientA.party, issuer = issuerA.party, linearId = linearId1, status = Status.ACTIVE))
                output(MembershipContract.ID, LastingMembershipState(owner = clientA.party, issuer = issuerA.party, linearId = linearId1, status = Status.ACTIVE))
                command(issuerA.publicKey, MembershipContract.Commands.Revoke())
                failsWith("There cannot be any output state(s)")
            }
            transaction {
                input(MembershipContract.ID, LastingMembershipState(owner = clientA.party, issuer = issuerA.party, linearId = linearId1, status = Status.PENDING))
                command(issuerA.publicKey, MembershipContract.Commands.Revoke())
                failsWith("The input state must be in status ACTIVE")
            }
        }
    }

    @Test
    fun `One use membership can be used`() {
        val linearId1 = UniqueIdentifier()
        ledgerServices.ledger {
            transaction {
                input(MembershipContract.ID, OneUseMembershipState(owner = clientA.party, issuer = issuerA.party, linearId = linearId1, status = Status.ACTIVE))
                command(clientA.publicKey, MembershipContract.Commands.Use())
                verifies()
            }
            transaction {
                input(MembershipContract.ID, LastingMembershipState(owner = clientA.party, issuer = issuerA.party, linearId = linearId1, status = Status.ACTIVE))
                command(issuerA.publicKey, MembershipContract.Commands.Use())
                failsWith("There cannot be any input state(s) of type(s) [LastingMembershipState]")
            }
            transaction {
                input(MembershipContract.ID, OneUseMembershipState(owner = clientA.party, issuer = issuerA.party, linearId = linearId1, status = Status.ACTIVE))
                command(issuerA.publicKey, MembershipContract.Commands.Use())
                failsWith("The owner from the input must sign the transaction.")
            }
            transaction {
                input(MembershipContract.ID, OneUseMembershipState(owner = clientA.party, issuer = issuerA.party, linearId = linearId1, status = Status.ACTIVE))
                output(MembershipContract.ID, OneUseMembershipState(owner = clientA.party, issuer = issuerA.party, linearId = linearId1, status = Status.ACTIVE))
                command(issuerA.publicKey, MembershipContract.Commands.Use())
                failsWith("There cannot be any output state(s)")
            }
            transaction {
                input(MembershipContract.ID, OneUseMembershipState(owner = clientA.party, issuer = issuerA.party, linearId = linearId1, status = Status.PENDING))
                command(issuerA.publicKey, MembershipContract.Commands.Use())
                failsWith("The input state must be in status ACTIVE")
            }
        }
    }

}