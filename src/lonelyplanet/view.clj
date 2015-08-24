(ns lonelyplanet.view
  (:require [net.cgrand.enlive-html :refer :all]))

(def blank "static/example copy.html")

(defn gen-hierarchy-step
  "Generate <li> fragments built from destination IDs (supplied as Strings) enclosing <a> elements to their
  corresponding pages. Assumes IDs used as page filenames."
  [step taxonomy]
  (when step (html [:li [:a {:href (str step ".html")} (-> (Integer. ^String step) taxonomy :placename)]])))

(defn gen-content
  "Convert destination data (supplied as a lazy xml parser) into the main HTML content for the destination page."
  [destination]
  (html [:h1 "Test only"] [:p "Testing, just testing."])
  )

(deftemplate destination blank [destination taxonomy]
             [:div#header :h1] (content (get-in destination [:meta :placename]))
             [:div.secondary-navigation :a] (content (get-in destination [:meta :placename]))
             [:div#sidebar :div.inner] (content (html [:ul (map
                                                             #(gen-hierarchy-step % taxonomy)
                                                             (get-in destination [:meta :route]))]))
             [:div#main :div.inner] (content (gen-content destination)))
