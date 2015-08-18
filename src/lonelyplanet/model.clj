(ns lonelyplanet.model
  (:require [clojure.data.xml :as x]
            [clojure.data.zip.xml :as zx]))

(defn gen-parser
  "Utility fn to create an xml stream of a supplied reader's data."
  [src]
  (x/parse src))
