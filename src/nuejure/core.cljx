(ns nuejure.core
  (:require
   [nuejure.protocols :as p]
   [nuejure.return :as r]))

(defn flip [f & [a b & rs]]
  (apply f b a rs))

(def return r/->Return)

(defn ap [f & as]
  (letfn [(ap ([f a] (if (r/return? a)
                       (p/ap f (p/return f (#+clj .value #+cljs .-value a)))
                       (p/ap f a)))
            ([f a & as] (apply ap (ap f a) as)))
          (curry [n f] ((apply partial (replicate n partial)) f))]
    (apply ap (p/mapf f (partial curry (count as))) as)))

(defn mapf
  ([f a] (p/mapf a f))
  ([f a & as] (apply ap (mapf (partial partial f) a) as)))

(defn bind
  ([m f]
     (let [coerce-return #(if (r/return? %)
                            (p/return m (#+clj .value #+cljs .-value %)) %)]
       (p/join (mapf (comp coerce-return f) m))))
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
