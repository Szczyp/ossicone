(ns nuejure.test
  #+clj (:require
         [clojure.test :as t :refer [is deftest with-test run-tests testing]]
         [nuejure.core :refer [mlet mdo mapf return ap bind traverse]]
         [nuejure.effect :refer [env state local out put modify run]])
  #+cljs (:require-macros
          [cemerick.cljs.test :refer [is deftest with-test run-tests testing]]
          [nuejure.core :refer [mlet mdo]])
  #+cljs (:require
          [cemerick.cljs.test :as t]
          [nuejure.core :refer [mapf return ap bind traverse]]
          [nuejure.effect :refer [env state local out put modify run]]))

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
    (is (= {:result {:a 3 :b 16} :env 1 :state 15 :out [14]}
           (let [m (mlet [e env
                          s state
                          s (local (put (+ s 11)))]
                     (out s)
                     (put 1)
                     (mlet [s (modify + s)]
                       (return (+ e s))))]
             (as-> (sorted-map :a (put 3) :b m) _
                   (traverse _)
                   (run _ :env 1 :state 2 :out [])))))))
