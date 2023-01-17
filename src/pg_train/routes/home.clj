(ns pg-train.routes.home
  (:require
    [ring.util.response :refer [response redirect status]]
    [pg-train.jwt :as jwt]
    [pg-train.template :as template]
    [pg-train.models.question :as models.question]
    [pg-train.models.answer :as models.answer]))


(defn home-page
  [req]
  (response (template/render "home.html" {})))

(defn questions-page
  [req]
  (let [questions (models.question/select-all)]
    (response (template/render "questions.html"
                {:questions questions}))))

(defn answer-page
  [req]
  (let [question_id (get-in req [:path-params :id])
        user_id (jwt/payload-id req)
        question (models.question/select-by-id question_id)
        answer (models.answer/select-by-question_id_and_user_id question_id user_id)]
    (cond
      (empty? question) (redirect "/login")
      (empty? answer) (response (template/render "answer.html" 
                        {:question (first question)}))
      :else (response (template/render "answer.html" 
                        {:question (first question) :answer (first answer)})))))

(defn answer-init
  [question_id user_id]
  {:question_id question_id
   :user_id user_id
   :correct_flg "0"
   :program ""
   :help_flg "0"
   :advice ""})

(defn register-correct_flg!
  [req]
  (let [question_id (get-in req [:path-params :id])
        user_id (jwt/payload-id req)
        answer (models.answer/select-by-question_id_and_user_id question_id user_id)
        answer* (if (empty? answer) 
                    (answer-init question_id user_id) 
                    (first answer))]
    (try
      (models.answer/upsert! (assoc (dissoc answer* :correct_flg)
                                    :correct_flg "1"))
      (status 200)
      (catch Exception _
        (redirect "/login")))))

(defn register-help_flg!
  [req]
  (let [question_id (get-in req [:path-params :id])
        user_id (jwt/payload-id req)
        answer (models.answer/select-by-question_id_and_user_id question_id user_id)
        answer* (if (empty? answer) 
                    (answer-init question_id user_id) 
                    (first answer))]
    (try
      (models.answer/upsert! (assoc (dissoc answer* :help_flg)
                                    :help_flg "1"))
      (status 200)
      (catch Exception _
        (redirect "/login")))))

(defn register-program!
  [req]
  (let [question_id (get-in req [:path-params :id])
        user_id (jwt/payload-id req)
        answer (models.answer/select-by-question_id_and_user_id question_id user_id)
        answer* (if (empty? answer) 
                    (answer-init question_id user_id) 
                    (first answer))]
    (try
      (models.answer/upsert! (assoc (dissoc answer* :program)
                                    :program (get-in req [:params :program])))
      (status 200)
      (catch Exception _
        (redirect "/login")))))

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
   ["" {:get home-page}]
   ["/questions" {:get questions-page}]
   ["/questions/:id" {:get answer-page}]
   ["/questions/:id/correct" {:post register-correct_flg!}]
   ["/questions/:id/help" {:post register-help_flg!}]
   ["/questions/:id/program" {:post register-program!}]])