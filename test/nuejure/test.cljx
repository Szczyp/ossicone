(ns nuejure.test
  #+clj (:require
         [clojure.test :as t :refer [is deftest with-test run-tests testing]]
         [nuejure.core :refer [mapf return ap bind mdo]]
         [nuejure.effect :refer [env state local out put modify run]])
  #+cljs (:require-macros
          [cemerick.cljs.test :refer [is deftest with-test run-tests testing]]
          [nuejure.core :refer [mdo]])
  #+cljs (:require
          [cemerick.cljs.test :as t]
          [nuejure.core :refer [mapf return ap bind]]
          [nuejure.effect :refer [env state local out put modify run]]))

(deftest a-test
  (testing "a test"
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
    (is (= {:result 16 :env 1 :state 15 :out [13]}
           (-> (mdo [e env
                     s state
                     s (local (put (+ s 11)))
                     s (out s)
                     s (modify + s)]
                    (return (+ e s)))
               (run :env 1 :state 2 :out []))))))
