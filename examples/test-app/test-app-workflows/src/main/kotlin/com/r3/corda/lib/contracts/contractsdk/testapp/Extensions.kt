package com.r3.corda.lib.contracts.contractsdk.testapp

import com.r3.corda.lib.contracts.contractsdk.ContractSDKJarMarker
import net.corda.core.node.ServiceHub
import net.corda.core.transactions.TransactionBuilder

fun TransactionBuilder.attachContractSDKJar(serviceHub : ServiceHub) {
    val contractSdkAttachment = serviceHub.attachments.getLatestContractAttachments(ContractSDKJarMarker::class.qualifiedName!!).single()
    addAttachment(contractSdkAttachment)
}