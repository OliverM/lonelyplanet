(ns lonelyplanet.model-test
  (:require [lonelyplanet.model :refer :all]
            [lonelyplanet.core :refer [gen-reader]]

            [clojure.zip :as z]
            [clojure.data.xml :as x]
            [clojure.data.zip :as dz]
            [clojure.data.zip.xml :as zx]))


(def testy (gen-parser (gen-reader "resources/test" "taxonomy.xml")))
(def tz (clojure.zip/xml-zip testy))
