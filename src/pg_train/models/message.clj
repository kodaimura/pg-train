(ns pg-train.models.message
  (:require
    [next.jdbc.sql :as sql]
    [pg-train.db :refer [db]]))


(defn insert!
  [key-map]
  (sql/insert! db :message key-map))

(defn update!
  [key-map where-params]
  (sql/update! db :message key-map where-params))

(defn get-chat
  [user_id1 user_id2]
  (let [sql [
         "select 
            message_id,
            message,
            send_from, 
            send_to,
            read_flg,
            create_at
          from message
          where (send_to = ? and send_from = ?)
             or (send_to = ? and send_from = ?)"
          user_id1 user_id2 user_id2 user_id1]]
    (sql/query db sql)))

(defn get-messages-after
  [user_id1 user_id2 time]
  (let [sql [
         "select 
            message_id,
            message,
            send_from, 
            send_to,
            read_flg,
            create_at
          from message
          where (send_to = ? and send_from = ? or send_to = ? and send_from = ?)
            and create_at > ?"
          user_id1 user_id2 user_id2 user_id1 time]]
    (sql/query db sql)))