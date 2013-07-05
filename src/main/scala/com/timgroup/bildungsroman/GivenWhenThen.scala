package com.timgroup.bildungsroman

import scalaz._
import Scalaz._

trait GivenWhenThen {

  case class ActionWithActor[A, B](input: A, f: A => GWTState[B]) extends GWTState[B] {
    override def apply(s: Context) = f(input)(s)
    def andThen[C](f2: A => GWTState[C]) = ActionWithActor[A, C](input, i => f(i).flatMap(_ => f2(i)))
  }
  
  object DoWithActor {
    def apply[A, B](input: A)(f: A => GWTState[B]): ActionWithActor[A, B] = ActionWithActor(input, f)
  }
  
  val the = DoWithActor
  val givenThe = DoWithActor
  val whenThe = DoWithActor
  val thenThe = DoWithActor
  val andThe = DoWithActor
  
  object DoWithoutActor {
    def apply[A](s: GWTState[A]): GWTState[A] = s
  }
  val given_ = DoWithoutActor
  val when_ = DoWithoutActor
  val then_ = DoWithoutActor
  val and_ = DoWithoutActor

  type GWTState[A] = State[Context, A]
  
  def verify[A](s: GWTState[A], context: Context = Context.empty): (Context, A) = context.verify[A](s)
}

object GivenWhenThen extends GivenWhenThen

import GivenWhenThen._

case class Ref[A](id: String = java.util.UUID.randomUUID.toString) extends GWTState[A] {
  private val self = this
  override def apply(s: Context): (Context, A) = s.get(this) match {
    case Some(value) => (s, value)
    case None => throw new MissingRefException(this) 
  }

  def :=(value: A): GWTState[A] = new GWTState[A] { override def apply(s: Context) = (s.updated(self, value), value) }

  def /=(f: A => A): GWTState[A] = for {
    oldValue <- this
    newValue <- this := f(oldValue)
  } yield newValue

  def set(value: A) = :=(value)
  def update(f: A => A) = /=(f)
  def get: GWTState[Option[A]] = new GWTState[Option[A]] { override def apply(s: Context) = (s, s.get(self)) }
  def orElse(alternative: A): GWTState[A] = get.map(_.getOrElse(alternative))
  
  def ->(value: A) = RefValue(this, value)
}

case class RefValue[A](ref: Ref[A], value: A) {
  val asTuple: (Ref[_], Any) = (ref, value)
}

class MissingRefException(ref: Ref[_]) extends RuntimeException("Missing value for reference %s".format(ref.id)) { }

case class Context(values: Map[Ref[_], Any]) {
  def apply[A](ref: Ref[A]): A = get(ref) match {
    case Some(value) => value
    case None => throw new MissingRefException(ref)
  }
  def get[A](ref: Ref[A]): Option[A] = values.get(ref).map(_.asInstanceOf[A])
  def getOrElse[A](ref: Ref[A], default: A): A = get(ref).getOrElse(default)
  def updated[A](ref: Ref[A], value: A): Context = Context(values.updated(ref, value))
  private def extended(otherValues: Map[Ref[_], Any]): Context = Context(values ++ otherValues)
  def extended(other: Context): Context = extended(other.values)
  def extended(otherValues: RefValue[_]*): Context = extended(otherValues.map(_.asTuple).toMap)
  def verify[A](s: GWTState[A]): (Context, A) = s.apply(this)
}

object Context {
  def empty = Context(Map.empty[Ref[_], Any])
  def apply(values: RefValue[_]*): Context = Context(values.map(_.asTuple).toMap)
}