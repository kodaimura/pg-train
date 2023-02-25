(ns pg-train.core
  (:gen-class)
  (:require 
    [postal.core :as postal]
    [ring.adapter.jetty9 :as jetty]
    [unilog.config :as unilog]
    [pg-train.handler :refer [handler]]
    [pg-train.config :as config]))


(defn run-server
  [config]
  (unilog/start-logging! (:logging config))
  (jetty/run-jetty 
      handler 
      {:host (get-in config [:webserver :host]) 
       :port (get-in config [:webserver :port])})) 

(defn -main
  [& args]
  (let [config (config/read-config :dev)]
    (run-server config)))