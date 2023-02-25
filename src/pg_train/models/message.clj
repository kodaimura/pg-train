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

(defn get-messages-recent-20
  [my_id partner_id]
  (let [sql [
         "select * 
           from
            (select 
              message_id,
              message,
              send_from, 
              send_to,
              read_flg,
              create_at
             from message
             where ((send_to = ? and send_from = ?) or (send_to = ? and send_from = ?))
             order by message_id desc limit 20) 
           order by message_id"
          my_id partner_id partner_id my_id]]
    (sql/query db sql)))

(defn get-messages-before-20
  [my_id partner_id message_id]
  (let [sql [
         "select 
            message_id,
            message,
            send_from, 
            send_to,
            read_flg,
            create_at
          from message
          where ((send_to = ? and send_from = ?) or (send_to = ? and send_from = ?))
            and message_id < ?
          order by message_id desc
          limit 20"
          my_id partner_id partner_id my_id message_id]]
    (sql/query db sql)))

(defn get-messages-after
  [my_id partner_id message_id]
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
            and message_id > ?"
          my_id partner_id partner_id my_id message_id]]
    (sql/query db sql)))
