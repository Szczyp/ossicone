(ns nuejure.test
  #+clj (:require
         [clojure.test :as t :refer [is deftest with-test run-tests testing]]
         [nuejure.core :refer [mapf return ap bind mdo traverse]]
         [nuejure.effect :refer [env state local out put modify run]])
  #+cljs (:require-macros
          [cemerick.cljs.test :refer [is deftest with-test run-tests testing]]
          [nuejure.core :refer [mdo]])
  #+cljs (:require
          [cemerick.cljs.test :as t]
          [nuejure.core :refer [mapf return ap bind traverse]]
          [nuejure.effect :refer [env state local out put modify run]]))

(deftest test-all
  (testing "monad test"
    (is (= -21 (let [f (mdo [a inc
                             b (partial * 2)
                             :let [c (dec (- b a))]
                             d (mapf + dec (partial * 3))
                             e (return 1)]
                            (return (if (even? d)
                                      (+ a b c d e)
                                      (- e d c b a))))]
                 (f 3)))))

  (testing "effect test"
    (is (= {:result {:a 3 :b 18} :env 1 :state 17 :out [14]}
           (as-> (mdo [e env
                       s state
                       s (local (put (+ s 11)))
                       _ (out s)
                       s (modify + s)]
                      (return (+ e s))) _
                (sorted-map :a (put 3) :b _)
                (traverse _)
                (run _ :env 1 :state 2 :out []))))))
