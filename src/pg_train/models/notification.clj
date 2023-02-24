(ns pg-train.models.notification
  (:require
    [next.jdbc.sql :as sql]
    [pg-train.db :refer [db]]))


(defn insert!
  [key-map]
  (sql/insert! db :notification key-map))

(defn update!
  [key-map where-params]
  (sql/update! db :notification key-map where-params))

(defn delete!
  [where-params]
  (sql/delete! db :notification where-params))

(defn get-notification
  [user_id]
  (let [sql [
         "select 
            notification_id,
            message,
            send_from,
            send_to, 
            url_path,
            create_at
          from notification
          where send_to = ?"
          user_id]]
    (sql/query db sql)))