(ns nuejure.state
  (:refer-clojure :exclude [get])
  (:require
   [nuejure.protocols :as p]))

(defn run [m s]
  #+clj ((.state-fn m) s)
  #+cljs (.state-fn m s))

(deftype State [state-fn]
  p/Functor
  (mapf [this f]
    (State.
     (fn [s]
       (let [[r s] (state-fn s)]
         [(f r) s]))))
  p/Applicative
  (return [this a] (State. (fn [s] [a s])))
  (ap [this a]
    (State.
     (fn [s]
       (let [[f s] (state-fn s)
             [r s] (run a s)]
         [(f r) s]))))
  p/Monad
  (join [this]
    (State.
     (fn [s]
       (let [[r s] (state-fn s)]
         (run r s))))))

(def state ->State)

(def get
  (state (fn [s] [s s])))

(defn modify [f]
  (state (fn [s]
           (let [s (f s)]
             [s s]))))

(defn put [s]
  (state (fn [_] [s s])))

(def m ->State)
