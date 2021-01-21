package com.r3.corda.lib.contracts.contractsdk.annotations

import net.corda.core.contracts.ContractState
import kotlin.reflect.KClass

annotation class RequireNumberOfStatesOnInput(val value : Int, val targetClasses : Array<KClass<out ContractState>> = [])
annotation class RequireNumberOfStatesOnInputAtLeast(val value : Int, val targetClasses : Array<KClass<out ContractState>> = [])
annotation class RequireNumberOfStatesOnInputAtMost(val value : Int, val targetClasses : Array<KClass<out ContractState>> = [])
annotation class RequireNumberOfStatesOnInputBetween(val min : Int, val max : Int, val targetClasses : Array<KClass<out ContractState>> = [])

annotation class RequireNumberOfStatesOnOutput(val value : Int, val targetClasses : Array<KClass<out ContractState>> = [])
annotation class RequireNumberOfStatesOnOutputAtLeast(val value : Int, val targetClasses : Array<KClass<out ContractState>> = [])
annotation class RequireNumberOfStatesOnOutputAtMost(val value : Int, val targetClasses : Array<KClass<out ContractState>> = [])
annotation class RequireNumberOfStatesOnOutputBetween(val min : Int, val max : Int, val targetClasses : Array<KClass<out ContractState>> = [])

annotation class PermitStatusOnInput(val value : String, val targetClasses : Array<KClass<out ContractState>> = [])
annotation class PermitStatusesOnInput(vararg val statuses : String, val targetClasses : Array<KClass<out ContractState>> = [])
annotation class PermitStatusOnOutput(val value : String, val targetClasses : Array<KClass<out ContractState>> = [])
annotation class PermitStatusesOnOutput(vararg val statuses: String, val targetClasses : Array<KClass<out ContractState>> = [])

/* Takes list of roles as parameter.
   Each input state is expected to implement interface StateWithRoles.
   During verification of the transaction each role is translated into a party (or parties) using the method from the StateWithRoles interface.
   And each such party must be a signer on the transaction
 */
annotation class RequireSignersFromEachInputState(vararg val roles : String, val targetClasses : Array<KClass<out ContractState>> = [])

/* Takes list of roles as parameter.
   Each output state is expected to implement interface StateWithRoles.
   During verification of the transaction each role is translated into a party (or parties) using the method from the StateWithRoles interface.
   And each such party must be a signer on the transaction
 */
annotation class RequireSignersFromEachOutputState(vararg val roles : String, val targetClasses : Array<KClass<out ContractState>> = [])

annotation class RequireStatusChangeInCoupledLinearStates(val statusOnInput : String, val statusOnOutput : String, val targetClasses : Array<KClass<out ContractState>> = [])

annotation class ForbidChangeInCoupledLinearStatesExcept(vararg val exemptProperties : String, val targetClasses : Array<KClass<out ContractState>> = [])

annotation class RequireDistinctPartiesWithinEachInputState(vararg val roles : String, val targetClasses : Array<KClass<out ContractState>> = [])

annotation class RequireDistinctPartiesWithinEachOutputState(vararg val roles : String, val targetClasses : Array<KClass<out ContractState>> = [])

annotation class RequirePartyToAssumeAtLeastOneOtherRoleWithinEachInputState(val role : String, vararg val otherRoles : String, val targetClasses : Array<KClass<out ContractState>> = [])
annotation class RequirePartyToAssumeAtLeastOneOtherRoleWithinEachOutputState(val role : String, vararg val otherRoles : String, val targetClasses : Array<KClass<out ContractState>> = [])

annotation class RequirePropertySetOnInput(val value : String, val targetClasses : Array<KClass<out ContractState>> = [])
annotation class RequirePropertiesSetOnInput(vararg val properties : String, val targetClasses : Array<KClass<out ContractState>> = [])
annotation class RequirePropertySetOnOutput(val value : String, val targetClasses : Array<KClass<out ContractState>> = [])
annotation class RequirePropertiesSetOnOutput(vararg val properties : String, val targetClasses : Array<KClass<out ContractState>> = [])

annotation class RequirePropertyNotSetOnInput(val value : String, val targetClasses : Array<KClass<out ContractState>> = [])
annotation class RequirePropertiesNotSetOnInput(vararg val properties : String, val targetClasses : Array<KClass<out ContractState>> = [])
annotation class RequirePropertyNotSetOnOutput(val value : String, val targetClasses : Array<KClass<out ContractState>> = [])
annotation class RequirePropertiesNotSetOnOutput(vararg val properties : String, val targetClasses : Array<KClass<out ContractState>> = [])

annotation class RequirePartyToAssumeAtLeastOneOtherRoleWithinEachInputStateList(vararg val rules : RequirePartyToAssumeAtLeastOneOtherRoleWithinEachInputState)
annotation class RequirePartyToAssumeAtLeastOneOtherRoleWithinEachOutputStateList(vararg val rules : RequirePartyToAssumeAtLeastOneOtherRoleWithinEachOutputState)

annotation class RequireDistinctPartiesWithinEachInputStateList(vararg val rules : RequireDistinctPartiesWithinEachInputState)
annotation class RequireDistinctPartiesWithinEachOutputStateList(vararg val rules : RequireDistinctPartiesWithinEachOutputState)