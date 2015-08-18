(ns lonelyplanet.core-test
  (:require [clojure.test :refer :all]
            [lonelyplanet.core :refer :all]

            [clojure.tools.cli :as cli]
            [clojure.string :as s]
            [aprint.core :refer (aprint)]))

(defn parse-util
  "Utility testing function generating pipeline for supplied cli-options seq"
  [args]
  (->> (cli/parse-opts args cli-options) validate-invocation))

(defn cli-test-fixture
  [f]
  "Redefine the exit fn so it doesn't quit the JVM on completion, to allow testing."
  (with-redefs
       [exit (fn [status msg] {:status status :msg msg})]
       (try (f))))

(use-fixtures :once cli-test-fixture)

(deftest CLI-integration
  (testing "Exit fn mocked out."
    (is (= (exit 1 "test") {:status 1 :msg "test"})))
  (testing "Usage generates expected text"
    (is (= "search-string" (->> (usage "search-string")
                                (re-find #"search-string")))))
  (testing "Help option returns correct exit code."
    (is (= 0 (:status (parse-util ["-h"])))))
  (testing "Help options returns correct help text."
    (is ("taxonomy" (re-find #"taxonomy" (:msg (parse-util ["-h"]))))))
  (testing "Unknown option generates error status code"
    (is (not= 0 (:status (parse-util ["-j"])))))
  (testing "Wrong number of arguments caught"
    (is (not= 0 (:status (parse-util ["arg1" "arg2" "spurious-arg"])))))
  (testing "Alternative filenames for input files accepted" ;; nil as correct invocation so no exit generated
    (is (= nil (:status (parse-util ["-t" "tax.txt" "--destinations" "dest.txt" "arg1" "arg2"]))))))
