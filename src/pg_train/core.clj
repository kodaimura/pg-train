(ns pg-train.core
  (:require 
    [postal.core :as postal]
    [ring.adapter.jetty9 :as jetty]
    [unilog.config :as unilog]
    [pg-train.handler :refer [handler]]
    [pg-train.config :as config]
    [pg-train.env :refer [env]]))


(defn init-logging! 
  [config]
  (unilog/start-logging! (:logging config)))

(defn run
  [& args]
  (let [config (config/read-config :dev)]
    (init-logging! config)
    (jetty/run-jetty 
      handler 
      {:host (get-in config [:webserver :host]) 
       :port (get-in config [:webserver :port])})))