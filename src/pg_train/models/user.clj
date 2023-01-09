(ns pg-train.models.user
  (:require
    [next.jdbc.sql :as sql]
    [pg-train.db :refer [db]]))


(defn create-user!
  [user]
  (let [sql [
         "insert into users (
           id, username, password
         ) values (?, ?, ?)"
         (:id user)
         (:username user)
         (:password user)]]
     (sql/query db sql)))