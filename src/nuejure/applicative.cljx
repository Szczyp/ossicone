(ns nuejure.applicative
  (:require
   [nuejure.return :refer [return return? value]]
   [nuejure.functor :refer [mapf]])
  #+clj (:import
         [nuejure.return Return]
         [clojure.lang
          PersistentList PersistentVector LazySeq
          MapEntry PersistentHashMap PersistentArrayMap PersistentTreeMap
          Fn Keyword
          PersistentHashSet PersistentTreeSet]))

(defprotocol Applicative
  (return* [this a])
  (ap* [this that]))

(extend-protocol Applicative
  Return
  (return* [this a] (return a))
  (ap* [this a] (if (= (type a) Return)
                  (return ((value this) (value a)))
                  (ap* (return* a (value this)) a)))
  #+clj PersistentList #+cljs List
  (return* [this a] (list a))
  (ap* [this that] (apply list (for [f this a that] (f a))))
  PersistentVector
  (return* [this a] (vector a))
  (ap* [this that] (vec (for [f this a that] (f a))))
  LazySeq
  (return* [this a] (lazy-seq (list a)))
  (ap* [this that] (for [f this a that] (f a)))
  MapEntry
  (return* [[k _] a] (MapEntry. k a))
  (ap* [[k f] [_ v]] (MapEntry. k (f v)))
  PersistentHashMap
  (return* [this a] (apply hash-map a))
  (ap* [this that] (into (hash-map)
                        (for [[k f] this]
                          [k (f (k that))])))
  PersistentArrayMap
  (return* [this a] (apply hash-map a))
  (ap* [this that] (into (hash-map)
                        (for [[k f] this]
                          [k (f (k that))])))
  PersistentTreeMap
  (return* [this a] (apply sorted-map a))
  (ap* [this that] (into (sorted-map)
                        (for [[k f] this]
                          [k (f (k that))])))
  PersistentHashSet
  (return* [this a] (hash-set a))
  (ap* [this that] (apply hash-set (for [f this a that] (f a))))
  #+clj Fn #+cljs function
  (return* [this f] (constantly f))
  (ap* [this that] #((this %) (that %)))
  Keyword
  (return* [this f] (constantly f))
  (ap* [this that] #((this %) (that %))))

(defn ap [f & as]
  (letfn [(ap ([f a] (if (return? a)
                       (ap* f (return* f (value a)))
                       (ap* f a)))
            ([f a & as] (apply ap (ap f a) as)))
          (curry [n f] ((apply partial (replicate n partial)) f))]
    (apply ap (mapf (partial curry (count as)) f) as)))
