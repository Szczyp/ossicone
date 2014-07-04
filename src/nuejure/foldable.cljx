(ns nuejure.foldable
  #+clj (:import
         [clojure.lang
          PersistentList PersistentVector LazySeq
          PersistentHashMap PersistentArrayMap PersistentTreeMap
          PersistentHashSet PersistentTreeSet]))

(defprotocol Foldable
  (fold* [this f z]))

(extend-protocol Foldable
  #+clj PersistentList #+cljs List
  (fold* [this f z] (reduce f z this))
  PersistentVector
  (fold* [this f z] (reduce f z this))
  LazySeq
  (fold* [this f z] (reduce f z this))
  PersistentHashMap
  (fold* [this f z] (reduce (fn [a [k v]] (f a v)) z this))
  PersistentArrayMap
  (fold* [this f z] (reduce (fn [a [k v]] (f a v)) z this))
  PersistentTreeMap
  (fold* [this f z] (reduce (fn [a [k v]] (f a v)) z this))
  PersistentHashSet
  (fold* [this f z] (reduce f z this))
  PersistentTreeSet
  (fold* [this f z] (reduce f z this)))

(defn fold [f z s]
  (fold* s f z))

