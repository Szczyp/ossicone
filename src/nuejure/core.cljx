(ns nuejure.core
  (:require
   [nuejure.return :as r]
   [nuejure.functor :as f]
   [nuejure.applicative :as a]
   [nuejure.monad :as m :include-macros true]
   [nuejure.foldable :as l]
   [nuejure.traversable :as t]))

(defn update
  ([m k f] (assoc m k (f (get m k))))
  ([m k f x1] (assoc m k (f (get m k) x1)))
  ([m k f x1 x2] (assoc m k (f (get m k) x1 x2)))
  ([m k f x1 x2 & xs] (assoc m k (apply f (get m k) x1 x2 xs))))

(def return r/return)

(defn mapf
  ([f a] (f/mapf f a))
  ([f a & as] (apply a/ap (f/mapf (partial partial f) a) as)))

(def ap a/ap)

(def bind m/bind)

(defmacro mdo [bindings body]
  `(m/mdo ~bindings ~body))

(def fold l/fold)

(def traverse t/traverse)
