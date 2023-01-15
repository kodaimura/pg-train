(ns pg-train.models.answer
  (:require
    [next.jdbc.sql :as sql]
    [pg-train.db :refer [db]]))


(defn insert!
  [answer]
  (let [sql [
         "insert into answers (
            user_id,
            correct_flg,
            program,
            help_flg,
            advice
          ) values (?,?,?,?,?)"
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
            user_id = ?,
            correct_flg = ?,
            program = ?,
            help_flg = ?,
            advice = ?
		      where id = ?"
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
