package quotidian.examples.lens

import scala.quoted.*

import quotidian.*
import quotidian.syntax.*

case class LensesMap[S](lenses: Map[String, Lens[S, ?]])

object LensesMap:
  inline given derived[S]: LensesMap[S] = ${ derivedImpl[S] }

  def derivedImpl[S: Type](using Quotes): Expr[LensesMap[S]] =
    import quotes.reflect.*

    val productMirror = MacroMirror.summonProduct[S]

    val lenses = productMirror.elems.map: elem =>
      import elem.asType

      val selector = '{ (s: S) => ${ elem.get('s) } }

      elem.label -> LensMacros.makeLensImpl(selector)

    val map = Expr.ofMap(lenses)

    '{
      LensesMap($map)
    }
