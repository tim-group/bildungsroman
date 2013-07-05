package com.timgroup.bildungsroman

import org.scalatest._
import org.scalatest.matchers.MustMatchers

class ContextSpec extends FunSpec with MustMatchers {

  val stringRef = Ref[String]("string")
  val intRef = Ref[Int]("integer")
  val otherStringRef = Ref[String]("another string")
  
  describe("A context") {
    it("can be initialised with a typesafe map of ref/value pairs") {
      val ctx = Context(
        stringRef -> "A string",
        intRef    -> 4
      )
      
      ctx(stringRef) must be("A string")
      ctx(intRef) must be(4)
    }
    
    it("can create an updated copy of itself with a new ref/value pair") {
      val ctx = Context.empty.updated(stringRef, "foo")
      
      ctx(stringRef) must be("foo")
    }
    
    it("can create an updated copy of itself with an existing ref/value pair overwritten") {
      val ctx = Context(intRef -> 5).updated(intRef, 6)
      
      ctx(intRef) must be(6)
    }
    
    it("can be extended with a further map of ref/value pairs") {
      val ctx = Context(
        stringRef -> "A string",
        intRef    -> 4
      ).extended(
        stringRef -> "Overwritten string",
        otherStringRef -> "New string")
        
      ctx(stringRef) must be("Overwritten string")
      ctx(intRef) must be(4)
      ctx(otherStringRef) must be("New string")
    }
    
    it("can be extended with another context") {
      val ctx1 = Context(
        stringRef -> "A string",
        intRef    -> 4
      )
      
      val ctx2 = Context(
        stringRef -> "Overwritten string",
        otherStringRef -> "New string"
      )
      
      val ctx = ctx1.extended(ctx2)
      ctx(stringRef) must be("Overwritten string")
      ctx(intRef) must be(4)
      ctx(otherStringRef) must be("New string")
    }
  }
}