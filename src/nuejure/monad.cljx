(ns nuejure.monad
  (:require
   [nuejure.return :refer [return? value]]
   [nuejure.functor :refer [mapf]]
   [nuejure.applicative :refer [return*]])
  (:import
   [nuejure.return Return]
   #+clj [clojure.lang
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
  ([m f]
     (let [coerce-return #(if (return? %)
                            (return* m (#+clj .value #+cljs .-value %)) %)]
       (join* (mapf (comp coerce-return f) m))))
  ([m f & fs] (apply bind (bind m f) fs)))

(defmacro mdo [bindings body]
  (if (and (vector? bindings) (even? (count bindings)))
    (if (seq bindings)
      (let [[sym val] bindings
            cont `(mdo ~(subvec bindings 2) ~body)]
        (if (= sym :let)
          `(let ~val ~cont)
          `(bind ~val (fn [~sym] ~cont))))
      body)
    (throw (IllegalArgumentException.
            "bindings has to be a vector with even number of elements."))))
