(ns nuejure.protocols
  (:refer-clojure :exclude [map])
  (:require [nuejure.core :refer [lazy-map]])
  #+clj (:import
         [clojure.lang PersistentList PersistentVector LazySeq
          PersistentHashMap PersistentArrayMap Fn]))

(defprotocol Functor
  (map [this f]))

(extend-protocol Functor
  #+clj PersistentList #+cljs List
  (map [this f] (doall (lazy-map f this)))
  PersistentVector
  (map [this f] (mapv f this))
  LazySeq
  (map [this f] (lazy-map f this))
  PersistentHashMap
  (map [this f] (into (empty this) (lazy-map (fn [[k v]] [k (f v)]) this)))
  PersistentArrayMap
  (map [this f] (into (empty this) (lazy-map (fn [[k v]] [k (f v)]) this)))
  #+clj Fn #+cljs function
  (map [this f] (comp this f)))

(defprotocol Applicative
  (ap [this that]))

(extend-protocol Applicative
  #+clj PersistentList #+cljs List
  (ap [this that] (for [f this a that] (f a)))

  PersistentVector
  (ap [this that] (vec (for [f this a that] (f a)))))

(defprotocol Monad
  (bind [this f]))
