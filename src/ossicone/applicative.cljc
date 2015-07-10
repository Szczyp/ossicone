(ns ossicone.applicative
  (:require
   [ossicone.functor :refer [mapf Functor]])
  #?(:clj (:import
           [clojure.lang
            PersistentList PersistentVector LazySeq
            MapEntry PersistentHashMap PersistentArrayMap PersistentTreeMap
            Fn Keyword
            PersistentHashSet PersistentTreeSet])))

(deftype Return [value]
  Object
  (toString [this] (str value)))

(defprotocol Applicative
  (return* [this a])
  (ap* [this that]))

(defn return? [a] (= (type a) Return))

(def return ->Return)

(defn value [r] (#?(:clj .value :cljs .-value) r))

(defn coerce-return [a b]
  (case [(return? a) (return? b)]
    [true false] [(return* b (value a)) b]
    [false true] [a (return* a (value b))]
    [a b]))

(extend-type Return
  Functor
  (mapf* [this f] (return (f (value this)))))

(extend-protocol Applicative
  Return
  (return* [this a] (return a))
  (ap* [this a] (return ((value this) (value a))))
  #?(:clj PersistentList :cljs List)
  (return* [this a] (list a))
  (ap* [this that] (apply list (for [f this a that] (f a))))
  PersistentVector
  (return* [this a] (vector a))
  (ap* [this that] (vec (for [f this a that] (f a))))
  LazySeq
  (return* [this a] (lazy-seq (list a)))
  (ap* [this that] (for [f this a that] (f a)))
  #?(:clj MapEntry)
  #?(:clj (return* [[k _] a] (MapEntry. k a)))
  #?(:clj (ap* [[k f] [_ v]] (MapEntry. k (f v))))
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
  #?(:clj Fn :cljs function)
  (return* [this f] (constantly f))
  (ap* [this that] #((this %) (that %)))
  Keyword
  (return* [this f] (constantly f))
  (ap* [this that] #((this %) (that %))))

(defn ap [f & as]
  (letfn [(ap ([f a] (apply ap* (coerce-return f a)))
            ([f a & as] (reduce ap f (cons a as))))
          (curry [n f] ((apply partial (replicate n partial)) f))]
    (apply ap (mapf (partial curry (count as)) f) as)))
