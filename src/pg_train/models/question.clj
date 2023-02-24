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

(defn inc-correctcount!
  [id]
  (let [sql [
         "update question 
          set correct_count = correct_count + 1
          where question_id = ?"
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
            correct_count, 
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
            correct_count
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
            correct_count, 
            del_flg
          from question"]]
    (sql/query db sql)))

(defn get-qas
  [user_id]
  (let [sql [
         "select
            q.question_id,
            q.title,
            q.level, 
            q.correct_count,
            a.correct_flg,
            a.reaction_flg
          from question as q
          left outer join (select * from answer where user_id = ?) as a
            on q.question_id = a.question_id
          where q.del_flg = '0'"
            user_id]]
    (sql/query db sql)))