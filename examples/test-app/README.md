# Examples/Test-App

### Description

This is a very simple CorDapp demonstrating the use of the Contract SDK. The app allows creating memberships and managing them. 
It consists of two modules: `test-app-contracts` and `test-app-workflows`.

#### Test-App-Contracts

The state classes and the contract are defined in [ContractAndState.kt](test-app-contracts/src/main/kotlin/com/r3/corda/lib/contracts/contractsdk/testapp/contracts/ContractAndStates.kt).
There are two types of membership, each is represented by its own `ContractState` class:

* `LastingMembershipState`
* `OneUseMembershipState`

Each can be in one of these two statuses:

* Pending
* Active

Each has two other properties, of type `Party`. In the Contract SDK speak those would be "roles":

* Owner
* Issuer

The actions (i.e. commands) which can be executed on the memberships are the following. To understand what they expect and enforce just look at their definition
in the `MembershipContract` and the associated annotations ;-)

* Request
* Activate
* Revoke
* Use

#### Test-App-Workflows

The flows are deliberately generic so that they let you create transactions which are deemed invalid by the `MembershipContract`.
Thus you can check for yourself that the contract written with the Contract SDK behaves as expected. The flows are:

* Issue
* Modify
* Deissue

### Usage

Here are some examples of flows invocation via the node shell:

* flow start IssueInitiator membershipType: LASTING, issuer: Operator, owner: Client, status: PENDING
* flow start ModifyInitiator identifier: 35ddf139-bf18-496c-96ef-f8dc00ad5dd2, command: Activate, status: ACTIVE

You will, of course, need to replace the identifier with the one of your membership state. Also, the activation of the membership
must be run from the issuer's node. In this example that's from the Operator's node.