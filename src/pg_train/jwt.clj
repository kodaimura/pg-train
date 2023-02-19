(ns pg-train.jwt
  (:require 
    [ring.util.response :refer [redirect status]]
    [buddy.sign.jwt :as jwt]
    [clj-time.core :as time]
    [pg-train.env :refer [env]]))


(def secret (env :jwt_secret))

(defn unauthorized-handler
  [request]
  (status 401))

(defn unsign
  [token]
  (try
    (jwt/unsign token secret)
    (catch Exception _
      false)))

(defn wrap-jwt-authentication
  [handler]
  (fn [request]
    (let [token (get-in request [:cookies "token" :value])
          claims (unsign token)]
      (if claims
          (handler (assoc request :identity claims))
          (unauthorized-handler request)))))

(defn create-token
  [id username]
  (let [claims {:id id
                :username username
                :exp (time/plus (time/now) (time/days 30))}]
  	(jwt/sign claims secret)))

(defn payload-username
  [request]
  (get-in request [:identity :username]))

(defn payload-id
  [request]
  (get-in request [:identity :id]))