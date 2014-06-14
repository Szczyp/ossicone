(ns nuejure.core)

(defn flip [f & [a b & rs]]
  (apply f b a rs))

(defn- make-curried [n f args]
  (if (= n 0)
    `(~f ~@args)
    (let [a# (gensym)]
      `(fn [~a#] ~(make-curried (dec n) f (conj args a#))))))

(defmacro curry
  ([n f] (make-curried n f []))
  ([f] (make-curried 2 f [])))
