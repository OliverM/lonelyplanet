(ns lonelyplanet.model
  (:require [clojure.data.xml :as x]
            [clojure.zip :as z]
            [clojure.data.zip :as dz]
            [clojure.data.zip.xml :as zx]

            [aprint.core :refer [aprint]]))

(defn gen-parser
  "Utility fn to create an xml stream of a supplied reader's data."
  [src]
  (x/parse src))

(defn leaves
  "Transform tree zipper to sequence of leaf locations"
  [loc]
  (filter (complement z/branch?) (take-while (complement z/end?) (iterate z/next loc))))

(defn hierarchy
  "Get a seq of location IDs leading to this location from a zipper location"
  [loc]
  (lazy-seq (when loc (cons (-> loc z/node :attrs :geo_id) (hierarchy (z/up loc))))))

(defn placename
  "Pull place name from zipper location"
  [loc]
  (zx/text loc))

(defn place-id
  "Derive geo-id from taxonomy leaf location"
  [loc]
  (-> loc z/up z/up z/node :attrs :geo_id))

(defn location-meta
  "Generate location meta-info from leaf node location (containing location name). Returns pair of
  place-id (as integer) and dictionary of location meta-data."
  [loc]
  (let [location-id (place-id loc)
        location-id (or location-id "-1")]
    [(Integer. ^String location-id)
     {:placename (placename loc)
      :place-id  location-id
      :route     (-> loc
                     z/up                                   ;; up to node-name
                     z/up                                   ;; up to node
                     hierarchy)}]))

(defn location-metas
  "Generate all destination meta info from taxonomy leaf zipper locations"
  [taxonomy-locs]
  (into (sorted-map) (map location-meta taxonomy-locs)))

(defn generate-destinations
  "Given file readers of the taxonomy and the destination xml files, generate a combined structure holding data from
  both, suitable for rendering."
  [taxonomy destinations]
  (let [dest-metas (-> taxonomy gen-parser z/xml-zip leaves location-metas)
        destinations (-> destinations gen-parser :content)]
    {:destinations (for [destination destinations]
                     (let [destination-id (-> destination :attrs :atlas_id)]
                       (assoc destination :meta (dest-metas (Integer. ^String destination-id)))))
     :dest-metas dest-metas}))

(defn walk&transform-zipper
  "Walk a zipper, mutating with f & walking the returned, mutated version"
  [loc f]
  (if (z/end? loc)
    loc
    (recur (z/next (f loc)) f)))

(defn to-html-list
  "Convert a taxonomy node loc to a html list. When it encounters a :node element, change the :tag of the element to
  :ul, and add a child :li node containing a :a node with a :href attribute to the destination"
  [loc]
  (let [link-text (-> loc z/children first :content)
        link (-> loc z/node (get-in [:attrs :geo_id]))
        link (str link ".html")]
    (-> (z/edit loc assoc :tag :ul)
        (z/append-child (x/map->Element {:tag     :li :attrs {}
                                         :content [(x/map->Element {:tag :a :attrs {:href link} :content link-text})]})))))

(defn transform-taxonomy-nodes
  "Transform a taxonomy zipper from the xml tag-based structure to an enlive-html tag-based structure suitable
  for rendering to HTML by enlive. Assumes it's operating from a root node tag, not the root <taxonomies> tag."
  [loc]
  (if (= (-> loc z/node :tag) :node) (to-html-list loc) loc))

(defn prune-taxonomy-nodes
  "Remove nodes not in the set of :ul, :li or :a element types, or is a string."
  [loc]
  (if (or (#{:ul :li :a nil} (-> loc z/node :tag))
          (string? (-> loc z/node)))
    loc
    (z/remove loc)))