(ns pg-train.routes.home
  (:require
    [ring.util.response :refer [response redirect status]]
    [pg-train.jwt :as jwt]
    [pg-train.template :as template]))


(defn home-page
  [req]
  (response (template/render "home.html" {})))

(defn wrap-home
  [handler]
  (fn [request]
    (let [name (jwt/payload-username request)]
      (if (= name "admin")
          (redirect "/admin")
          (handler request)))))

(def home-routes
  ["/home"
   {:middleware [jwt/wrap-jwt-authentication
   	             wrap-home]}
   ["" {:get home-page}]])