(ns pg-train.handler
  (:require 
    [reitit.ring :as ring]
    [pg-train.middleware :as middleware]))


(defn get-test
  [req]
  {:status 200 :body "test"})

(def app-routes
  (ring/ring-handler
    (ring/router
      ["/"
        ["" {:get get-test}]
      ])))

(def handler
  (middleware/wrap-base app-routes))