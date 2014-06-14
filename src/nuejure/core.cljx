(ns nuejure.core
  (:require
   [nuejure.protocols :as p]
   [nuejure.return :as r]))

(defn flip [f & [a b & rs]]
  (apply f b a rs))

(defn- make-curried [n f args]
  (if (= n 0)
    `(~f ~@args)
    (let [a# (gensym)]
      `(fn [~a#] ~(make-curried (dec n) f (conj args a#))))))

(defmacro curry
  ([n f] (make-curried n f []))
  ([f] (make-curried 2 f [])))

(def return r/->Return)

(defn ap
  ([f a] (if (r/return? a)
           (p/ap f (p/return f (#+clj .value #+cljs .-value a)))
           (p/ap f a)))
  ([f a & as] (apply ap (ap f a) as)))

(defn mapf
  ([f a] (p/mapf a f))
  ([f a & as] (apply ap (mapf f a) as)))

(defn bind
  ([m f]
     (let [coerce-return #(if (r/return? %)
                            (p/return m (#+clj .value #+cljs .-value %)) %)]
       (p/join (p/mapf m (comp coerce-return f)))))
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
