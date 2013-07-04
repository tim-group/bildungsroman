package com.timgroup.bildungsroman

import org.scalatest._
import org.scalatest.matchers.MustMatchers

import Accounts.AccountId
import AccountActions._

class GivenWhenThenSpec extends FunSpec with MustMatchers with GivenWhenThen {

  val accountRef = ref[AccountId]
  val otherAccountRef = ref[AccountId]
  val testUser = User("test user")
  val otherTestUser = User("other test user")
  val admin = User("admin")
  
  describe("Givens") {

    it("populate the test context") {
      val (context, id) = verify(
        givenThe(testUser) { user =>
          accountRef := Accounts.create(user)
        })

      context(accountRef).value must be(id)
    }

    it("can be composed") {
      val (context, (firstAccountId, secondAccountId)) = verify(for {
        firstAccountId  <- givenThe(testUser)      { createsAnAccount(accountRef) }
        secondAccountId <- givenThe(otherTestUser) { createsAnAccount(otherAccountRef) }
      } yield (firstAccountId, secondAccountId))

      context(accountRef).value must be(firstAccountId)
      context(otherAccountRef).value must be(secondAccountId)
    }
  }

  describe("Whens") {
    it("compose with Givens") {
      val (context, (newBalance1, newBalance2)) = verify(for {
        _ <- givenThe(testUser)      { createsAnAccount(accountRef) }
        _ <- givenThe(otherTestUser) { createsAnAccount(otherAccountRef) }
        _ <- givenThe(testUser)      { deposits(accountRef, 100) }
        _ <- givenThe(otherTestUser) { deposits(otherAccountRef, 200) }

        balances <- whenThe(admin) { transfers(accountRef, otherAccountRef, 50) }
      } yield balances)

      newBalance1 must be(50)
      newBalance2 must be(250)
    }
  }
  
  describe("Thens") {
    it("compose with Givens and Whens") {
      verify(for {
        _ <- givenThe(testUser)    { createsAnAccount(accountRef) }
        _ <- andThe(otherTestUser) { createsAnAccount(otherAccountRef) }
        _ <- andThe(testUser)      { deposits(accountRef, 100) }
        _ <- andThe(otherTestUser) { deposits(otherAccountRef, 200) }

        _ <- whenThe(admin)        { transfers(accountRef, otherAccountRef, 50) }
        
        _ <- thenThe(testUser)     { hasABalanceOf(50).in(accountRef) }
        _ <- andThe(otherTestUser) { hasABalanceOf(250).in(otherAccountRef) }
      } yield ())
    }
  }
}