(ns lonelyplanet.view-test
  (:require [clojure.test :refer :all]
            [lonelyplanet.view :refer :all]))

(defn lv
  "Quick utility function to create html pages from destination data, saved
  into the resources directory to pick up static/all.css"
  [data]
  (spit "resources/test.html" (reduce str (destination data))))
