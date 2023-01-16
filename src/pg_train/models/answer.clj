(ns pg-train.models.answer
  (:require
    [next.jdbc.sql :as sql]
    [pg-train.db :refer [db]]))


(defn insert!
  [answer]
  (let [sql [
         "insert into answers (
            question_id,
            user_id,
            correct_flg,
            program,
            help_flg,
            advice
          ) values (?, ?,?,?,?,?)"
         (:question_id answer)
         (:user_id answer)
         (:correct_flg answer)
         (:program answer)
         (:help_flg answer)
         (:advice answer)]]
     (sql/query db sql)))

(defn update!
  [question]
  (let [sql [
         "update answers
          set
            question_id = ?,
            user_id = ?,
            correct_flg = ?,
            program = ?,
            help_flg = ?,
            advice = ?
		      where id = ?"
		     (:question_id answer)
         (:user_id answer)
         (:correct_flg answer)
         (:program answer)
         (:help_flg answer)
         (:advice answer)
         (:id answer)]]
     (sql/query db sql)))

(defn select-by-id
  [id]
  (let [sql [
         "select
            id,
            question_id,
            user_id,
            correct_flg,
            program,
            help_flg,
            advice,
            create_at,
            update_at
          from answers
          where id = ?"
          id]]
    (sql/query db sql)))

(defn select-by-user_id
  [user_id]
  (let [sql [
         "select
            id,
            question_id,
            user_id,
            correct_flg,
            program,
            help_flg,
            advice,
            create_at,
            update_at
          from questions
          where user_id = ?"
          user_id]]
    (sql/query db sql)))

(defn select-by-question_id_and_user_id
  [question_id user_id]
  (let [sql [
         "select
            id,
            question_id,
            user_id,
            correct_flg,
            program,
            help_flg,
            advice,
            create_at,
            update_at
          from questions
          where question_id = ? and user_id = ?"
          question_id
          user_id]]
    (sql/query db sql)))
