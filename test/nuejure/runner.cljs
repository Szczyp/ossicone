(ns nuejure.runner
  (:require
   [cljs.test :refer-macros [run-tests] :refer [empty-env]]
   [nuejure.core-test]))

(enable-console-print!)

(defn main []
  (run-tests (empty-env) 'nuejure.core-test))

(set! *main-cli-fn* main)

