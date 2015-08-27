(ns lonelyplanet.view
  (:require [net.cgrand.enlive-html :refer :all]
            [clojure.zip :as z]
            [clojure.string :as s]
            [aprint.core :refer [aprint]]))

(def blank "static/example copy.html")

(defn gen-breadcrumb-step
  "Generate <li> fragments built from destination IDs (supplied as Strings) enclosing <a> elements to their
  corresponding pages. Assumes IDs used as page filenames."
  [step taxonomy]
  (when step (html [:li [:a {:href (str step ".html")} (-> (Integer. ^String step) taxonomy :placename)]])))

(defn make-title
  "Convert lowercase strings with underscores instead of spaces to capitalised strings"
  [string]
  (-> string (s/replace "_" " ") s/capitalize))

(defn depth
  "Determine depth in content hierarchy. 1 is the highest level."
  [loc]
  (count (z/path loc)))

(defn level-header
  "Generate a header keyword (in Hiccup format) based on a supplied hierarchy, with 1 returning a :h1, etc."
  [depth]
  (keyword (str "h" depth)))

(defn gen-content
  "Convert destination data (supplied as a lazy xml parser) into the main HTML content for the destination page."
  [destination]
  (let [destination (-> destination z/xml-zip z/down)       ;; z/down unwraps <destination> tag
        fragment-locs (take-while (complement z/end?) (iterate z/next destination))]
    (html (map (fn [loc]
                 (list (when (z/down loc) [(level-header (depth loc)) (-> loc z/node :tag name make-title)])
                       (when (not (z/down loc)) [:p (-> loc z/node)])))
               fragment-locs))))

(deftemplate destination blank [destination taxonomy hierarchy]
             [:div#header :h1] (content (get-in destination [:meta :placename]))

             ;; breadcrumb navigation
             [:div.secondary-navigation :ul] (content (map #(gen-breadcrumb-step % taxonomy)
                                                       (get-in destination [:meta :route])))
             [:div.secondary-navigation [:li first-of-type]] (add-class "first")


             [:div#sidebar :div.inner] (content hierarchy)
             [:div#main :div.inner] (content (gen-content destination)))

(defn render-destination
  "Generate a string of HTML from a supplied destination dataset and metadata taxonomy structure"
  [dest dest-metas hierarchy]
  (apply str (destination dest dest-metas hierarchy)))