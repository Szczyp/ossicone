(ns nuejure.reader
  (:require
   [nuejure.protocols :as p]))

(defn run [m e]
  #+clj ((.reader-fn m) e)
  #+cljs (.reader-fn m e))

(deftype Reader [reader-fn]
  p/Functor
  (mapf [this f]
    (Reader. (comp f reader-fn)))
  p/Applicative
  (return [this a] (Reader. (constantly a)))
  (ap [this a]
    (Reader.
     (fn [e]
       (let [f (reader-fn e)
             r (run a e)]
         (f r)))))
  p/Monad
  (join [this]
    (Reader.
     (fn [e]
       (let [r (reader-fn e)]
         (run r e))))))

(def reader ->Reader)

(def ask
  (reader identity))

(def m ->Reader)
