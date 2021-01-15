package net.corda.contractsdk.test

import net.corda.contractsdk.test.statesandcontracts.*
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.identity.CordaX500Name
import net.corda.core.identity.Party
import net.corda.testing.core.TestIdentity
import net.corda.testing.node.MockServices
import net.corda.testing.node.ledger
import org.junit.Test

class AnnotationsOnContractsTests {
    private val companyA = TestIdentity(CordaX500Name("Company A", "London", "GB"))
    private val companyB = TestIdentity(CordaX500Name("Company B", "London", "GB"))
    private val companyC = TestIdentity(CordaX500Name("Company C", "London", "GB"))
    private val companyD = TestIdentity(CordaX500Name("Company D", "London", "GB"))
    private val ledgerServices = MockServices(listOf("net.corda.contractsdk.test.statesandcontracts"), companyA, companyB, companyC, companyD)

    @Test
    fun `Annotation on contract is recognized and used`() {
        ledgerServices.ledger {
            transaction {
                input(TestContract3.ID, TestContract3.getTestState(x = companyA.party, y = companyB.party))
                output(TestContract3.ID, TestContract3.getTestState(x = companyA.party, y = companyB.party))
                command(companyA.publicKey, TestContract3.Commands.CommandA())
                verifies()
            }
            transaction {
                input(TestContract3.ID, TestContract3.getTestState(x = null, y = companyB.party))
                output(TestContract3.ID, TestContract3.getTestState(x = companyA.party, y = companyB.party))
                command(companyA.publicKey, TestContract3.Commands.CommandA())
                failsWith("Property 'x' cannot be null on the input.")
            }
            transaction {
                input(TestContract3.ID, TestContract3.getTestState(x = companyA.party, y = companyB.party))
                output(TestContract3.ID, TestContract3.getTestState(x = null, y = companyB.party))
                command(companyA.publicKey, TestContract3.Commands.CommandA())
                failsWith("Property 'x' cannot be null on the output.")
            }
        }
    }

}