(ns lonelyplanet.model
  (:require [clojure.data.xml :as x]
            [clojure.zip :as z]
            [clojure.core.cache :as cache]
            [aprint.core :refer [aprint]]))

(defn gen-parser
  "Utility fn to create an xml stream of a supplied reader's data."
  [src]
  (x/parse src))

(defn walk-zipper
  "Walk a zipper & return a lazy seq of nodes, depth first."
  [loc]
  (take-while (complement z/end?) (iterate z/next loc)))

(defn walk&transform-zipper
  "Walk a zipper, mutating with f & walking the returned, mutated version"
  [loc f]
  (if (z/end? loc)
    loc
    (recur (z/next (f loc)) f)))

(defn to-html-list
  [loc]
  (let [link-text (-> loc z/children first :content)
        link (-> loc z/node (get-in [:attrs :geo_id]))
        children (-> loc z/children)]
    (-> (z/edit loc assoc :tag :ul :attrs {})
        (z/edit assoc :content [])
        (z/insert-child (x/map->Element {:tag     :li :attrs {:id link}
                                         :content (concat [(x/map->Element {:tag     :a
                                                                            :attrs   {:href (str link ".html")}
                                                                            :content link-text})]
                                                          children)})))))

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

(defn gen-route-parent
  "Generate a route to the top destination from the current destination"
  [loc acc]
  (let [parent (-> loc z/up z/up)
        loc-id (-> loc z/node :attrs :id)]
    (if parent (recur parent (cons loc-id acc)) (cons loc-id acc))))

(defn gen-route-child
  "Generate a route to the bottom destination from the current destination, taking the left-most child if any"
  [loc acc]
  (let [loc-id (-> loc z/node :attrs :id)
        child-test (-> loc z/down z/right)]
    (if child-test (recur (z/down child-test) (conj acc loc-id)) (conj acc loc-id ))))

(defn gen-route [loc]
  "Merge the parent route and the child route"
  (concat (gen-route-parent loc '()) (rest (gen-route-child loc []))))

(defn gen-meta
  "Generate destination meta-info for the view"
  [loc]
  (let [loc-id (-> loc z/node :attrs :id)
        loc-name (-> loc z/down z/down z/node)]
    [(Integer. ^String loc-id) {:route     (gen-route loc)
                                :place-id  loc-id
                                :placename loc-name}]))

(defn gen-routes
  "Uses the hierarchy of :ul and :li nodes to generate routes to any destination. If multiple children exist, uses first
  only. Returns a sorted map keyed by destination-id as an Integer."
  [hierarchy]
  (let [li-seq (filter #(-> % z/node :tag (= :li)) (-> hierarchy z/xml-zip walk-zipper))]
    (into (sorted-map) (map gen-meta li-seq))))

(defn generate-destinations
  "Given file readers of the taxonomy and the destination xml files, generate a combined structure holding data from
  both, suitable for rendering."
  [taxonomy destinations]
  (let [tax-zip (-> taxonomy gen-parser z/xml-zip)
        hierarchy (-> tax-zip z/next z/next (walk&transform-zipper transform-taxonomy-nodes)
                      z/root z/xml-zip z/next z/next (walk&transform-zipper prune-taxonomy-nodes)
                      z/root z/xml-zip z/next z/next z/node) ;; navigate to root :ul element
        dest-metas (-> hierarchy gen-routes)
        destinations (-> destinations gen-parser :content)]
    {:destinations (for [destination destinations]
                     (let [destination-id (-> destination :attrs :atlas_id)]
                       (assoc destination :meta (dest-metas (Integer. ^String destination-id)))))
     :dest-metas dest-metas
     :hierarchy hierarchy}))