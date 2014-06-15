(ns nuejure.test
  #+clj (:require
         [clojure.test :as t :refer [is deftest with-test run-tests testing]]
         [nuejure.core :refer [mapf return ap bind curry mdo]]
         [nuejure.state :as state])
  #+cljs (:require-macros
          [cemerick.cljs.test :refer [is deftest with-test run-tests testing test-var]]
          [nuejure.core :refer [curry mdo]])
  #+cljs (:require
          [cemerick.cljs.test :as t]
          [nuejure.core :refer [mapf return ap bind]]
          [nuejure.state :as state]))

(deftest a-test
  (testing "a test"
    (is (= -21 (let [f (mdo [a inc
                             b (partial * 2)
                             :let [c (dec (- b a))]
                             d (mapf (curry +) dec (partial * 3))
                             e (return 1)]
                            (if (even? d)
                              (return (+ a b c d e))
                              (return (- e d c b a))))]
                 (f 3)))))

  (testing "state test"
    (is (= [0 12] (-> (mdo [s state/get
                            _ (state/put 10)
                            :let [a (inc s)]
                            _ (state/modify (partial + a))]
                           (return (dec s)))
                      (state/run 1))))))
