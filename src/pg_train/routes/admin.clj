(ns pg-train.routes.admin
  (:require
    [ring.util.response :refer [response redirect status]]
    [pg-train.jwt :as jwt]
    [pg-train.template :as template]))


(defn admin-page
  [req]
  (response (template/render "admin.html" {})))

(defn wrap-admin
  [handler]
  (fn [request]
    (let [name (jwt/payload-username request)]
      (if (= name "admin") 
          (handler request)
          (redirect "/home")))))

(def admin-routes
  ["/admin"
   {:middleware [jwt/wrap-jwt-authentication 
   	             wrap-admin]}
   ["" {:get admin-page}]])