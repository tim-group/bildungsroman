bildungsroman
=============

[![Build Status](https://travis-ci.org/youdevise/bildungsroman.png)](https://travis-ci.org/youdevise/bildungsroman)

[Narrative](http://github.com/youdevise/narrative)-style testing for Scala - see [this blogpost](https://devblog.timgroup.com/2013/07/05/narrative-style-testing-with-bildungsroman/) for details.

Example code:

```scala
describe("A calculator") {
  it("adds two numbers") {
    calculatorContext.verify(for {
      _ <- givenThe(operator) { presses('2') }
                   .andThen   { presses('+') }
                   .andThen   { presses('2') }

      _ <- whenThe(operator)  { presses('=') }

      _ <- thenThe(operator)  { seesTheDisplayedValue('4') }
    } yield ())
  }
}
```

Use it in your project - add to build.sbt:

```scala
libraryDependencies += "com.timgroup" %% "bildungsroman" % "0.1.0" % "test"
```
