(ns pg-train.routes.chat
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

(defn chat-page
  [req]
  (let [my_id (jwt/payload-id req)
        partner_id (get-in req [:params :user_id])
        messages (models.message/get-messages-recent-20 my_id partner_id)]
    (response (template/render "chat.html"
                (if (empty? messages)
                    {:my_id my_id
                     :partner_id partner_id
                     :messages []
                     :max_message_id 0
                     :min_message_id 0}
                    {:my_id my_id
                     :partner_id partner_id
                     :messages messages 
                     :max_message_id (:message/message_id (last messages))
                     :min_message_id (:message/message_id (first messages))})))))

(defn get-messages
  [req]
  (let [max_message_id (get-in req [:params :max_message_id])
        min_message_id (get-in req [:params :min_message_id])
        type (get-in req [:params :type])
        my_id (jwt/payload-id req)
        partner_id (get-in req [:params :user_id])]
    (cond
      (= type "after") 
        (let [messages (models.message/get-messages-after my_id partner_id max_message_id)]
          (if (empty? messages)
              (response {:messages [] :max_message_id nil :min_message_id nil})
              (response {:messages messages
              	         :max_message_id (:message/message_id (last messages)) 
              	         :min_message_id (:message/message_id (first messages))})))
      (= type "before") 
        (let [messages (models.message/get-messages-before-20 my_id partner_id min_message_id)]
          (if (empty? messages)
              (response {:messages [] :max_message_id nil :min_message_id nil})
              (response {:messages messages 
              	         :max_message_id (:message/message_id (first messages)) 
              	         :min_message_id (:message/message_id (last messages))})))
      :else (response {:messages [] :max_message_id nil :min_message_id nil}))))

(defn register-message!
  [req]
  (let [params (:params req)
        send_from (jwt/payload-id req)
        send_to (:send_to params)]
  	(models.message/insert! {
  		:send_from send_from
  		:send_to send_to
  		:message (:message params)
  	})
  	(models.notification/insert! {
  		:message (str "新着メッセージがあります。" (if-not (= send_from 1) (str "(ユーザID: " send_from ")")))
  		:send_from send_from
  		:send_to send_to
  		:url_path (str "/chat?user_id=" send_from)
  	})
  	(status 200)))


(def chat-routes
  ["/chat"
   {:middleware [jwt/wrap-jwt-authentication]}
   ["" {:get chat-page}]
   ["/messages" {:get get-messages :post register-message!}]])