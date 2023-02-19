(ns pg-train.middleware
  (:require 
    [reitit.ring :as ring]
    [ring.util.response :refer [status]]
    [ring.middleware.params :refer [wrap-params]]
    [ring.logger :refer [wrap-log-request-params 
                         wrap-log-response
                         wrap-log-request-start]]
    [ring.middleware.keyword-params :refer [wrap-keyword-params]]
    [ring.middleware.json :refer [wrap-json-params wrap-json-response]]
    [ring.middleware.cookies :refer [wrap-cookies]]))

(defn wrap-base
  [handler]
  (-> handler
      (wrap-log-response)
      (wrap-log-request-params)
      (wrap-keyword-params)
      (wrap-params)
      (wrap-json-params)
      (wrap-json-response)
      (wrap-cookies)
      (wrap-log-request-start)))
      