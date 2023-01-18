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
            advice,
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
            advice,
            create_at,
            update_at
          from answers
          where question_id = ? and user_id = ?"
          question_id
          user_id]]
    (sql/query db sql)))
