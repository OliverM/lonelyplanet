(ns lonelyplanet.model-test
  (:require [clojure.test :refer :all]
            [lonelyplanet.model :refer :all]
            [lonelyplanet.core :refer [gen-reader]]

            [clojure.zip :as z]
            [clojure.data.xml :as x]
            [clojure.data.zip :as dz]
            [clojure.data.zip.xml :as zx]

            [aprint.core :refer [aprint]]))

(deftest parse-taxonomy
  (let [test-zip (-> (gen-reader "resources/test" "taxonomy.xml")
                     gen-parser
                     z/xml-zip)
        leaves (leaves test-zip)
        sa (location-meta (nth leaves 2))
        destination-metas (location-metas leaves)
        ]
    (testing "Leaf nodes count matches no. of leaf nodes in test taxonomy"
      (is (= (count leaves) 25)))
    (testing "Leaf nodes are nil or strings"
      (is (= (let [leaf (z/node (rand-nth leaves))]
               (or (string? leaf) (nil? leaf)))
             true)))
    (testing "Parsing location information"
      (is (= 355611 (first sa)))
      (is (= (:placename (second sa)) "South Africa"))
      (is (= (:place-id (second sa)) "355611"))
      (is (= (:route (second sa)) '("355611" "355064" nil nil))))
    (testing "Destination metadata lookup structure returns expected location metadata"
      (is (= (destination-metas 355611) (second sa))))))

(deftest generate-destinations-data
  (let [destinations (generate-destinations
                       (gen-reader "resources/test" "taxonomy.xml")
                       (gen-reader "resources/test" "destinations.xml"))]
    (testing "Expected number of destinations parsed"
      (is (= (count (:destinations destinations)) 24)))))                   ;; excludes 'World' found in taxonomy.xml

(deftest destination-ordering
  (let [destination-ids (->> (gen-reader "resources/test" "destinations.xml")
                             gen-parser
                             :content
                             (map (comp #(Integer. ^String %) :atlas_id :attrs)))]
    (testing "Assumption of unique & continually-increasing :atlas_id codes in destinations.xml"
      (is (= (apply < destination-ids) true)))))

(deftest destination-count
  (let [taxonomy-count (-> (gen-reader "resources/test" "taxonomy.xml")
                           gen-parser
                           z/xml-zip
                           leaves
                           count)
        destinations-count (-> (gen-reader "resources/test" "destinations.xml")
                               gen-parser
                               :content
                               count)]
    (testing "One extra destination in taxonomy vs destinations as it includes notional 'World' entry"
      (is (= 1 (- taxonomy-count destinations-count))))))