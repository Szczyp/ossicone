(ns nuejure
  (:require [nuejure.core :as c]
            [nuejure.protocols :as p]
            [nuejure.return :as r]))

(defmacro curry
  ([n f] `(c/curry ~n ~f))
  ([f] `(c/curry ~f)))

(def flip c/flip)

(def return r/->Return)

(defn ap
  ([f a] (if (r/return? a)
           (p/ap f (p/return f (.value a)))
           (p/ap f a)))
  ([f a & as] (apply ap (ap f a) as)))

(defn mapf
  ([f a] (p/mapf a f))
  ([f a & as] (apply ap (mapf f a) as)))

(defn bind
  ([m f]
     (let [coerce-return #(if (r/return? %) (p/return m (.value %)) %)]
       (p/join (p/mapf m (comp coerce-return f)))))
  ([m f & fs] (apply bind (bind m f) fs)))

(defmacro mdo
  [bindings body]
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

(comment
  (ns test
    #+cljs (:require-macros [nuejure :refer [curry]])
    (:require [nuejure :refer [#+clj curry flip mapf return ap bind mdo]])))
