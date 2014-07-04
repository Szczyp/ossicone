(ns nuejure.functor
  (:require
   [nuejure.return :refer [return]])
  (:import
   [nuejure.return Return]
   #+clj [clojure.lang
          PersistentList PersistentVector LazySeq
          MapEntry PersistentHashMap PersistentArrayMap PersistentTreeMap
          Fn Keyword
          PersistentHashSet PersistentTreeSet]))

(defprotocol Functor
  (mapf* [this f]))

(extend-protocol Functor
  Return
  (mapf* [this f] (return (f (#+clj .value #+cljs .-value this))))
  #+clj PersistentList #+cljs List
  (mapf* [this f] (apply list (map f this)))
  PersistentVector
  (mapf* [this f] (mapv f this))
  LazySeq
  (mapf* [this f] (map f this))
  #+clj MapEntry
  #+clj (mapf* [[k v] f] (MapEntry. k (f v)))
  PersistentHashMap
  (mapf* [this f] (into (hash-map) (map (fn [[k v]] [k (f v)]) this)))
  PersistentArrayMap
  (mapf* [this f] (into (hash-map) (map (fn [[k v]] [k (f v)]) this)))
  PersistentTreeMap
  (mapf* [this f] (into (sorted-map) (map (fn [[k v]] [k (f v)]) this)))
  PersistentHashSet
  (mapf* [this f] (into (hash-set) (map f this)))
  PersistentTreeSet
  (mapf* [this f] (into (sorted-set) (reverse (map f this))))
  #+clj Fn #+cljs function
  (mapf* [this f] (comp f this))
  Keyword
  (mapf* [this f] (comp f this)))

(defn mapf [f a] (mapf* a f))
