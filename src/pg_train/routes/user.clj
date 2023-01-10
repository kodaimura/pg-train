(ns pg-train.routes.user
  (:require
    [buddy.hashers :as hashers]
    [ring.util.response :refer [redirect]]
    [pg-train.template :as template]
    [pg-train.models.user :as models.user]))


(defn login
  [req]
  (template/render "login.html" {}))

(defn login-post
  [req]
  (let [params (req :params)
        user (models.user/select-by-username (:username params))]
    (if (and (not (empty? user))
             (hashers/check (:password params) 
                            (:users/password (first user))))
        (redirect "/")
        (redirect "/login"))))

(defn signup
  [req]
  (template/render "signup.html" {}))

(defn signup-post
  [req]
  (let [params (req :params)]
    (try (models.user/insert! (assoc (dissoc params :password)
                                 :password (hashers/derive (:password params))))
      (redirect "/login")
      (catch Exception _
        (redirect "/signup")))))


(def user-routes
  [""
   {:middleware []}
   ["/login" {:get  login
   	          :post login-post}]
   ["/signup" {:get signup
   	           :post signup-post}]])