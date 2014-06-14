(ns nuejure.return
  (:require [nuejure.protocols :as p]))

(deftype Return [value]
  p/Functor
  (mapf [this f] (Return. (f value)))
  p/Applicative
  (return [this a] (Return. a))
  (ap [this a] (if (= (type a) Return)
                   (Return. (value (.value a)))
                   (p/ap (p/return a value) a)))
  p/Monad
  (join [this] (.value this))
  Object
  (toString [this] (str value)))

(defn return? [a] (= (type a) Return))
