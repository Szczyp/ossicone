(defproject nuejure "0.1.0-SNAPSHOT"
  :description "FIXME: write description"

  :url "http://example.com/FIXME"

  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/clojurescript "0.0-3308"]]

  :source-paths ["src"]

  :test-paths ["test"]

  :cljsbuild {:test-commands {"test" ["node" "output/tests.js"]}
              :builds [{:id "test"
                        :source-paths ["src" "test"]
                        :notify-command ["node" "output/tests.js"]
                        :compiler {:output-to "output/tests.js"
                                   :output-dir "output"
                                   :static-fns true
                                   :source-map true
                                   :cache-analysis false
                                   :main nuejure.runner
                                   :optimizations :none
                                   :target :nodejs
                                   :pretty-print true}}]}

  :profiles {:dev {:plugins [[lein-cljsbuild "1.0.6"]
                             [com.cemerick/austin "0.1.6"]]}})
