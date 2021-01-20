# Contract SDK

### Why would you want to use the Contract SDK?

* you want to write Corda contracts with minimum code
* you want to write Corda contracts which are easy to read and understand

### What do you need to do to use it?

The Contract SDK itself is a CorDapp. In order to use its power you need to add it as a dependency in your build.gradle file. 
Typically, you would be referring to the Contract SDK from within your contracts module of your CorDapp so adding the dependency
to build.gradle of the contracts module makes most sense.

You will have a choice to make, whether you want to:

1) Fat jar the Contract SDK CorDapp into your contracts jar or
2) Keep the Contract SDK CorDapp out of your contracts jar and manually add the Contract SDK jar to the Corda transactions as attachment. Using, for example, [this helper function](examples/test-app/test-app-workflows/src/main/kotlin/net/corda/contractsdk/testapp/Extensions.kt).

If you decide for the second option you need to provide checks stopping a potential attacker from using different (and possibly malicious) version of the Contract SDK jar to the one intended. 

In order to fat jar the Contract SDK into your contracts jar, you would declare your dependency as:

`compile "com.r3.corda.lib.contracts:contract-sdk:$corda_contracts_sdk_version"`

In order to keep the Contract SDK jar separate, you would declare your dependency as:

`cordaCompile "com.r3.corda.lib.contracts:contract-sdk:$corda_contracts_sdk_version"`

### You got the dependency set, how do you write contracts now?

Normally you would go writing your contract like this:

    class MyContract : Contract {

      override fun verify(tx: LedgerTransaction) {
          // Verification logic here. Typically some kind of
          // when (command) {
          //  is Action -> do some checks
          //  is SomeOtherAction -> do some other checks
          // }
      }
    
      interface Commands : CommandData {
          class Action : Commands
          class SomeOtherAction : Commands
      }

    }

With the Contract SDK you don't need to write the `verify` method of your contract. Instead, you extend from the `StandardContract` and you 
annotate your contract and your commands with annotations defining what you want the contract and commands to accept and enforce. In the below
snippet placeholder annotations are used to describe the concept. Full list of annotations available to you is described in the next section of this document.

    @Annotation_Defining_What_The_Contract_Requires_Or_Enforces_Always_1
    @Annotation_Defining_What_The_Contract_Requires_Or_Enforces_Always_2
    class MyContract : Contract, StandardContract() {
    
      interface Commands : CommandData {
          @Annotation_Defining_What_The_Command_Requires_Or_Enforces_When_Present_On_The_Transaction_1
          @Annotation_Defining_What_The_Command_Requires_Or_Enforces_When_Present_On_The_Transaction_2
          class Action : Commands

          @Annotation_Defining_What_The_Command_Requires_Or_Enforces_When_Present_On_The_Transaction_3
          class SomeOtherAction : Commands
      }

    }

Note that `MyContract` here still must extend the `Contract` class, else it won't be recognized by the Corda's mechanism loading CorDapps.

### What annotations are available to you?

As of now there are over 30 annotations that you can use on the contract or command level. You can view them further down in this readme or in [Annotations.kt](contract-sdk/src/main/kotlin/net/corda/contractsdk/annotations/Annotations.kt). 
Their names follow a convention that should make it easier to find the annotation you are after. Each annotation follows this pattern:

`@[Require|Permit|Forbid][Subject][Scope][AnyOtherQualifier](parameter1, parameter2...)` 

such as, for example:

* @RequireNumberOfStatesOnInputAtLeast(1)
* @PermitStatusOnInput('Pending')  
* @RequireSignersFromEachOutputState('buyer','seller')
* @RequireStatusChangeInCoupledLinearStates('Pending','Active')
* @ForbidChangeInCoupledLinearStatesExcept('status','authorizedBy')

The only states in the transaction under verification which the annotations apply to are the states which belong to the contract (`@BelongsToContract`). So for example if you defined contract `MyContract` and
contract state `MyState` which belongs to `MyContract` and `MyContract` is annotated with `@RequireNumberOfStatesOnInputBetween(0,5)`, then whilst the contract will only allow 0 to 5 of `MyState`s 
on the input it won't care if you have 100 of input states of type `SomeOtherState` which doesn't belong to `MyContract`. Even this can be further narrowed down by using the `targetClasses` parameter of the annotations. More about it later in this readme.

The way the Contract SDK recognizes statuses of the Corda states is by utilizing the `StateWithStatus` interface. A state that wants to expose its status
to the Contract SDK must implement that interface.

Similarly, the Contract SDK translates roles (e.g. 'buyer') to parties by utilizing the `StateWithRoles` interface. A state that wants to expose the translation of roles to parties
must implement that interface.

This means that if you want to use annotations referring to status or roles (e.g. `@PermitStatusOnInput` or `@RequireDistinctPartiesWithinEachInputState`), you will also need to use those two interfaces when defining the Contract states.
You can see an example of that in the examples/test-app, where the `Membership` states implement both the interfaces.

This is the whole list of annotations currently available for use. Click on the black triangle for a short description.

* Require
  - <details><summary>RequireNumberOfStatesOnInput</summary>The number of input states must be the one provided as parameter.</details>
  - <details><summary>RequireNumberOfStatesOnInputAtLeast</summary>The number of input states must be at least the one provided as parameter.</details>
  - <details><summary>RequireNumberOfStatesOnInputAtMost</summary>The number of input states must be at most the one provided as parameter.</details>
  - <details><summary>RequireNumberOfStatesOnInputBetween</summary>The number of input states must be in the range provided in parameters.</details>
  - <details><summary>RequireNumberOfStatesOnOutput</summary>The number of output states must be the one provided as parameter.</details>
  - <details><summary>RequireNumberOfStatesOnOutputAtLeast</summary>The number of output states must be at least the one provided as parameter.</details>
  - <details><summary>RequireNumberOfStatesOnOutputAtMost</summary>The number of output states must be at most the one provided as parameter.</details>
  - <details><summary>RequireNumberOfStatesOnOutputBetween</summary>The number of output states must be in the range provided in parameters.</details>
  - <details><summary>RequireSignersFromEachInputState</summary>Takes list of roles as parameter. Each input state is expected to implement interface StateWithRoles. During verification of the transaction each role is translated into a party (or parties) using the method from the StateWithRoles interface. And each such party must be a signer on the transaction.</details>
  - <details><summary>RequireSignersFromEachOutputState</summary>Takes list of roles as parameter. Each output state is expected to implement interface StateWithRoles. During verification of the transaction each role is translated into a party (or parties) using the method from the StateWithRoles interface. And each such party must be a signer on the transaction.</details>
  - <details><summary>RequireStatusChangeInCoupledLinearStates</summary>States on input and output must be linear and they must form pairs by linear id. They must also implement interface StateWithStatus. The status on the input side must be as defined in the first parameter and the status on the output must be as defined in the second parameter.</details>
  - <details><summary>RequireDistinctPartiesWithinEachInputState</summary>Each state on input must implement interface StateWithRoles. Each state on input is asked to provide parties for each of the roles listed among the parameters. All these parties must be distinct.</details>
  - <details><summary>RequireDistinctPartiesWithinEachOutputState</summary>Each state on output must implement interface StateWithRoles. Each state on output is asked to provide parties for each of the roles listed among the parameters. All these parties must be distinct.</details>
  - <details><summary>RequirePartyToAssumeAtLeastOneOtherRoleWithinEachInputState</summary>Each state on input must implement interface StateWithRoles. Each state on input is asked to provide party for the role set in the first parameter and parties for the roles provided in the second parameter. The party resolved from the first parameter must be found among the parties resolved from the second parameter.</details>
  - <details><summary>RequirePartyToAssumeAtLeastOneOtherRoleWithinEachOutputState</summary>Each state on output must implement interface StateWithRoles. Each state on output is asked to provide party for the role set in the first parameter and parties for the roles provided in the second parameter. The party resolved from the first parameter must be found among the parties resolved from the second parameter.</details>
  - <details><summary>RequirePropertySetOnInput</summary>Each state on the input must have the property specified as the parameter set.</details>
  - <details><summary>RequirePropertiesSetOnInput</summary>Each state on the input must have all the properties specified in the parameters set.</details>
  - <details><summary>RequirePropertySetOnOutput</summary>Each state on the output must have the property specified as the parameter set.</details>
  - <details><summary>RequirePropertiesSetOnOutput</summary>Each state on the output must have all the properties specified in the parameters set.</details>
  - <details><summary>RequirePropertyNotSetOnInput</summary>Each state on the input must have the property specified as the parameter set to null.</details>
  - <details><summary>RequirePropertiesNotSetOnInput</summary>Each state on the input must have all the properties specified in the parameters set to null.</details>
  - <details><summary>RequirePropertyNotSetOnOutput</summary>Each state on the output must have the property specified as the parameter set to null.</details>
  - <details><summary>RequirePropertiesNotSetOnOutput</summary>Each state on the output must have all the properties specified in the parameters set to null.</details>
* Permit
  - <details><summary>PermitStatusOnInput</summary>All input states must be in the status set as parameter. To understand the status, the state must implement the StateWithStatus interface.</details>
  - <details><summary>PermitStatusesOnInput</summary>All input states must be in one of the statuses listed in the parameter. To understand the status, the state must implement the StateWithStatus interface.</details>
  - <details><summary>PermitStatusOnOutput</summary>All output states must be in the status set as parameter. To understand the status, the state must implement the StateWithStatus interface.</details>
  - <details><summary>PermitStatusesOnOutput</summary>All output states must be in one of the statuses listed in the parameter. To understand the status, the state must implement the StateWithStatus interface.</details>
* Forbid
  - <details><summary>ForbidChangeInCoupledLinearStatesExcept</summary>States on input and output must be linear and they must form pairs by linear id. No change in values of properties between input and output is allowed except for the exempt properties listed in the parameter.</details>

Occasionally, you may want to tag the same command (or contract) with the same annotation more than once. For example the `@RequireDistinctPartiesWithinEachInputState` could be a candidate for such use. Kotlin currently won't let you 
do this. But you can still achieve the same effect by using a `@*List` annotation, such as `@RequireDistinctPartiesWithinEachInputStateList`, where you provide the multiple annotations of the same type as a parameter.
  
## What if you need more custom verification logic?  

If you need to provide further verification logic, which is not offered by the annotations then you have two options.

1. Implement the `verifyFurther` method of the `StandardContract` class, which you are extending your contract from. 
This method will be called on every transaction the contract verifies.
2. Have any of your commands implement the interface `StandardCommand` and its method `verifyFurther`. This method will be called
if the transaction being verified contains that command. 
  
## What if you have multiple state types belonging to the same contract and you want different verification rules for each?

Imagine you have a contract looking like this:

    class MyContract : Contract, StandardContract() {
    
      interface Commands : CommandData {
          class Issue : Commands
      }

    }

And two state types belonging to this contract.

    @BelongsToContract(MyContract::class)
    class StateOne(override val participants: List<AbstractParty>) : ContractState

    @BelongsToContract(MyContract::class)
    class StateTwo(override val participants: List<AbstractParty>) : ContractState

You can target the annotations driving the contract behaviour specifically to a state type using the `targetClasses` parameter. Below we
allow the `Issue` command issue exactly one state of type `StateOne` but at least one state of type `StateTwo`.

    class MyContract : Contract, StandardContract() {
    
      interface Commands : CommandData {
          @RequireNumberOfStatesOnOutput(1, targetClasses = [StateOne::class])
          @RequireNumberOfStatesOnOutputAtLeast(1, targetClasses = [StateTwo::class])
          class Issue : Commands
      }

    }

Remember, if you don't provide the `targetClasses` parameter then the default behaviour is that the annotation targets all states whose types belong to the contract.

## Contributing

The Contract SDK is an open-source project and contributions are welcome as seen here: [Contributing](CONTRIBUTING.md)

## Feedback

Any suggestions / issues are welcome in the issues section: <https://github.com/corda/contract-sdk/issues/new>

## Versioning

* latest code is in `main` branch
* tags on `main` branch mark versions (e.g. v0.9)

## Disclaimer

Whilst all the verification logic behind each annotation has been tested, always test your contract logic and your entire CorDapp independently and thoroughly before using it in production. Also, please note that
R3 doesn't provide any support for this utility. Responding to any issues raised will be on best effort basis.