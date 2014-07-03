(ns nuejure.effect
  (:require
   [nuejure.protocols :as p]
   [nuejure.core :refer [update]]))

(deftype Effect [effect-fn])

(def effect ->Effect)

(defn run
  ([m s]
     #+clj ((.effect-fn m) s)
     #+cljs (.effect-fn m s))
  ([m k v & kvs]
     (run m (apply hash-map k v kvs))))

(extend-type Effect
  p/Functor
  (mapf [this f] (effect (fn [s] (update (run this s) :result f))))
  p/Applicative
  (return [this a] (effect (fn [s] (assoc s :result a))))
  (ap [this a]
    (effect (fn [s]
              (let [{f :result :as s} (run this s)
                    s (run a s)]
                (update s :result f)))))
  p/Monad
  (join [this]
    (effect (fn [s]
              (let [{m :result :as s} (run this s)]
                (run m s))))))

(def env
  (effect (fn [s] (assoc s :result (:env s)))))

(def state
  (effect (fn [s] (assoc s :result (:state s)))))

(def env+state
  (effect (fn [s] (assoc s :result (merge (:env s) (:state s))))))

(defn put [ns]
  (effect (fn [s] (assoc s :state ns :result ns))))

(defn modify [f & args]
  (effect (fn [s]
            (let [ns (apply f (:state s) args)]
              (assoc s :state ns :result ns)))))

(defn out [m]
  (effect (fn [s]
            (-> (update s :out conj m)
                (assoc :result m)))))

(defn local [m & kvs]
  (effect (fn [s]
            (let [s (reduce (fn [s [k v]]
                              (update s k v))
                            s
                            (partition 2 kvs))]
              (assoc s :result (:result (run m s)))))))
