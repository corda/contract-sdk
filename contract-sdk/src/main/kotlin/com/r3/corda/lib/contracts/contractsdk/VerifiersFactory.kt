package com.r3.corda.lib.contracts.contractsdk

import net.corda.core.internal.packageName
import com.r3.corda.lib.contracts.contractsdk.annotations.*
import com.r3.corda.lib.contracts.contractsdk.verifiers.*
import net.corda.core.contracts.ContractState
import java.lang.RuntimeException
import kotlin.reflect.KClass

object VerifiersFactory {

    fun getVerifiers(annotation: Annotation) = when (annotation) {
        is RequirePartyToAssumeAtLeastOneOtherRoleWithinEachInputStateList -> annotation.rules.mapNotNull { getVerifier(it) }
        is RequirePartyToAssumeAtLeastOneOtherRoleWithinEachOutputStateList -> annotation.rules.mapNotNull { getVerifier(it) }
        is RequireDistinctPartiesWithinEachInputStateList -> annotation.rules.mapNotNull { getVerifier(it) }
        is RequireDistinctPartiesWithinEachOutputStateList -> annotation.rules.mapNotNull { getVerifier(it) }
        else -> listOf(getVerifier(annotation)).filterNotNull()
    }

    fun getVerifier(annotation : Annotation) : StandardVerifier? = when (annotation) {
        is RequireNumberOfStatesOnInput -> BoundedNumberOfStatesVerifier(annotation.value, annotation.value, VerificationScope.INPUT_STATES, annotation.targetClasses.toNullOrSet())
        is RequireNumberOfStatesOnInputBetween -> BoundedNumberOfStatesVerifier(annotation.min, annotation.max, VerificationScope.INPUT_STATES, annotation.targetClasses.toNullOrSet())
        is RequireNumberOfStatesOnInputAtLeast -> BoundedNumberOfStatesVerifier(annotation.value, null, VerificationScope.INPUT_STATES, annotation.targetClasses.toNullOrSet())
        is RequireNumberOfStatesOnInputAtMost -> BoundedNumberOfStatesVerifier(null, annotation.value, VerificationScope.INPUT_STATES, annotation.targetClasses.toNullOrSet())

        is RequireNumberOfStatesOnOutput -> BoundedNumberOfStatesVerifier(annotation.value, annotation.value, VerificationScope.OUTPUT_STATES, annotation.targetClasses.toNullOrSet())
        is RequireNumberOfStatesOnOutputBetween -> BoundedNumberOfStatesVerifier(annotation.min, annotation.max, VerificationScope.OUTPUT_STATES, annotation.targetClasses.toNullOrSet())
        is RequireNumberOfStatesOnOutputAtLeast -> BoundedNumberOfStatesVerifier(annotation.value, null, VerificationScope.OUTPUT_STATES, annotation.targetClasses.toNullOrSet())
        is RequireNumberOfStatesOnOutputAtMost -> BoundedNumberOfStatesVerifier(null, annotation.value, VerificationScope.OUTPUT_STATES, annotation.targetClasses.toNullOrSet())

        is PermitStatusesOnInput -> AllowedStatusesVerifier(annotation.statuses.toSet(), VerificationScope.INPUT_STATES, annotation.targetClasses.toNullOrSet())
        is PermitStatusOnInput -> AllowedStatusesVerifier(setOf(annotation.value), VerificationScope.INPUT_STATES, annotation.targetClasses.toNullOrSet())

        is PermitStatusesOnOutput -> AllowedStatusesVerifier(annotation.statuses.toSet() , VerificationScope.OUTPUT_STATES, annotation.targetClasses.toNullOrSet())
        is PermitStatusOnOutput -> AllowedStatusesVerifier(setOf(annotation.value), VerificationScope.OUTPUT_STATES, annotation.targetClasses.toNullOrSet())

        is RequireSignersFromEachInputState -> RequiredSignersVerifier(annotation.roles.toSet(), VerificationScope.INPUT_STATES, annotation.targetClasses.toNullOrSet())

        is RequireSignersFromEachOutputState -> RequiredSignersVerifier(annotation.roles.toSet(), VerificationScope.OUTPUT_STATES, annotation.targetClasses.toNullOrSet())

        is RequireStatusChangeInCoupledLinearStates -> RequireStatusChangeInCoupledLinearStatesVerifier(annotation.statusOnInput, annotation.statusOnOutput, annotation.targetClasses.toNullOrSet())

        is ForbidChangeInCoupledLinearStatesExcept -> ForbidChangeInCoupledLinearStatesExceptVerifier(annotation.exemptProperties.toSet(), annotation.targetClasses.toNullOrSet())

        is RequireDistinctPartiesWithinEachInputState -> MustBeDistinctPartiesVerifier(annotation.roles.toSet(), VerificationScope.INPUT_STATES, annotation.targetClasses.toNullOrSet())

        is RequireDistinctPartiesWithinEachOutputState -> MustBeDistinctPartiesVerifier(annotation.roles.toSet(), VerificationScope.OUTPUT_STATES, annotation.targetClasses.toNullOrSet())

        is RequirePartyToAssumeAtLeastOneOtherRoleWithinEachInputState -> MustAssumeAtLeastOneOtherRoleVerifier(annotation.role, annotation.otherRoles.toSet(), VerificationScope.INPUT_STATES, annotation.targetClasses.toNullOrSet())
        is RequirePartyToAssumeAtLeastOneOtherRoleWithinEachOutputState -> MustAssumeAtLeastOneOtherRoleVerifier(annotation.role, annotation.otherRoles.toSet(), VerificationScope.OUTPUT_STATES, annotation.targetClasses.toNullOrSet())

        is RequirePropertySetOnInput -> PropertiesMustBeSetVerifier(setOf(annotation.value), VerificationScope.INPUT_STATES, annotation.targetClasses.toNullOrSet())
        is RequirePropertiesSetOnInput -> PropertiesMustBeSetVerifier(annotation.properties.toSet(), VerificationScope.INPUT_STATES, annotation.targetClasses.toNullOrSet())

        is RequirePropertySetOnOutput -> PropertiesMustBeSetVerifier(setOf(annotation.value), VerificationScope.OUTPUT_STATES, annotation.targetClasses.toNullOrSet())
        is RequirePropertiesSetOnOutput -> PropertiesMustBeSetVerifier(annotation.properties.toSet(), VerificationScope.OUTPUT_STATES, annotation.targetClasses.toNullOrSet())

        is RequirePropertyNotSetOnInput -> PropertiesMustNotBeSetVerifier(setOf(annotation.value), VerificationScope.INPUT_STATES, annotation.targetClasses.toNullOrSet())
        is RequirePropertiesNotSetOnInput -> PropertiesMustNotBeSetVerifier(annotation.properties.toSet(), VerificationScope.INPUT_STATES, annotation.targetClasses.toNullOrSet())

        is RequirePropertyNotSetOnOutput -> PropertiesMustNotBeSetVerifier(setOf(annotation.value), VerificationScope.OUTPUT_STATES, annotation.targetClasses.toNullOrSet())
        is RequirePropertiesNotSetOnOutput -> PropertiesMustNotBeSetVerifier(annotation.properties.toSet(), VerificationScope.OUTPUT_STATES, annotation.targetClasses.toNullOrSet())

        else -> {
            checkWeCanIgnoreThisAnnotation(annotation)
            null
        }
    }

    fun checkWeCanIgnoreThisAnnotation(annotation: Annotation) {
        if (annotation.annotationClass.packageName == "com.r3.corda.lib.contracts.contractsdk.annotations") {
            throw RuntimeException("The annotation $annotation belongs among the standard contract annotations but is not recognized in the VerifiersFactory")
        }
    }

    fun Array<KClass<out ContractState>>.toNullOrSet() = if (this.isEmpty()) { null } else { this.toSet() }

}