(ns pg-train.models.question
  (:require
    [next.jdbc.sql :as sql]
    [pg-train.db :refer [db]]))


(defn insert!
  [question]
  (let [sql [
         "insert into questions (
            statement, 
            hint, 
            answer, 
            level
          ) values (?, ?, ?, ?)"
         (:statement question)
         (:hint question)
         (:answer question)
         (:level question)]]
     (sql/query db sql)))

(defn update!
  [question]
  (let [sql [
         "update questions
          set
            statement = ?,
            hint = ?,
            answer = ?,
            level = ?,
            classifying_id = ?,
            del_flg = ?
          where id = ?"
         (:statement question)
         (:hint question)
         (:answer question)
         (:level question)
         (:classifying_id question)
         (:del_flg question)
         (:id question)]]
     (sql/query db sql)))

(defn select-by-id
  [id]
  (let [sql [
         "select 
            id,
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
            statement, 
            hint, 
            answer, 
            level, 
            respondents, 
            classifying_id, 
            del_flg
          from questions
          where del_flg = '0'"]]
    (sql/query db sql)))