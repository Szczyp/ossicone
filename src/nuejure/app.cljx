(ns nuejure.app
  (:require
   [nuejure.protocols :as p]))

(defn run-app
  ([m s]
     #+clj ((.app-fn m) s)
     #+cljs (.app-fn m s))
  ([m k v & kvs]
     (run-app m (apply hash-map k v kvs))))

(deftype App [app-fn]
  p/Functor
  (mapf [this f]
    (App.
     (fn [s]
       (let [{r :result :as s} (app-fn s)]
         (update-in s [:result] f)))))
  p/Applicative
  (return [this a] (App. (fn [s] (assoc s :result a))))
  (ap [this a]
    (App.
     (fn [s]
       (let [{f :result :as s} (app-fn s)
             s (run-app a s)]
         (update-in s [:result] f)))))
  p/Monad
  (join [this]
    (App.
     (fn [s]
       (let [{m :result :as s} (app-fn s)]
         (run-app m s))))))

(def app ->App)

(def env
  (app (fn [s] (assoc s :result (:env s)))))

(def state
  (app (fn [s] (assoc s :result (:state s)))))

(defn put [ns]
  (app (fn [s] (assoc s :state ns :result ns))))

(defn modify [f & args]
  (app (fn [s]
         (let [ns (apply f (:state s) args)]
           (assoc s :state ns :result ns)))))

(defn log [m]
  (app (fn [s]
         (-> (update-in s [:log] conj m)
             (assoc :result m)))))

(defn run [m]
  (app (fn [s]
         (assoc s :result (:result (run-app m s))))))

(def m ->App)
