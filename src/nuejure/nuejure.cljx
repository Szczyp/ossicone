(ns nuejure
  (:require
   [nuejure.core :as c]
   [nuejure.return :as r]
   [nuejure.functor :as f]
   [nuejure.applicative :as a]
   [nuejure.monad :as m :include-macros true]
   [nuejure.foldable :as l]
   [nuejure.traversable :as t]))

(def update c/update)

(def flip c/flip)

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
