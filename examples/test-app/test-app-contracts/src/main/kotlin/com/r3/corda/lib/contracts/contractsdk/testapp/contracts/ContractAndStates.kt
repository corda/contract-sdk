package com.r3.corda.lib.contracts.contractsdk.testapp.contracts

import com.r3.corda.lib.contracts.contractsdk.StandardContract
import com.r3.corda.lib.contracts.contractsdk.annotations.*
import com.r3.corda.lib.contracts.contractsdk.verifiers.StandardState
import net.corda.core.contracts.*
import net.corda.core.identity.AbstractParty
import net.corda.core.identity.Party
import net.corda.core.serialization.CordaSerializable
import java.lang.RuntimeException


@RequireDistinctPartiesWithinEachInputState("issuer", "owner")
@RequireDistinctPartiesWithinEachOutputState("issuer", "owner")
class MembershipContract : StandardContract(), Contract {

    interface Commands : CommandData {
        @RequireNumberOfStatesOnInput(0)
        @RequireNumberOfStatesOnOutputAtLeast(1)
        @RequireSignersFromEachOutputState("owner", "issuer")
        @PermitStatusOnOutput("PENDING")
        class Request : Commands

        @RequireStatusChangeInCoupledLinearStates("PENDING", "ACTIVE")
        @RequireSignersFromEachInputState("issuer")
        @ForbidChangeInCoupledLinearStatesExcept("status")
        class Activate : Commands

        @RequireNumberOfStatesOnInput(0, targetClasses = [OneUseMembershipState::class]) //LastingMembership cannot be revoked
        @RequireNumberOfStatesOnInputAtLeast(1, targetClasses = [LastingMembershipState::class])
        @PermitStatusOnInput("ACTIVE")
        @RequireNumberOfStatesOnOutput(0)
        @RequireSignersFromEachInputState("issuer")
        class Revoke : Commands

        @RequireNumberOfStatesOnInput(0, targetClasses = [LastingMembershipState::class])
        @RequireNumberOfStatesOnInputAtLeast(1, targetClasses = [OneUseMembershipState::class])
        @PermitStatusOnInput("ACTIVE")
        @RequireNumberOfStatesOnOutput(0)
        @RequireSignersFromEachInputState("owner")
        class Use : Commands
    }

    companion object {
        val ID = "com.r3.corda.lib.contracts.contractsdk.testapp.contracts.MembershipContract"
    }

}

abstract class Membership(val owner: Party, val issuer: Party, val status : Status) : StandardState {
    override fun getParty(role: String): Party {
        return when (role.toUpperCase()) {
            "ISSUER" -> issuer
            "OWNER" -> owner
            else -> throw RuntimeException("Unrecognized role '$role'")
        }
    }

    override fun isInStatus(status: String) = this.status == Status.valueOf(status)
    abstract fun withStatus(status : Status) : Membership
}

@BelongsToContract(MembershipContract::class)
class LastingMembershipState(owner: Party, issuer: Party, status : Status, override val linearId: UniqueIdentifier, override val participants: List<AbstractParty> = listOf(owner, issuer)) : Membership(owner, issuer, status), LinearState {
    override fun withStatus(status: Status) = LastingMembershipState(owner, issuer, status, linearId, participants)
}

@BelongsToContract(MembershipContract::class)
class OneUseMembershipState(owner: Party, issuer: Party, status : Status, override val linearId: UniqueIdentifier, override val participants: List<AbstractParty> = listOf(owner, issuer)) : Membership(owner, issuer, status), LinearState {
    override fun withStatus(status: Status) = OneUseMembershipState(owner, issuer, status, linearId, participants)
}

@CordaSerializable
enum class Status {
    PENDING,
    ACTIVE
}