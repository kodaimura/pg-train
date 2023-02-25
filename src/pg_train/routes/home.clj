(ns pg-train.routes.home
  (:require
    [ring.util.response :refer [response redirect status]]
    [pg-train.jwt :as jwt]
    [pg-train.template :as template]
    [pg-train.models.question :as models.question]
    [pg-train.models.answer :as models.answer]
    [pg-train.models.notification :as models.notification]
    [pg-train.models.general :as models.general]))


(defn home-page
  [req]
  (let [user_id (jwt/payload-id req)
        username (jwt/payload-username req)
        qas (models.question/get-qas user_id)
        announce (models.general/get-by-key1 "announce")
        notification (models.notification/get-notification (jwt/payload-id req))]
  (response (template/render "home.html" 
              {:qas qas :username username :announce (first announce) :notification notification}))))

(defn questions-page
  [req]
  (let [qas (models.question/get-qas (jwt/payload-id req))]
    (response (template/render "questions.html"
                {:qas qas}))))

(defn answer-page
  [req]
  (let [question_id (get-in req [:path-params :question_id])
        user_id (jwt/payload-id req)
        question (models.question/get-question question_id)
        answer (models.answer/get-answer question_id user_id)]
    (if (empty? answer) 
        (response (template/render "answer-form.html" {:question (first question)}))
        (response (template/render "answer-form.html" {:question (first question) :answer (first answer)})))))

(defn answer-init
  [question_id user_id & 
   {:keys [correct_flg program help_flg comment]
  	:or {correct_flg "0" program "" help_flg "0" comment ""}}]
  {:question_id question_id
   :user_id user_id
   :correct_flg correct_flg
   :program program
   :help_flg help_flg
   :reaction_flg "0"
   :comment comment})

(defn register-correct_flg!
  [req]
  (let [question_id (get-in req [:path-params :question_id])
        user_id (jwt/payload-id req)
        answer (models.answer/get-answer question_id user_id)]
    (if (empty? answer)
        (models.answer/insert! (answer-init question_id user_id {:correct_flg "1"}))
        (models.answer/update! {:correct_flg "1"} {:question_id question_id :user_id user_id}))
    (models.question/inc-correctcount! question_id)
    (status 200)))

(defn switch-bit
  [bit]
  (if (= bit "1") "0" "1"))

(defn switch-help_flg!
  [req]
  (let [question_id (get-in req [:path-params :question_id])
        user_id (jwt/payload-id req)
        answer (models.answer/get-answer question_id user_id)]
    (if (empty? answer)
        (models.answer/insert! (answer-init question_id user_id {:help_flg "1"}))
        (models.answer/update! {:help_flg (switch-bit (:answer/help_flg (first answer)))} 
                               {:question_id question_id :user_id user_id}))
    (status 200)))

(defn register-program!
  [req]
  (let [question_id (get-in req [:path-params :question_id])
        user_id (jwt/payload-id req)
        program (get-in req [:params :program])
        answer (models.answer/get-answer question_id user_id)]
    (if (empty? answer)
        (models.answer/insert! (answer-init question_id user_id {:program program}))
        (models.answer/update! {:program program} {:question_id question_id :user_id user_id}))
    (status 200)))

(defn delete-notification!
  [{:keys [path-params]}]
  (models.notification/delete! 
    {:notification_id (:notification_id path-params)}))

(defn wrap-home
  [handler]
  (fn [request]
    (let [name (jwt/payload-username request)]
      (if (= name "admin")
          (redirect "/admin")
          (handler request)))))

(def home-routes
  [""
   {:middleware [jwt/wrap-jwt-authentication
   	             wrap-home]}
   ["/" {:get home-page}]
   ["/questions" {:get questions-page}]
   ["/questions/:question_id" {:get answer-page}]
   ["/questions/:question_id/correct" {:post register-correct_flg!}]
   ["/questions/:question_id/help" {:post switch-help_flg!}]
   ["/questions/:question_id/program" {:post register-program!}]
   ["/notification/:notification_id" {:delete delete-notification!}]])
