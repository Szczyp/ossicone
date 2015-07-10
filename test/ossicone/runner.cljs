(ns ossicone.runner
  (:require
   [cljs.test :refer-macros [run-tests] :refer [empty-env]]
   [ossicone.core-test]))

(enable-console-print!)

(defn main []
  (run-tests (empty-env) 'ossicone.core-test))

(set! *main-cli-fn* main)
