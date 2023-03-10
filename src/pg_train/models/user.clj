(ns pg-train.models.user
  (:require
    [next.jdbc.sql :as sql]
    [pg-train.db :refer [db]]))


(defn insert!
  [key-map]
  (sql/insert! db :users key-map))

(defn update!
  [key-map where-params]
  (sql/update! db :users key-map where-params))

(defn get-user-by-username
  [username]
  (let [sql [
         "select user_id, username, password 
          from users
          where username = ?"
          username]]
    (sql/query db sql)))

(defn get-users
  []
  (let [sql [
         "select user_id, username, create_at 
          from users
          where username != 'admin'"]]
    (sql/query db sql)))