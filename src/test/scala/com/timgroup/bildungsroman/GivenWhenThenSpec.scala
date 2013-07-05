package com.timgroup.bildungsroman

import org.scalatest._
import org.scalatest.matchers.MustMatchers

import Accounts._
import AccountActions._
import GivenWhenThen._

class GivenWhenThenSpec extends FunSpec with MustMatchers {

  val accountRef = Ref[AccountId]("account")
  val otherAccountRef = Ref[AccountId]("other account")
  val testUser = User("test user")
  val otherTestUser = User("other test user")
  val admin = User("admin")
  
  describe("Givens") {

    it("populate the test context") {
      val (context, id) = defaultAccountsContext.verify(
        givenThe(testUser) { user =>
          accountsRef.flatMap(accounts => accountRef := accounts.create(user))
        })

      context(accountRef) must be(id)
    }

    it("can be composed") {
      val (context, (firstAccountId, secondAccountId)) = defaultAccountsContext.verify(for {
        firstAccountId  <- givenThe(testUser)      { createsAnAccount(accountRef) }
        secondAccountId <- givenThe(otherTestUser) { createsAnAccount(otherAccountRef) }
      } yield (firstAccountId, secondAccountId))

      context(accountRef) must be(firstAccountId)
      context(otherAccountRef) must be(secondAccountId)
    }
  }

  describe("Whens") {
    it("compose with Givens") {
      val (context, (newBalance1, newBalance2)) = defaultAccountsContext.verify(for {
        _ <- givenThe(testUser)      { createsAnAccount(accountRef) }
             .andThen                { deposits(accountRef, 100) }
        _ <- givenThe(otherTestUser) { createsAnAccount(otherAccountRef) }
             .andThen                { deposits(otherAccountRef, 200) }

        balances <- whenThe(admin) { transfers(accountRef, otherAccountRef, 50) }
      } yield balances)

      newBalance1 must be(50)
      newBalance2 must be(250)
    }
  }
  
  describe("Thens") {
    it("compose with Givens and Whens") {
      defaultAccountsContext.verify(for {
        _ <- givenThe(testUser)      { createsAnAccount(accountRef) }
             .andThen                { deposits(accountRef, 40) }
             .andThen                { deposits(accountRef, 60) }
        _ <- givenThe(otherTestUser) { createsAnAccount(otherAccountRef) }
             .andThen                { deposits(otherAccountRef, 200) }

        _ <- whenThe(admin)        { transfers(accountRef, otherAccountRef, 50) }
        
        _ <- thenThe(testUser)     { hasABalanceOf(50).in(accountRef) }
        _ <- andThe(otherTestUser) { hasABalanceOf(250).in(otherAccountRef) }
      } yield ())
    }
  }
}