(ns pg-train.middleware
  (:require 
    [reitit.ring :as ring]
    [ring.middleware.params :refer [wrap-params]]
    [ring.logger :refer [wrap-log-request-params 
                         wrap-log-response
                         wrap-log-request-start]]
    [ring.middleware.keyword-params :refer [wrap-keyword-params]]))


(defn wrap-base
  [handler]
  (-> handler
      (wrap-log-response)
      (wrap-log-request-params)
      (wrap-keyword-params)
      (wrap-params)
      (wrap-log-request-start)))