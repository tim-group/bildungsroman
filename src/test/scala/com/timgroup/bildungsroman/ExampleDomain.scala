package com.timgroup.bildungsroman

import GivenWhenThen._

case class User(name: String)
case class Account(id: Int, owner: User, balance: BigDecimal)

case class Accounts() {
  
  import Accounts.AccountId
  
  var accounts = Map.empty[AccountId, Account]
  def create(owner: User): AccountId = {
    val id = accounts.size
    val newAccount = Account(id, owner, 0)
    accounts = accounts.updated(id, newAccount)
    id
  }
  def get(id: AccountId): Account = accounts(id)
  def updateBalance(id: AccountId, newBalance: BigDecimal) = {
    val newAccount = get(id).copy(balance = newBalance)
    accounts = accounts.updated(id, newAccount)
    newBalance
  }
}

object Accounts {
  type AccountId = Int
}