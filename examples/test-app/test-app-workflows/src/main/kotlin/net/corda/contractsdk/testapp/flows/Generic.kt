package net.corda.contractsdk.testapp.flows

import co.paralleluniverse.fibers.Suspendable
import net.corda.contractsdk.ContractSDKJarMarker
import net.corda.contractsdk.testappcontracts.*
import net.corda.core.contracts.ContractState
import net.corda.core.contracts.LinearState
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.flows.*
import net.corda.core.identity.Party
import net.corda.core.node.services.queryBy
import net.corda.core.node.services.vault.QueryCriteria
import net.corda.core.serialization.CordaSerializable
import net.corda.core.transactions.SignedTransaction
import net.corda.core.transactions.TransactionBuilder
import net.corda.core.utilities.ProgressTracker
import java.lang.RuntimeException
import java.util.*

@CordaSerializable
enum class MembershipType {
    LASTING,
    ONE_USE
}

@InitiatingFlow
@StartableByRPC
class IssueInitiator(val membershipType: MembershipType, val issuer: Party, val owner: Party, val status: Status) : FlowLogic<String>() {

    override val progressTracker = ProgressTracker()

    @Suspendable
    override fun call(): String {
        val notary = serviceHub.networkMapCache.notaryIdentities.first()

        val txBuilder = TransactionBuilder(notary = notary)
        txBuilder.addCommand(MembershipContract.Commands.Request(), issuer.owningKey, owner.owningKey)
        val identifier = UniqueIdentifier()
        when (membershipType) {
            MembershipType.LASTING -> txBuilder.addOutputState(LastingMembershipState(owner, issuer, status, identifier))
            MembershipType.ONE_USE -> txBuilder.addOutputState(OneUseMembershipState(owner, issuer, status, identifier))
        }
        //unless we fat-jarred the Contract SDK jar into the contracts jar we need to attach the Contract SDK jar manually. Corda won't do this automatically.
        val contractSdkAttachment = serviceHub.attachments.getLatestContractAttachments(ContractSDKJarMarker::class.qualifiedName!!).single()
        txBuilder.addAttachment(contractSdkAttachment)

        val locallySigned = serviceHub.signInitialTransaction(txBuilder)
        val sessions = (listOf(issuer, owner) - ourIdentity).map { initiateFlow(it) }
        val fullySigned = subFlow(CollectSignaturesFlow(locallySigned, sessions))

        subFlow(FinalityFlow(fullySigned, sessions))
        return identifier.externalId!!
    }
}

@InitiatedBy(IssueInitiator::class)
class IssueResponder(val session: FlowSession) : FlowLogic<SignedTransaction>() {

    @Suspendable
    override fun call(): SignedTransaction {

        val flow = object : SignTransactionFlow(session) {
            @Suspendable
            override fun checkTransaction(stx: SignedTransaction) {
                //do nothing here
            }
        }

        val expectedTxId = subFlow(flow).id
        return subFlow(ReceiveFinalityFlow(session, expectedTxId))
    }
}

@InitiatingFlow
@StartableByRPC
class ModifyInitiator(val identifier : String, val command : String, val status: Status) : FlowLogic<SignedTransaction>() {

    override val progressTracker = ProgressTracker()

    @Suspendable
    override fun call(): SignedTransaction {
        val inputState = serviceHub.vaultService.queryBy<LinearState>(QueryCriteria.LinearStateQueryCriteria(uuid = listOf(UUID.fromString(identifier)))).states.single()
        val inputStateData = inputState.state.data as Membership
        val inputStateOwner = inputStateData.owner

        val notary = serviceHub.networkMapCache.notaryIdentities.first()
        val txBuilder = TransactionBuilder(notary = notary)
        when (command.toUpperCase()) {
            "ACTIVATE" -> txBuilder.addCommand(MembershipContract.Commands.Activate(), listOf(ourIdentity.owningKey))
            else -> throw RuntimeException("Unrecognized command '$command'")
        }
        txBuilder.addInputState(inputState)
        txBuilder.addOutputState(inputStateData.withStatus(status) as ContractState)

        //unless we fat-jarred the Contract SDK jar into the contracts jar we need to attach the Contract SDK jar manually. Corda won't do this automatically.
        val contractSdkAttachment = serviceHub.attachments.getLatestContractAttachments(ContractSDKJarMarker::class.qualifiedName!!).single()
        txBuilder.addAttachment(contractSdkAttachment)

        val locallySigned = serviceHub.signInitialTransaction(txBuilder)
        return subFlow(FinalityFlow(locallySigned, listOf(initiateFlow(inputStateOwner))))
    }
}

@InitiatedBy(ModifyInitiator::class)
class ModifyResponder(val session: FlowSession) : FlowLogic<SignedTransaction>() {

    @Suspendable
    override fun call(): SignedTransaction {
        return subFlow(ReceiveFinalityFlow(session))
    }
}

@InitiatingFlow
@StartableByRPC
class DeissueInitiator(val identifier : String, val command : String) : FlowLogic<SignedTransaction>() {

    override val progressTracker = ProgressTracker()

    @Suspendable
    override fun call(): SignedTransaction {
        val inputState = serviceHub.vaultService.queryBy<LinearState>(QueryCriteria.LinearStateQueryCriteria(uuid = listOf(UUID.fromString(identifier)))).states.single()
        val inputStateData = inputState.state.data as Membership

        val notary = serviceHub.networkMapCache.notaryIdentities.first()
        val txBuilder = TransactionBuilder(notary = notary)
        when (command.toUpperCase()) {
            "REVOKE" -> txBuilder.addCommand(MembershipContract.Commands.Revoke(), listOf(ourIdentity.owningKey))
            "USE" -> txBuilder.addCommand(MembershipContract.Commands.Use(), listOf(ourIdentity.owningKey))
            else -> throw RuntimeException("Unrecognized command '$command'")
        }
        txBuilder.addInputState(inputState)

        //unless we fat-jarred the Contract SDK jar into the contracts jar we need to attach the Contract SDK jar manually. Corda won't do this automatically.
        val contractSdkAttachment = serviceHub.attachments.getLatestContractAttachments(ContractSDKJarMarker::class.qualifiedName!!).single()
        txBuilder.addAttachment(contractSdkAttachment)

        val locallySigned = serviceHub.signInitialTransaction(txBuilder)
        val sessionWithTheOtherParticipants = ((inputStateData as ContractState).participants - ourIdentity).map { initiateFlow(it) }

        return subFlow(FinalityFlow(locallySigned, sessionWithTheOtherParticipants))
    }
}

@InitiatedBy(DeissueInitiator::class)
class DeissueResponder(val session: FlowSession) : FlowLogic<SignedTransaction>() {

    @Suspendable
    override fun call(): SignedTransaction {
        return subFlow(ReceiveFinalityFlow(session))
    }
}