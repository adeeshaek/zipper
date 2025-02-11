package zipper

import shapeless.ops.hlist.{Replacer, Selector}
import shapeless.{Generic, HList}

import scala.collection.Factory
import scala.language.higherKinds

private[zipper] trait GenericUnzipInstances {
  implicit def `Unzip List-based`[A, L <: HList](
      implicit generic: Generic.Aux[A, L],
      select: Selector[L, List[A]],
      replace: Replacer.Aux[L, List[A], List[A], (List[A], L)]
  ): Unzip[A] = new Unzip[A] {
    type Out = (List[A], L)
    def unzip(node: A): List[A] = select(generic.to(node))
    def zip(node: A, children: List[A]): A = generic.from(replace(generic.to(node), children)._2)
  }

  /** A helper for deriving [[zipper.Unzip]] instances for collections other than List */
  object For {
    /**
     * @tparam A The type of the tree-like data structure
     * @tparam Coll The type of the collection used for recursion (e.g. Vector)
     */
    def apply[A, Coll[X] <: Seq[X]] = new For[A, Coll]
  }

  class For[A, Coll[X] <: Seq[X]] {
    /** Derive an instance of `Unzip[A]` */
    def derive[L <: HList](
        implicit generic: Generic.Aux[A, L],
        select: Selector[L, Coll[A]],
        replace: Replacer.Aux[L, Coll[A], Coll[A], (Coll[A], L)],
        factory: Factory[A, Coll[A]]
    ): Unzip[A] = new Unzip[A] {
      def unzip(node: A): List[A] = select(generic.to(node)).toList
      def zip(node: A, children: List[A]): A = generic.from(
        replace(
          t = generic.to(node),
          u = children.to[Coll[A]](factory)
        )._2
      )
    }
  }
}
