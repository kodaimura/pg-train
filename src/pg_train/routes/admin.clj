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
  (let [questions (models.question/select-all-admin)]
    (response (template/render "questions-admin.html"
                {:questions questions}))))

(defn question-page
  [{:keys [path-params]}]
  (if (= "new" (:id path-params))
      (response (template/render "question-admin.html" {}))
      (let [question (models.question/select-by-id (:id path-params))]
        (println path-params question)
        (if (empty? question)
            (response (template/render "question-admin.html" {}))
            (response (template/render "question-admin.html" 
                        {:question (first question)}))))))

(defn register-question!
  [{:keys [path-params params]}]
  (try
  	(if (= "new" (:id path-params))
  	    (models.question/insert! params)
  	    (models.question/update! params))
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
   ; :id が new の時は新規登録
   ["/questions/:id" {:get question-page
   	                  :post register-question!}]])