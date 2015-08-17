(ns lonelyplanet.core-test
  (:require [clojure.test :refer :all]
            [lonelyplanet.core :refer :all]

            [clojure.tools.cli :refer [parse-opts]]
            [clojure.string :as s]
            [aprint.core :refer (aprint)]))

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
    (is (string? (re-find #"search-string" (usage "search-string")))))
  (testing "Help option returns correct exit code."
    (is (= 0 (:status (-main "-h")))))
  (testing "Help options returns correct help text."
    (is (string? (re-find #"taxonomy" (:msg (-main "-h"))))))
  (testing "Unknown option generates error status code"
    (is (not= 0 (:status (-main "j")))))
  (testing "Wrong number of arguments caught"
    (is (not= 0 (:status (-main "arg1" "arg2" "spurious-arg")))))
  (testing "Alternative filenames for input files accepted" ;; this test is passing in practice...
    (is (= 0 (:status (-main "-t" "tax.txt" "--destinations" "dest.txt" "arg1" "arg2")))))
  )
