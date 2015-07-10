(ns ossicone.core
  #?(:cljs (:require-macros [ossicone.core :refer [mlet f-> f->>]]))
  (:require
   [ossicone.functor :as f]
   [ossicone.applicative :as a]
   [ossicone.monad :as m]
   [ossicone.foldable :as l]
   [ossicone.traversable :as t]))

(def return a/return)

(defn mapf
  ([f a] (f/mapf f a))
  ([f a & as] (apply a/ap (f/mapf (partial partial f) a) as)))

(def ap a/ap)

(def bind m/bind)

(def fold l/fold)

(def traverse t/traverse)

(defn compm [f & fs]
  (let [[f & fs] (reverse (cons f fs))]
    (fn [a] (apply bind (f a) fs))))

(defn mdo [& ms]
  (reduce #(mapf (fn [_ a] a) % %2) (return nil) ms))

(do #?@(:clj
        ((defmacro mlet [bindings & body]
           (if (and (vector? bindings) (even? (count bindings)))
             (if (seq bindings)
               (let [[sym val] bindings
                     cont `(mlet ~(subvec bindings 2) ~@body)]
                 `(bind ~val (fn [~sym] ~cont)))
               `(mdo ~@body))
             (throw (IllegalArgumentException.
                     "bindings has to be a vector with even number of elements."))))

         (defmacro f->> [f & transforms]
           `(mapf #(->> % ~@transforms) ~f))

         (defmacro f-> [f & transforms]
           `(mapf #(-> % ~@transforms) ~f)))))
