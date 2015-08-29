(ns lonelyplanet.model-test
  (:require [clojure.test :refer :all]
            [lonelyplanet.model :refer :all]
            [lonelyplanet.core :refer [gen-reader]]
            [clojure.zip :as z]
            [clojure.data.zip.xml :as zx]

            [aprint.core :refer [aprint]]))

(defn leaves
  "Transform tree zipper to sequence of leaf locations. Useful utility fn for testing."
  [loc]
  (filter (complement z/branch?) (take-while (complement z/end?) (iterate z/next loc))))

(deftest parse-taxonomy
  (let [tax-zip (-> (gen-reader "resources/test" "taxonomy.xml")
                     gen-parser
                     z/xml-zip)
        hrrchy (-> tax-zip z/next z/next (walk&transform-zipper transform-taxonomy-nodes)
                      z/root z/xml-zip z/next z/next (walk&transform-zipper prune-taxonomy-nodes)
                      z/root z/xml-zip z/next z/next z/node) ;; navigate to root :ul element
        dest-metas (-> hrrchy gen-routes)
        sa (dest-metas 355611)
        ]
    (testing "Destination meta info nodes count matches no. of destinations in test taxonomy"
      (is (= (count dest-metas) 24)))
    (testing "Parsing location information"
      (is (= (:placename sa) "South Africa"))
      (is (= (:place-id sa) "355611"))
      (is (= (:route sa) '("355064" "355611" "355612" "355613"))))))

(deftest transform-taxonomy
  (let [test-zip (-> (gen-reader "resources/test" "taxonomy.xml")
                     gen-parser
                     z/xml-zip)
        leaf-node-count (count (zx/xml-> test-zip :taxonomy :node leaves))
        test-node (z/node (nth (take-while (complement z/end?) (iterate z/next test-zip)) 13))]
    (testing "Identity transform of walker function leaves structure unchanged"
      (is (= (z/node (walk&transform-zipper test-zip identity)) (z/node test-zip))))
    (testing "Pruning taxonomy zipper before adding html tags leaves minimal structure"
      (is (= (z/node (walk&transform-zipper (-> test-zip z/next z/next) prune-taxonomy-nodes))
             #clojure.data.xml.Element{:tag :taxonomies, :attrs {},
                                       :content [#clojure.data.xml.Element{:tag :taxonomy, :attrs {},
                                                                           :content nil}]})))
    (testing "Transforming but not pruning taxonomy doubles of leaves from node_names"
      (is (= (+ 1 (* 2 leaf-node-count))
             (count (leaves (-> test-zip z/next z/next
                                (walk&transform-zipper transform-taxonomy-nodes)
                                z/root
                                z/xml-zip))))))
    (testing "Transformation of taxonomy node gives expected augmented structure"
      (is (= (-> (z/xml-zip test-node)
                 (walk&transform-zipper transform-taxonomy-nodes)
                 z/node)
             #clojure.data.xml.Element{:tag :ul,
                                       :attrs {},
                                       :content [#clojure.data.xml.Element{:tag :li,
                                                                           :attrs {:id "355613"},
                                                                           :content (#clojure.data.xml.Element{:tag :a,
                                                                                                               :attrs {:href "355613.html"},
                                                                                                               :content ("Table Mountain National Park")}
                                                                                      #clojure.data.xml.Element{:tag :node_name,
                                                                                                                :attrs {},
                                                                                                                :content ("Table Mountain National Park")})}]})))
    (testing "Transforming then pruning sample taxomony node gives expected structure"
      (is (= (-> (z/xml-zip test-node)
                 (walk&transform-zipper transform-taxonomy-nodes)
                 z/root z/xml-zip
                 (walk&transform-zipper prune-taxonomy-nodes)
                 z/node)
             #clojure.data.xml.Element{:tag :ul,
                                       :attrs {},
                                       :content [#clojure.data.xml.Element{:tag :li,
                                                                           :attrs {:id "355613"},
                                                                           :content [#clojure.data.xml.Element{:tag :a,
                                                                                                               :attrs {:href "355613.html"},
                                                                                                               :content ("Table Mountain National Park")}]}]})))))

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