(ns nuejure.return)

(deftype Return [value]
  Object
  (toString [this] (str value)))

(defn return? [a] (= (type a) Return))

(def return ->Return)

(defn value [r] (#+clj .value #+cljs .-value r))
