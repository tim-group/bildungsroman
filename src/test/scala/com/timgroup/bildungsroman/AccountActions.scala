package com.timgroup.bildungsroman

import org.scalatest.matchers.MustMatchers
import Accounts.AccountId

object AccountActions extends MustMatchers {
  
  def createsAnAccount(ref: Ref[AccountId]) = { user: User => ref := Accounts.create(user) }
  def deposits(ref: Ref[AccountId], amount: BigDecimal) = { user: User =>
    for {
      account <- ref.map(Accounts.get _)
      newBalance = account.balance + amount
      _ <- ref.map(id => Accounts.updateBalance(id, newBalance))
    } yield newBalance
  }

  def transfers(ref1: Ref[AccountId], ref2: Ref[AccountId], amount: BigDecimal) = { user: User =>
    for {
      account1 <- ref1.map(Accounts.get _)
      account2 <- ref2.map(Accounts.get _)
      newBalance1 = account1.balance - amount
      newBalance2 = account2.balance + amount
      _ <- ref1.map(id => Accounts.updateBalance(id, newBalance1))
      _ <- ref2.map(id => Accounts.updateBalance(id, newBalance2))
    } yield (newBalance1, newBalance2)
  }
  
  def hasABalanceOf(amount: BigDecimal) = new {
    def in(accountRef: Ref[AccountId]) = { user: User =>
      accountRef.map { id => Accounts.get(id).balance must be(amount) }
    }
  }
}