(ns pg-train.routes.admin
  (:require
    [ring.util.response :refer [response redirect status]]
    [pg-train.jwt :as jwt]
    [pg-train.template :as template]
    [pg-train.models.question :as models.question]
    [pg-train.models.answer :as models.answer]))


(defn admin-page
  [req]
  (response (template/render "admin.html" {})))

(defn questions-page
  [req]
  (let [questions (models.question/select-all-admin)]
    (response (template/render "admin-questions.html"
                {:questions questions}))))

(defn answers-page
  [{:keys [params]}]
  (let [answers (models.answer/select-uqa params)]
    (response (template/render "admin-answers.html"
                {:answers answers}))))

(defn comment-page
  [{:keys [path-params]}]
  (let [answer (models.answer/select-by-question_id_and_user_id
                (:question_id path-params) (:user_id path-params))
        question (models.question/select-by-id (:question_id path-params))]
    (if (or (empty? answer) (empty? question))
        (redirect "/login")
        (response (template/render "admin-comment.html"
                    {:answer (first answer) :question (first question)})))))

(defn question-page
  [{:keys [path-params]}]
  (if (= "new" (:id path-params))
      (response (template/render "admin-question.html" {}))
      (let [question (models.question/select-by-id (:id path-params))]
        (if (empty? question)
            (response (template/render "admin-question.html" {}))
            (response (template/render "admin-question.html" 
                        {:question (first question)}))))))

(defn register-question!
  [{:keys [path-params params]}]
  (try
  	(if (= "new" (:id path-params))
  	    (models.question/insert! params)
  	    (models.question/update! params path-params))
    (redirect "/admin/questions")
    (catch Exception _
      (redirect "/login"))))

(defn register-comment!
  [{:keys [path-params params]}]
  (try
  	(models.answer/update! params path-params)
    (status 200)
    (catch Exception _
      (status 500))))

(defn settled!
  [{:keys [path-params]}]
  (try
  	(models.answer/update! {:help_flg "0"} path-params)
    (status 200)
    (catch Exception _
      (status 500))))

(defn reaction!
  [{:keys [path-params]}]
  (try
    (models.answer/update! {:reaction_flg "1"} path-params)
    (status 200)
    (catch Exception _
      (status 500))))

(defn wrap-admin
  [handler]
  (fn [request]
    (let [name (jwt/payload-username request)]
      (if (= name "admin") 
          (handler request)
          (redirect "/")))))

(def admin-routes
  ["/admin"
   {:middleware [jwt/wrap-jwt-authentication 
   	             wrap-admin]}
   ["" {:get admin-page}]
   ["/questions" {:get questions-page}]
   ; :id が new の時は新規登録
   ["/questions/:id" {:get question-page
   	                  :post register-question!}]
   ["/answers" {:get answers-page}]
   ["/answers/:question_id/:user_id" {:get comment-page}]
   ["/answers/:question_id/:user_id/comment" {:post register-comment!}]
   ["/answers/:question_id/:user_id/settled" {:post settled!}]
   ["/answers/:question_id/:user_id/reaction" {:post reaction!}]])