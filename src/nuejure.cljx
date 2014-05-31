(ns nuejure
  (:refer-clojure :exclude [map])
  (:require [nuejure.core :as c]
            [nuejure.protocols :as p]))

(defmacro curry
  ([n f] `(c/curry ~n ~f))
  ([f] `(c/curry ~f)))

(def flip c/flip)

(def lazy-map c/lazy-map)

(defn ap
  ([f a] (p/ap f a))
  ([f a & as] (apply ap (p/ap f a) as)))

(defn map
  ([f a] (p/map a f))
  ([f a & as] (apply ap (map f a) as)))

(comment
  (ns test
    (:refer-clojure :exclude [map])
    #+cljs (:require-macros [nuejure :refer [curry]])
    (:require [nuejure :refer [#+clj curry flip lazy-map ap map]])))
