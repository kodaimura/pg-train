(ns pg-train.models.general
  (:require
    [next.jdbc.sql :as sql]
    [pg-train.db :refer [db]]))


(defn insert!
  [key-map]
  (sql/insert! db :general key-map))

(defn update!
  [key-map where-params]
  (sql/update! db :general key-map where-params))

(defn get-by-key1
  [key1]
  (let [sql [
         "select 
            key1,
            key2,
            value, 
            remarks,
            create_at,
            update_at
          from general
          where key1 = ?"
          key1]]
    (sql/query db sql)))