(ns pg-train.models.answer
  (:require
    [next.jdbc.sql :as sql]
    [pg-train.db :refer [db]]))


(defn insert!
  [key-map]
  (sql/insert! db :answer key-map))

(defn update!
  [key-map where-params]
  (sql/update! db :answer key-map where-params))

(defn get-user-answers
  [user_id]
  (let [sql [
         "select
            question_id,
            user_id,
            correct_flg,
            program,
            help_flg,
            reaction_flg,
            comment,
            create_at,
            update_at
          from answer
          where user_id = ?"
          user_id]]
    (sql/query db sql)))

(defn get-answer
  [question_id user_id]
  (let [sql [
         "select
            question_id,
            user_id,
            correct_flg,
            program,
            help_flg,
            reaction_flg,
            comment,
            create_at,
            update_at
          from answer
          where question_id = ? and user_id = ?"
          question_id
          user_id]]
    (sql/query db sql)))

(defn get-qas
  [params]
  (let [sql 
        (remove nil?
          [(str
            "select
               a.question_id,
               a.user_id,
               u.username,
               a.correct_flg,
               a.program,
               a.help_flg,
               a.reaction_flg,
               a.comment,
               q.title,
               q.answer,
               q.level, 
               q.respondents
             from answer as a, question as q, users as u
             where a.question_id = q.question_id
               and a.user_id = u.user_id
               and q.del_flg = '0'"
             (if (:user_id params) " and a.user_id = ?" "")
             (if (:question_id params) " and a.question_id = ?" "")
             (if (:help_flg params) " and a.help_flg = ?" "")
             (if (:correct_flg params) " and a.correct_flg = ?" "")
             (if (:reaction_flg params) " and a.reaction_flg = ?" ""))
            (:user_id params)
            (:question_id params)
            (:help_flg params)
            (:correct_flg params)
            (:reaction_flg params)])]
    (sql/query db sql)))