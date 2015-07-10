(ns ossicone.core-test
  (:require
   #?@(:clj  ([clojure.test :refer [deftest testing is]]
              [ossicone.core :refer [mlet mapf return traverse]])
       :cljs ([cljs.test :refer-macros [deftest testing is]]
              [ossicone.core :refer [mapf return traverse] :refer-macros [mlet]]))
   [ossicone.effect :refer [env state local log put modify run]]))

(deftest test-all
  (testing "monad test"
    (is (= -21 (let [f (mlet [a inc
                              b (partial * 2)]
                         (let [c (dec (- b a))]
                           (mlet [d (mapf + dec (partial * 3))
                                  e (return 1)]
                             (return (if (even? d)
                                       (+ a b c d e)
                                       (- e d c b a))))))]
                 (f 3)))))

  (testing "effect test"
    (is (= {:result {:a 3, :b 8} :state 4 :env 1 :log [3]}
           (let [m (mlet [e env
                          s state
                          l (local (mlet [e env]
                                     (return (inc e)))
                                   inc)]
                     (log s)
                     (put 1)
                     (mlet [s (modify + s)]
                       (return (+ e s l))))]
             (as-> (sorted-map :a (put 3) :b m) _
               (traverse _)
               (run _ :env 1 :state 20 :log [])))))))
