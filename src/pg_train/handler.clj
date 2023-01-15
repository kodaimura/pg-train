(ns pg-train.handler
  (:require 
    [reitit.ring :as ring]
    [pg-train.routes.user :refer [account-routes user-routes]]
    [pg-train.routes.admin :refer [admin-routes]]
    [pg-train.routes.home :refer [home-routes]]
    [pg-train.middleware :as middleware]))


(def app-routes
  (ring/ring-handler
    (ring/router
      [account-routes user-routes admin-routes home-routes])
    (ring/routes
      (ring/create-resource-handler {:path "/"}))))

(def handler
  (middleware/wrap-base app-routes))