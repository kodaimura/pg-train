(ns pg-train.db
  (:require
    [next.jdbc.sql :as sql]))


(def db {:dbtype "sqlite"
         :dbname "db/pg-train.db"})


(defn create-users-table! []
  (let [sql "create table if not exists users (
              id integer primary key autoincrement,
              username text unique,
              password text,
              created_at text default (datetime('now','localtime')),
              updated_at text default (datetime('now','localtime')))"]
    (sql/query db [sql])))

(create-users-table!)