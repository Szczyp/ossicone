(ns nuejure.traversable
  (:require
   [nuejure.return :refer [return]]
   [nuejure.functor :refer [mapf]]
   [nuejure.applicative :refer [ap]]
   [nuejure.foldable :refer [fold]])
  #+clj (:import
         [clojure.lang
          PersistentList PersistentVector LazySeq
          PersistentHashMap PersistentArrayMap PersistentTreeMap
          PersistentHashSet PersistentTreeSet]))

(defprotocol Traversable
  (traverse* [this]))

(extend-protocol Traversable
  #+clj PersistentList #+cljs List
  (traverse* [this]
    (mapf reverse
          (fold (fn [s a]
                  (ap (return conj) s a))
                (return (empty this)) this)))
  PersistentVector
  (traverse* [this]
    (fold (fn [s a]
            (ap (return conj) s a))
          (return (empty this)) this))
  LazySeq
  (traverse* [this]
    (mapf (comp #(lazy-seq %) reverse)
          (fold (fn [s a]
                  (ap (return conj) s a))
                (return (empty this)) this)))
  PersistentHashMap
  (traverse* [this]
    (reduce (fn [s [k v]]
              (ap (return conj) s (mapf (partial vector k) v)))
            (return (empty this)) this))
  PersistentArrayMap
  (traverse* [this]
    (reduce (fn [s [k v]]
              (ap (return conj) s (mapf (partial vector k) v)))
            (return (empty this)) this))
  PersistentTreeMap
  (traverse* [this]
    (reduce (fn [s [k v]]
              (ap (return conj) s (mapf (partial vector k) v)))
            (return (empty this)) this))
  PersistentHashSet
  (traverse* [this]
    (fold (fn [s a]
            (ap (return conj) s a))
          (return (empty this)) this))
  PersistentTreeSet
  (traverse* [this]
    (fold (fn [s a]
            (ap (return conj) s a))
          (return (empty this)) this)))

(def traverse traverse*)
