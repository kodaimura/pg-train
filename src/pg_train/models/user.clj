(ns pg-train.models.user
  (:require
    [next.jdbc.sql :as sql]
    [pg-train.db :refer [db]]))


(defn insert!
  [user]
  (let [sql [
         "insert into users (
           username, password
         ) values (?, ?)"
         (:username user)
         (:password user)]]
     (sql/query db sql)))

(defn select-by-username
  [username]
  (let [sql [
         "select id, username, password 
          from users
          where username = ?"
          username]]
    (sql/query db sql)))