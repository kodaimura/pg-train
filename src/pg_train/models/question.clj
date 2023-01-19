(ns pg-train.models.question
  (:require
    [next.jdbc.sql :as sql]
    [pg-train.db :refer [db]]))


(defn insert!
  [key-map]
  (sql/insert! db :questions key-map))

(defn update!
  [key-map where-params]
  (sql/update! db :questions key-map where-params))

(defn inc-respondents!
  [id]
  (let [sql [
         "update questions 
          set respondents = respondents + 1
          where id = ?"
          id]]
    (sql/query db sql)))

(defn select-by-id
  [id]
  (let [sql [
         "select 
            id,
            title,
            statement, 
            hint, 
            answer, 
            level, 
            respondents, 
            classifying_id, 
            del_flg
          from questions
          where id = ?"
          id]]
    (sql/query db sql)))

(defn select-all
  []
  (let [sql [
         "select
            id,
            title,
            statement, 
            hint, 
            answer, 
            level, 
            respondents, 
            classifying_id
          from questions
          where del_flg = '0'"]]
    (sql/query db sql)))

(defn select-all-admin
  []
  (let [sql [
         "select
            id,
            title,
            statement, 
            hint, 
            answer, 
            level, 
            respondents, 
            classifying_id, 
            del_flg
          from questions"]]
    (sql/query db sql)))