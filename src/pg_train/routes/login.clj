(ns pg-train.routes.login
  (:require
    [pg-train.template :as template]))


(defn login
  [req]
  (template/render "login.html" {}))

(def login-routes
  [""
   {:middleware []}
   ["/login" {:get  login}]])