(defproject lonelyplanet "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [aprint "0.1.3"] ;; nicer printing of data at REPL
                 [org.clojure/data.xml "0.0.8"] ;; xml parsing
                 [org.clojure/data.zip "0.1.1"] ;; XML as zippers
                 [org.clojure/tools.cli "0.3.2"] ;; command line opt parsing
                 [enlive "1.1.6"] ;; HTML generation
                 [criterium "0.4.3"] ;; benchmarking
                 ]
  :plugins [[lein-bin "0.3.5"]
            [lein-marginalia "0.8.0"]]
  :bin {:name "lonely"}
  :main ^:skip-aot lonelyplanet.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
