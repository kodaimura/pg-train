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
  (let [answer (models.answer/select)]
    (response (template/render "questions.html"
                {:questions questions}))))

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
   ["/questions/:id" {:get answer-page}]])