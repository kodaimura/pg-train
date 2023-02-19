(ns pg-train.template
  (:require
    [clojure.java.io]
    [selmer.parser :as parser]))


(parser/set-resource-path! 
  (clojure.java.io/resource "templates"))

(defn render
  [template params]
  (parser/render-file
    template
    params))