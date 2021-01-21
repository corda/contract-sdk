package com.r3.corda.lib.contracts.contractsdk

import com.r3.corda.lib.contracts.contractsdk.verifiers.StandardCommand
import com.r3.corda.lib.contracts.contractsdk.verifiers.StandardVerifier
import net.corda.core.contracts.*
import net.corda.core.transactions.LedgerTransaction
import kotlin.reflect.KClass
import kotlin.reflect.full.allSuperclasses


//@todo ask how many contract commands are allowed
abstract class StandardContract(private val commandsClass: Class<out CommandData>? = null) : Contract {

    override fun verify(tx : LedgerTransaction) {

        val commandsInScope = commandsClass ?: getCommandsClass()
        val command = tx.commands.requireSingleCommand(commandsInScope)
        val commandValue = command.value
        val verifiers = getVerifiers(this::class) + getVerifiers(commandValue)
        verifiers.forEach { it.verify(this::class, tx, command.signers) }

        verifyFurther(tx)
        if (commandValue is StandardCommand) {
            commandValue.verifyFurther(tx)
        }
    }

    //@todo, the toSet here probably has no effect
    private fun getVerifiers(contract: KClass<out Contract>) : Set<StandardVerifier> = contract.annotations.flatMap { VerifiersFactory.getVerifiers(it) }.toSet()
    //@todo, the toSet here probably has no effect
    private fun getVerifiers(command : CommandData) : Set<StandardVerifier> = command::class.annotations.flatMap { VerifiersFactory.getVerifiers(it) }.toSet()

    private fun getCommandsClass() : Class<out CommandData> {
        val allNestedClasses = this::class.nestedClasses
        val nestedClassesInheritingFromCommandData = allNestedClasses.filter { it.allSuperclasses.contains(CommandData::class) }
        @Suppress("UNCHECKED_CAST")
        return nestedClassesInheritingFromCommandData.single().java as Class<out CommandData>
    }

    open fun verifyFurther(tx : LedgerTransaction) { }
}

