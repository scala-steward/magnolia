package magnolia.examples

import magnolia.*

extension [A: Csv](value: A) def csv: List[String] = summon[Csv[A]](value)

trait Csv[A]:
  def apply(a: A): List[String]

object Csv extends Derivation[Csv]:
  def join[A](ctx: CaseClass[Csv, A]): Csv[A] = a =>
    ctx.params.foldLeft(List[String]()) { (acc, p) => acc ++ p.typeclass(p.deref(a)) }

  def split[A](ctx: SealedTrait[Csv, A]): Csv[A] = a => ctx.choose(a) { sub => sub.typeclass(sub.value) }

  given Csv[String] = List(_)
  given Csv[Int] = i => List(i.toString)
  given Csv[Char] = c => List(c.toString)
  given [T: Csv]: Csv[Seq[T]] = _.to(List).flatMap(summon[Csv[T]](_))

case class Foo(x: Int, y: String) derives Csv
case class Bar(c: Char, fs: Foo*) derives Csv