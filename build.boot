(set-env!
 :source-paths   #{"src" "test"}
 :resource-paths   #{"src"}
 :dependencies '[[org.clojure/clojure "1.7.0"]
                 [org.clojure/clojurescript "1.7.48"]
                 [adzerk/boot-cljs "0.0-3308-0" :scope "test"]
                 [adzerk/boot-test "1.0.4" :scope "test"]])

(require '[adzerk.boot-cljs :refer [cljs]]
         '[adzerk.boot-test :refer [test]])

(task-options!
 test {:namespaces #{'ossicone.core-test}})

(deftask test-all []
  (comp (test)))

(deftask build []
  (comp (pom :project 'ossicone
          :version "0.1.0-SNAPSHOT")
     (jar)
     (install)))
