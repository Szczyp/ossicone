(ns nuejure.monad
  (:require
   [nuejure.functor :refer [mapf]]
   [nuejure.applicative :refer [coerce-return value #+cljs Return]])
  #+clj (:import
         [nuejure.applicative Return]
         [clojure.lang
          PersistentList PersistentVector LazySeq
          PersistentHashMap PersistentArrayMap PersistentTreeMap
          Fn Keyword
          PersistentHashSet PersistentTreeSet]))

(defprotocol Monad
  (join* [this]))

(extend-protocol Monad
  Return
  (join* [this] (value this))
  #+clj PersistentList #+cljs List
  (join* [this] (apply list (apply concat this)))
  PersistentVector
  (join* [this] (vec (apply concat this)))
  LazySeq
  (join* [this] (apply concat this))
  PersistentHashSet
  (join* [this] (apply hash-set (apply concat this)))
  #+clj Fn #+cljs function
  (join* [this] #((this %) %))
  Keyword
  (join* [this] #((this %) %)))

(defn bind
  ([m f] (join* (mapf (comp second (partial coerce-return m) f) m)))
  ([m f & fs] (reduce bind m (cons f fs))))

