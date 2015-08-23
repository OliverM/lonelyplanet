(ns lonelyplanet.model
  (:require [clojure.data.xml :as x]
            [clojure.zip :as z]
            [clojure.data.zip :as dz]
            [clojure.data.zip.xml :as zx]))

(defn gen-parser
  "Utility fn to create an xml stream of a supplied reader's data."
  [src]
  (x/parse src))

(defn leaves
  "Transform tree zipper to sequence of leaves"
  [loc]
  (filter (complement z/branch?) (take-while (complement z/end?) (iterate z/next loc))))

(defn hierarchy
  "Get a seq of location IDs leading to this location from a zipper loc"
  [loc]
  (lazy-seq (when loc (cons (-> loc z/node :attrs :geo_id) (hierarchy (z/up loc))))))

(defn placename
  "Pull place name from zipper loc"
  [loc]
  (zx/text loc))

(defn place-id
  "Derive geo-id from leaf node"
  [loc]
  (-> loc z/up z/up z/node :attrs :geo_id))

(defn location-meta
  "Generate location meta-info from leaf node location (containing location name)"
  [loc]
  {:placename (placename loc)
   :place-id (place-id loc)
   :route (-> loc
              z/up                                          ;; up to node-name
              z/up                                          ;; up to node
              hierarchy)})

