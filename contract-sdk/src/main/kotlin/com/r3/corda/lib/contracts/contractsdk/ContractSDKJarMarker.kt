package com.r3.corda.lib.contracts.contractsdk

import net.corda.core.contracts.Contract
import net.corda.core.transactions.LedgerTransaction
import java.lang.RuntimeException

//This contract only exists to get by the limitations of Corda and to make the Contract SDK jar easy to find among the attachments
class ContractSDKJarMarker : Contract {

    override fun verify(tx: LedgerTransaction) {
        throw RuntimeException("This contract should never be used")
    }

}