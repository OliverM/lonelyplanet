(ns lonelyplanet.view-test
  (:require [clojure.test :refer :all]
            [lonelyplanet.view :refer :all]

            [net.cgrand.enlive-html :refer :all]
            [clojure.zip :as z]))

;(def sa-content (assoc (second (:content (gen-parser (gen-reader "resources/test" "destinations.xml"))))
;                  :meta {:placename "South Africa"
;                         :place-id "355611"
;                         :route '("355611" "355064" nil nil)}))


(defn lv
  "Quick utility function to create html pages from destination data, saving
  into the resources directory to pick up static/all.css"
  [data]
  (spit "resources/test.html" (reduce str (destination
                                            data
                                            {355611 {:placename "South Africa"}
                                             355064 {:placename "Africa"}
                                             -1 {:placename "World"}}))))

(deftest util-fn-tests
  (testing "Testing xml tag name to title string conversion"
    (is (= (make-title "one_underscored_string") "One underscored string")))
  (testing "Level-appropriate header function returns correct hiccup header keyword"
    (is (= (level-header 4) :h4)))
  )

(deftest hierarchy-to-html-navigation
  (let [mock-taxonomy {355611 {:placename "South Africa"}
                       355064 {:placename "Africa"}
                       -1 {:placename "World"}}
        ]
    (testing "Breadcrumb steps convert into expected hiccup list item & link"
      (is (= (gen-breadcrumb-step "355064" mock-taxonomy)
             '({:tag     :li,
                :attrs   {},
                :content ({:tag     :a,
                           :attrs   {:href "355064.html"},
                           :content ("Africa")})})))
      (is (= (gen-breadcrumb-step nil mock-taxonomy)
             nil)))))