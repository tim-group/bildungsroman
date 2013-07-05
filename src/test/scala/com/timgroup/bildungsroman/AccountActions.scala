package com.timgroup.bildungsroman

import org.scalatest.matchers.MustMatchers
import Accounts._

object AccountActions extends MustMatchers {
  
  val accountsRef = Ref[Accounts]("accounts")
  val defaultAccountsContext = Context(accountsRef -> new Accounts())
  
  def createsAnAccount(ref: Ref[AccountId]) = { user: User => accountsRef.flatMap(accounts => ref := accounts.create(user) ) }
  
  def deposits(ref: Ref[AccountId], amount: BigDecimal) = { user: User =>
    for {
      accounts <- accountsRef
      account <- ref.map(accounts.get _)
      newBalance = account.balance + amount
      _ <- ref.map(id => accounts.updateBalance(id, newBalance))
    } yield newBalance
  }

  def transfers(ref1: Ref[AccountId], ref2: Ref[AccountId], amount: BigDecimal) = { user: User =>
    for {
      accounts <- accountsRef
      account1 <- ref1.map(accounts.get _)
      account2 <- ref2.map(accounts.get _)
      newBalance1 = account1.balance - amount
      newBalance2 = account2.balance + amount
      _ <- ref1.map(id => accounts.updateBalance(id, newBalance1))
      _ <- ref2.map(id => accounts.updateBalance(id, newBalance2))
    } yield (newBalance1, newBalance2)
  }
  
  def hasABalanceOf(amount: BigDecimal) = new {
    def in(accountRef: Ref[AccountId]) = { user: User =>
      for {
        accounts <- accountsRef
        account <- accountRef.map( id => accounts.get(id) )
      } yield account.balance must be(amount)
    }
  }
}