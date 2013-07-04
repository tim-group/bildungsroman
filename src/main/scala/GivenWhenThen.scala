package com.timgroup.bildungsroman

import scalaz._
import Scalaz._

trait GivenWhenThen {

  val givenThe = GivenWhenThen
  val whenThe = GivenWhenThen
  val thenThe = GivenWhenThen
  val andThe = GivenWhenThen

  type RefValues = Map[Ref[_], RefValue[_]]
  type GWTState[A] = State[RefValues, A]
  def apply[A, B](input: A)(f: A => GWTState[B]): GWTState[B] = f(input)

  def verify[A](s: GWTState[A]): (RefValues, A) = s.apply(Map.empty)
  
  def ref[A] = new Ref[A]()
}

import GivenWhenThen._

case class Ref[A](id: java.util.UUID = java.util.UUID.randomUUID) extends GWTState[A] {
  private val self = this
  override def apply(s: RefValues): (RefValues, A) = (s, s(this).get[A])

  def :=(value: A): GWTState[A] = new GWTState[A] { override def apply(s: RefValues) = (s.updated(self, RefValue(self, value)), value) }

  def /=(f: A => A): GWTState[A] = for {
    oldValue <- this
    newValue <- this := f(oldValue)
  } yield newValue

  def set(value: A) = :=(value)
  def update(f: A => A) = /=(f)
  def orElse(alternative: A): GWTState[A] = new GWTState[A] { override def apply(s: RefValues) = (s, s.get(self).map(_.get[A]).getOrElse(alternative)) }
}

case class RefValue[A](ref: Ref[A], value: A) {
  def get[B] = value.asInstanceOf[B]
}

object GivenWhenThen extends GivenWhenThen