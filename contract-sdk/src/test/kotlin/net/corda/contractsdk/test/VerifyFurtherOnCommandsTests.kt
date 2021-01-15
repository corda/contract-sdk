package net.corda.contractsdk.test

import net.corda.contractsdk.test.statesandcontracts.*
import net.corda.core.identity.CordaX500Name
import net.corda.testing.core.TestIdentity
import net.corda.testing.node.MockServices
import net.corda.testing.node.ledger
import org.junit.Test

class VerifyFurtherOnCommandsTests {
    private val companyA = TestIdentity(CordaX500Name("Company A", "London", "GB"))
    private val companyB = TestIdentity(CordaX500Name("Company B", "New York", "US"))
    private val ledgerServices = MockServices(listOf("net.corda.contractsdk.test.statesandcontracts"), companyA, companyB)

    @Test
    fun `VerifyFurther verification on commands gets called`() {
        ledgerServices.ledger {
            transaction {
                output(TestContract5.ID, TestContract5.getTestState(x = companyA.party))
                command(companyA.publicKey, TestContract5.Commands.CommandA())
                failsWith("X cannot be located in London")
            }
        }
        ledgerServices.ledger {
            transaction {
                output(TestContract5.ID, TestContract5.getTestState(x = companyB.party))
                command(companyA.publicKey, TestContract5.Commands.CommandA())
                verifies()
            }
        }
    }

}