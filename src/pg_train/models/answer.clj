(ns pg-train.models.answer
  (:require
    [next.jdbc.sql :as sql]
    [pg-train.db :refer [db]]))


(defn insert!
  [key-map]
  (sql/insert! db :answers key-map))

(defn update!
  [key-map where-params]
  (sql/update! db :answers key-map where-params))

(defn select-by-user_id
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
          from answers
          where user_id = ?"
          user_id]]
    (sql/query db sql)))

(defn select-by-question_id_and_user_id
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
          from answers
          where question_id = ? and user_id = ?"
          question_id
          user_id]]
    (sql/query db sql)))

(defn select-uqa
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
             from answers as a, questions as q, users as u
             where a.question_id = q.id
               and a.user_id = u.id
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