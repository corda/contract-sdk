package net.corda.contractsdk.testapp

import net.corda.contractsdk.ContractSDKJarMarker
import net.corda.core.node.ServiceHub
import net.corda.core.transactions.TransactionBuilder

fun TransactionBuilder.attachContractSDKJar(serviceHub : ServiceHub) {
    val contractSdkAttachment = serviceHub.attachments.getLatestContractAttachments(ContractSDKJarMarker::class.qualifiedName!!).single()
    addAttachment(contractSdkAttachment)
}