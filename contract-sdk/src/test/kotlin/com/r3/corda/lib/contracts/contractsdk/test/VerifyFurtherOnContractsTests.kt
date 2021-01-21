package com.r3.corda.lib.contracts.contractsdk.test

import com.r3.corda.lib.contracts.contractsdk.test.statesandcontracts.*
import net.corda.core.identity.CordaX500Name
import net.corda.testing.core.TestIdentity
import net.corda.testing.node.MockServices
import net.corda.testing.node.ledger
import org.junit.Test

class VerifyFurtherOnContractsTests {
    private val companyA = TestIdentity(CordaX500Name("Company A", "London", "GB"))
    private val companyB = TestIdentity(CordaX500Name("Company B", "New York", "US"))
    private val ledgerServices = MockServices(listOf("com.r3.corda.lib.contracts.contractsdk.test.statesandcontracts"), companyA, companyB)

    @Test
    fun `VerifyFurther verification on contracts gets called`() {
        ledgerServices.ledger {
            transaction {
                output(TestContract4.ID, TestContract4.getTestState(x = companyA.party))
                command(companyA.publicKey, TestContract4.Commands.CommandA())
                failsWith("X cannot be located in London")
            }
        }
        ledgerServices.ledger {
            transaction {
                output(TestContract4.ID, TestContract4.getTestState(x = companyB.party))
                command(companyA.publicKey, TestContract4.Commands.CommandA())
                verifies()
            }
        }
    }

}