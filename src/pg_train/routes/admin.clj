(ns pg-train.routes.admin
  (:require
    [ring.util.response :refer [response redirect status]]
    [buddy.hashers :as hashers]
    [pg-train.jwt :as jwt]
    [pg-train.template :as template]
    [pg-train.models.question :as models.question]
    [pg-train.models.answer :as models.answer]
    [pg-train.models.user :as models.user]
    [pg-train.models.message :as models.message]
    [pg-train.models.notification :as models.notification]
    [pg-train.models.general :as models.general]))


(defn admin-page
  [req]
  (let [announce (models.general/get-by-key1 "announce")
        notification (models.notification/get-notification (jwt/payload-id req))]
  	(response (template/render "admin.html" 
  	             {:announce (first announce) :notification notification}))))

(defn questions-page
  [req]
  (let [questions (models.question/get-all-questions)]
    (response (template/render "admin-questions.html"
                  {:questions questions}))))

(defn answers-page
  [{:keys [params]}]
  (let [answers (models.answer/get-qas params)]
    (response (template/render "admin-answers.html"
                {:answers answers}))))

(defn chat-page
  [{:keys [params]}]
  (let [user_id (:user_id params)
        messages (models.message/get-chat user_id 1)]
    (response (template/render "admin-chat.html"
                {:user_id user_id 
                 :messages messages 
                 :last_time (:message/create_at (last messages))}))))

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
      (response (template/render "admin-comment-form.html"
                  {:answer (first answer) :question (first question)}))))

(defn question-page
  [{:keys [path-params]}]
  (if (= "new" (:question_id path-params))
      (response (template/render "admin-question-form.html" {}))
      (let [question (models.question/get-question (:question_id path-params))]
        (if (empty? question)
            (response (template/render "admin-question-form.html" {}))
            (response (template/render "admin-question-form.html" 
                        {:question (first question)}))))))

(defn api-answers
  [{:keys [params]}]
  (let [answers (models.answer/get-qas params)]
    (response {:answers answers})))

(defn api-messages
  [{:keys [params]}]
  (let [last_time (:last_time params)
        user_id (:user_id params)
        messages (models.message/get-messages-after user_id 1 last_time)]
    (if (empty? messages)
        (response {:messages [] :last_time last_time})
        (response {:messages messages 
    	             :last_time (:message/create_at　(last messages))}))))

(defn register-message!
  [{:keys [params]}]
  (models.message/insert! {
  	:send_from 1
  	:send_to (:send_to params)
  	:message (:message params)
  	})
  (models.notification/insert! {
  	:message "新着メッセージがあります。"
  	:send_from 1
  	:send_to (:send_to params)
  	:url_path "/messages"
  	})
  (status 200))

(defn register-question!
  [{:keys [path-params params]}]
  (if (= "new" (:question_id path-params))
  	  (models.question/insert! params)
  	  (models.question/update! params path-params))
  (redirect "/admin/questions"))

(defn register-comment!
  [{:keys [path-params params]}]
  (models.answer/update! params path-params)
  (status 200))

(defn settled!
  [{:keys [path-params]}]
  (models.answer/update! {:help_flg "0"} path-params)
  (models.notification/insert! {
  	:message (str "ID:" (:question_id path-params) "にコメントがありました。")
  	:send_from 1
  	:send_to (:user_id path-params)
  	:url_path (str "/questions/" (:question_id path-params))
  	})
  (status 200))

(defn reaction!
  [{:keys [path-params]}]
  (models.answer/update! {:reaction_flg "1"} path-params)
  (models.notification/insert! {
  	:message (str "ID:" (:question_id path-params) "にリアクションがありました。")
  	:send_from 1
  	:send_to (:user_id path-params)
  	:url_path (str "/questions/" (:question_id path-params))
  	})
  (status 200))

(defn signup-page
  [req]
  (response (template/render "signup.html" {})))

(defn register-user!
  [{:keys [params]}]
  (let [user (models.user/get-user-by-username (:username params))]
    (if (empty? user)
        (models.user/insert! (assoc (dissoc params :password)
                               :password (hashers/derive (:password params))))
        (status 409))
    (status 200)))

(defn register-announce!
  [{:keys [params]}]
  (models.general/update! {:value (:announce params)} 
                           {:key1 "announce"})
  (status 200))

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
   ["/questions/:question_id" {:get question-page :post register-question!}]
   ["/api/answers" {:get api-answers}]
   ["/answers" {:get answers-page}]
   ["/answers/:question_id/:user_id" {:get comment-page}]
   ["/answers/:question_id/:user_id/comment" {:post register-comment!}]
   ["/answers/:question_id/:user_id/settled" {:post settled!}]
   ["/answers/:question_id/:user_id/reaction" {:post reaction!}]
   ["/users" {:get users-page}]
   ["/users/new" {:get signup-page :post register-user!}]
   ["/messages" {:get chat-page :post register-message!}]
   ["/api/messages" {:get api-messages}]
   ["/announce" {:post register-announce!}]])