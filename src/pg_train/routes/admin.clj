(ns pg-train.routes.admin
  (:require
    [ring.util.response :refer [response redirect status]]
    [pg-train.jwt :as jwt]
    [pg-train.template :as template]
    [pg-train.models.question :as models.question]))


(defn admin-page
  [req]
  (response (template/render "admin.html" {})))

(defn questions-page
  [req]
  (let [questions (models.question/select-all)]
    (response (template/render "questions-admin.html"
                {:questions questions}))))

(defn question-page
  [req]
  (response (template/render "question-admin.html" {})))

(defn create-question!
  [{:keys [params]}]
  (try (models.question/insert! params)
    (redirect "/admin/questions")
    (catch Exception _
      (redirect "/login"))))

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
   ["" {:get admin-page}]
   ["/questions" {:get questions-page}]
   ["/questions/new" {:get question-page
   	                  :post create-question!}]])
   ;["/questions/:id" {:get question-page
   ;	                  :post update-question!}]])