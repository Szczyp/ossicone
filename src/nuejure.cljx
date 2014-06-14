(ns nuejure
  (:require [nuejure.core :as c]
            [nuejure.protocols :as p]))

(defmacro curry
  ([n f] `(c/curry ~n ~f))
  ([f] `(c/curry ~f)))

(def flip c/flip)

(def return p/->Return)

(defn ap
  ([f a] (if (p/return? a)
           (p/ap f (p/return f (.value a)))
           (p/ap f a)))
  ([f a & as] (apply ap (ap f a) as)))

(defn mapf
  ([f a] (p/mapf a f))
  ([f a & as] (apply ap (mapf f a) as)))

(defn bind
  ([m f]
     (let [coerce-return #(if (p/return? %) (p/return m (.value %)) %)]
       (p/join (p/mapf m (comp coerce-return f)))))
  ([m f & fs] (apply bind (bind m f) fs)))

(defmacro mdo
  [bindings body]
  (if (and (vector? bindings) (even? (count bindings)))
    (if (seq bindings)
      (let [[sym monad] bindings]
        `(bind ~monad
               (fn [~sym]
                 (mdo ~(subvec bindings 2) ~body))))
      body)
    (throw (IllegalArgumentException.
            "bindings has to be a vector with even number of elements."))))

(comment
  (ns test
    #+cljs (:require-macros [nuejure :refer [curry]])
    (:require [nuejure :refer [#+clj curry flip ap mapf bind mdo return]])))
