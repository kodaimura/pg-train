(ns pg-train.db
  (:require
    [next.jdbc.sql :as sql]))


(def db {:dbtype "sqlite"
         :dbname "db/pg-train.db"})