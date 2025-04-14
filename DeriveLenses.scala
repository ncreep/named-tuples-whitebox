package quotidian.examples.lens

import scala.NamedTuple.Map

type ToLens[S] = [T] =>> Lens[S, T]

type LensesFor[A] = NamedTuple.From[A] `Map` ToLens[A]

trait DeriveLenses[A](using lensesMap: LensesMap[A]) extends Selectable:
  type Fields = LensesFor[A]

  private val lenses = lensesMap.lenses

  inline def selectDynamic(name: String): Lens[A, ?] =
    lenses.getOrElse(
      name,
      sys.error(s"Invalid field name [$name]"))
