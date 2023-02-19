(ns pg-train.models.question
  (:require
    [next.jdbc.sql :as sql]
    [pg-train.db :refer [db]]))


(defn insert!
  [key-map]
  (sql/insert! db :question key-map))

(defn update!
  [key-map where-params]
  (sql/update! db :question key-map where-params))

(defn inc-respondents!
  [id]
  (let [sql [
         "update question 
          set respondents = respondents + 1
          where questio_id = ?"
          id]]
    (sql/query db sql)))

(defn get-question
  [id]
  (let [sql [
         "select 
            question_id,
            title,
            statement, 
            answer, 
            level, 
            respondents, 
            classifying_id, 
            del_flg
          from question
          where question_id = ?"
          id]]
    (sql/query db sql)))

(defn get-valid-questions
  []
  (let [sql [
         "select
            question_id,
            title,
            statement, 
            answer, 
            level, 
            respondents, 
            classifying_id
          from question
          where del_flg = '0'"]]
    (sql/query db sql)))

(defn get-all-questions
  []
  (let [sql [
         "select
            question_id,
            title,
            statement, 
            answer, 
            level, 
            respondents, 
            classifying_id, 
            del_flg
          from question"]]
    (sql/query db sql)))