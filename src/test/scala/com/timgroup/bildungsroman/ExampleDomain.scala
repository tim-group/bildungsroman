package com.timgroup.bildungsroman

case class User(name: String)
case class Account(id: Int, owner: User, balance: BigDecimal)

object Accounts {
  
  type AccountId = Int
  
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