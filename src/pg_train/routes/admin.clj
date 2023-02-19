(ns pg-train.routes.admin
  (:require
    [ring.util.response :refer [response redirect status]]
    [buddy.hashers :as hashers]
    [pg-train.jwt :as jwt]
    [pg-train.template :as template]
    [pg-train.models.question :as models.question]
    [pg-train.models.answer :as models.answer]
    [pg-train.models.user :as models.user]))


(defn admin-page
  [req]
  (response (template/render "admin.html" {})))

(defn questions-page
  [req]
  (let [questions (models.question/get-all-questions)]
    (response (template/render "admin-questions.html"
                {:questions questions}))))

(defn answers-page
  [{:keys [params]}]
  (println params)
  (let [answers (models.answer/get-qas params)]
    (response (template/render "admin-answers.html"
                {:answers answers}))))

(defn users-page
  [req]
  (let [users (models.user/get-users)]
    (response (template/render "admin-users.html"
                {:users users}))))

(defn comment-page
  [{:keys [path-params]}]
  (let [answer (models.answer/get-answer
                (:question_id path-params) (:user_id path-params))
        question (models.question/get-question (:question_id path-params))]
    (if (or (empty? answer) (empty? question))
        (redirect "/login")
        (response (template/render "admin-comment-form.html"
                    {:answer (first answer) :question (first question)})))))

(defn question-page
  [{:keys [path-params]}]
  (if (= "new" (:question_id path-params))
      (response (template/render "admin-question-form.html" {}))
      (let [question (models.question/get-question (:question_id path-params))]
        (if (empty? question)
            (response (template/render "admin-question-form.html" {}))
            (response (template/render "admin-question-form.html" 
                        {:question (first question)}))))))

(defn register-question!
  [{:keys [path-params params]}]
  (try
  	(if (= "new" (:question_id path-params))
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

(defn signup-page
  [req]
  (response (template/render "signup.html" {})))

(defn register-user!
  [{:keys [params]}]
  (let [user (models.user/get-user-by-username (:username params))]
    (if (empty? user)
        (try (models.user/insert! (assoc (dissoc params :password)
                                    :password (hashers/derive (:password params))))
          (status 200)
          (catch Exception _
            (status 500)))
        (status 409))))

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
   ; :question_id が new の時は新規登録
   ["/questions/:question_id" {:get question-page
   	                           :post register-question!}]
   ["/answers" {:get answers-page}]
   ["/answers/:question_id/:user_id" {:get comment-page}]
   ["/answers/:question_id/:user_id/comment" {:post register-comment!}]
   ["/answers/:question_id/:user_id/settled" {:post settled!}]
   ["/answers/:question_id/:user_id/reaction" {:post reaction!}]
   ["/users" {:get users-page}]
   ["/users/new" {:get signup-page
   	              :post register-user!}]])