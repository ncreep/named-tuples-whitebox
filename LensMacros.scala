// Adapted from:
// https://github.com/kitlangton/quotidian/blob/2c1ffb497bbc91f8860a965e757812d6609ff84c/examples/src/main/scala/quotidian/examples/lens/LensMacros.scala
package quotidian.examples.lens

import scala.quoted.*

import quotidian.*

object LensMacros:
  def makeLensImpl[S: Type, A: Type](selectorExpr: Expr[S => A])(using Quotes): Expr[Lens[S, A]] =
    import quotes.reflect.*

    selectorExpr.asTerm.underlyingArgument match
      case Lambda(_, select @ Select(s, a)) =>
        val productMirror = MacroMirror.summonProduct[S]

        val elem = productMirror
          .elemForSymbol(select.symbol)
          .getOrElse(
            report.errorAndAbort(
              s"Invalid selector ${select.show}, must be a field of ${productMirror.monoType.show}"))
          .asElemOf[A]

        '{
          new Lens[S, A]:
            def get(s: S) = ${ elem.get('s) }
            def set(s: S, a: A) = ${ elem.set('s, 'a) }
        }
      case other =>
        report.errorAndAbort(s"Expected a selector of the form `s => a`, but got: ${other}")
