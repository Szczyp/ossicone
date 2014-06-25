(ns nuejure.test
  #+clj (:require
         [clojure.test :as t :refer [is deftest with-test run-tests testing]]
         [nuejure.core :refer [mapf return ap bind curry mdo]]
          [nuejure.reader :as reader]
         [nuejure.state :as state]
         [nuejure.app :as app])
  #+cljs (:require-macros
          [cemerick.cljs.test :refer [is deftest with-test run-tests testing test-var]]
          [nuejure.core :refer [curry mdo]])
  #+cljs (:require
          [cemerick.cljs.test :as t]
          [nuejure.core :refer [mapf return ap bind]]
          [nuejure.reader :as reader]
          [nuejure.state :as state]
          [nuejure.app :as app]))

(deftest a-test
  (testing "a test"
    (is (= -21 (let [f (mdo [a inc
                             b (partial * 2)
                             :let [c (dec (- b a))]
                             d (mapf (curry +) dec (partial * 3))
                             e (return 1)]
                            (if (even? d)
                              (+ a b c d e)
                              (- e d c b a)))]
                 (f 3)))))

  (testing "reader test"
    (is (= 1 (-> (mdo [e reader/ask
                       :let [a (inc e)]]
                      (dec a))
                 (reader/run 1)))))

  (testing "state test"
    (is (= [0 12] (-> (mdo [s state/get
                            _ (state/put 10)
                            :let [a (inc s)]
                            _ (state/modify (partial + a))]
                           (dec s))
                      (state/run 1)))))

  (testing "app test"
    (is (= {:result 16 :env 1 :state 15 :log [13]}
           (-> (mdo [e app/env
                     s app/state
                     s (app/run (app/put (+ s 11)))
                     s (app/log s)
                     s (app/modify (partial + s))]
                    (+ e s))
               (app/run-app :env 1 :state 2 :log []))))))
