(ns pg-train.jwt
  (:require 
    [buddy.auth.backends.token :refer [jws-backend]]
    [buddy.auth.middleware :refer  [wrap-authorization wrap-authentication]]
    [buddy.sign.jwt :as jwt]
    [clj-time.core :as time]))


(def secret "secret")
(def auth-backend
  (jws-backend {:secret secret}))

(defn wrap-auth
  [handler]
  (-> handler
    (wrap-authorization auth-backend)
    (wrap-authentication auth-backend)))

(defn create-token
  [id username]
  (let [claims {:id id
                :username username
                :exp (time/plus (time/now) (time/days 30))}]
  	(jwt/sign claims secret)))