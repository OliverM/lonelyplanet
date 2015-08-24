(ns lonelyplanet.view
  (:require [net.cgrand.enlive-html :refer :all]))

(def blank "example copy.html")

(deftemplate destination blank [data]
   [:title] (content "Testing"))
