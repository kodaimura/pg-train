(ns pg-train.routes.user
  (:require
    [buddy.hashers :as hashers]
    [ring.util.response :refer [response redirect status]]
    [pg-train.jwt :as jwt]
    [pg-train.template :as template]
    [pg-train.models.user :as models.user]))


(defn login-page
  [req]
  (response (template/render "login.html" {})))

(defn login-post
  [{:keys [params]}]
  (let [name (:username params)
        pass (:password params)
        result (models.user/select-by-username name)
        user (if (empty? result) nil (first result))
        login-ok? (and (not (nil? user))
                       (hashers/check 
                        pass
                        (:users/password user)))]
    (if login-ok? 
        (let [token (jwt/create-token (:users/id user) name)]
          (assoc (response "SetCookie")
                 :cookies {"token" {:value token}}))
        (status 401))))

(defn signup-page
  [req]
  (response (template/render "signup.html" {})))

(defn signup-post
  [{:keys [params]}]
  (try (models.user/insert! (assoc (dissoc params :password)
                                   :password (hashers/derive (:password params))))
    (redirect "/login")
    (catch Exception _
      (redirect "/signup"))))

(defn logout
  [req]
  (assoc (redirect "/login")
         :cookies {"token" {:value ""}}))

(defn profile
  [req]
  (response (:identity req)))


(def account-routes
  [""
   {:middleware []}
   ["/login" {:get login-page
   	          :post login-post}]
   ["/signup" {:get signup-page
   	           :post signup-post}]
   ["/logout" {:get logout}]])

(def user-routes
  [""
   {:middleware [jwt/wrap-jwt-authentication]}
     ["/api/profile" {:get profile}]])