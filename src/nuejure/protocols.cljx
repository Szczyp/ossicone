(ns nuejure.protocols
  #+clj (:import
         [clojure.lang PersistentList PersistentVector LazySeq
          PersistentHashMap PersistentArrayMap Fn Keyword]))

(defprotocol Functor
  (mapf [this f]))

(extend-protocol Functor
  #+clj PersistentList #+cljs List
  (mapf [this f] (apply list (map f this)))
  PersistentVector
  (mapf [this f] (mapv f this))
  LazySeq
  (mapf [this f] (map f this))
  PersistentHashMap
  (mapf [this f] (into (empty this) (map (fn [[k v]] [k (f v)]) this)))
  PersistentArrayMap
  (mapf [this f] (into (empty this) (map (fn [[k v]] [k (f v)]) this)))
  #+clj Fn #+cljs function
  (mapf [this f] (comp f this))
  Keyword
  (mapf [this f] (comp f this)))

(defprotocol Applicative
  (return [this a])
  (ap [this that]))

(extend-protocol Applicative
  #+clj PersistentList #+cljs List
  (return [this a] (list a))
  (ap [this that] (apply list (for [f this a that] (f a))))
  PersistentVector
  (return [this a] (vector a))
  (ap [this that] (vec (for [f this a that] (f a))))
  LazySeq
  (return [this a] (lazy-seq (list a)))
  (ap [this that] (for [f this a that] (f a)))
  #+clj Fn #+cljs function
  (return [this f] (constantly f))
  (ap [this that] #((this %) (that %)))
  Keyword
  (return [this f] (constantly f))
  (ap [this that] #((this %) (that %))))

(defprotocol Monad
  (join [this]))

(extend-protocol Monad
  #+clj PersistentList #+cljs List
  (join [this] (apply list (apply concat this)))
  PersistentVector
  (join [this] (vec (apply concat this)))
  LazySeq
  (join [this] (apply concat this))
  #+clj Fn #+cljs function
  (join [this] #((this %) %))
  Keyword
  (join [this] #((this %) %)))
