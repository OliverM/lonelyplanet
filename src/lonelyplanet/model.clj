(ns lonelyplanet.model
  (:require [clojure.data.xml :as x]
            [clojure.zip :as z]
            [clojure.data.zip.xml :as zx]))

(defn gen-parser
  "Utility fn to create an xml stream of a supplied reader's data."
  [src]
  (x/parse src))

;; repl capture
(def testy (gen-parser (gen-reader "resources/test" "taxonomy.xml")))
(def tz (clojure.zip/xml-zip testy))

(defn leaves [loc]
  (filter (complement z/branch?) (take-while (complement z/end?) (iterate z/next loc))))

(map #(->> % z/up z/up z/node :attrs :geo_id) (leaves tz))  ;; leaf ids
(map #(->> % zx/text) (leaves tz))                          ;; leaf names

;; get paths, then zip up into ref structure

