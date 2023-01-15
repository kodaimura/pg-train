(ns pg-train.jwt
  (:require 
    [ring.util.response :refer [redirect status]]
    [buddy.sign.jwt :as jwt]
    [clj-time.core :as time]))


(def secret "secret")

(defn unauthorized-handler
  [request]
  (let [uri (:uri request)]
    (if (and (not (nil? uri)) (re-matches #"/api.*" uri))
        (status 401)
        (redirect "/login"))))

(defn wrap-jwt-authentication
  [handler]
  (fn [request]
    (let [token (get-in request [:cookies "token" :value])]
      (try
        (let [claims (jwt/unsign token secret)]
          (handler (assoc request :identity claims)))
        (catch Exception e
          (unauthorized-handler request))))))

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