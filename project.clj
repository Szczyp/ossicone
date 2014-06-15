(defproject nuejure "0.1.0-SNAPSHOT"
  :description "FIXME: write description"

  :url "http://example.com/FIXME"

  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[org.clojure/clojure "1.6.0"]]
  
  :source-paths ["src" "target/classes"]

  :test-paths ["target/test-classes"]

  :profiles
  {:dev
   {:dependencies [[org.clojure/clojurescript "0.0-2227"]]

    :plugins [[com.keminglabs/cljx "0.4.0"]
              [lein-cljsbuild "1.0.3"]
              [com.cemerick/austin "0.1.4"]
              [com.cemerick/clojurescript.test "0.3.1"]]

    :hooks [cljx.hooks leiningen.cljsbuild]

    :cljx {:builds [{:source-paths ["src"]
                     :output-path "target/classes"
                     :rules :clj}
                    {:source-paths ["src"]
                     :output-path "target/classes"
                     :rules :cljs}
                    {:source-paths ["test"]
                     :output-path "target/test-classes"
                     :rules :clj}
                    {:source-paths ["test"]
                     :output-path "target/test-classes"
                     :rules :cljs}]}

    :cljsbuild {:builds [{:source-paths ["target/classes" "target/test-classes"]
                          :compiler {:output-to "target/testable.js"
                                     :optimizations :simple}}]
                :test-commands {"phantom" ["phantomjs" :runner "target/testable.js"]}}}})
