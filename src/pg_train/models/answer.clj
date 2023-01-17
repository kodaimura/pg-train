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
          ) values (?,?,?,?,?,?)"
         (:question_id answer)
         (:user_id answer)
         (:correct_flg answer)
         (:program answer)
         (:help_flg answer)
         (:advice answer)]]
     (sql/query db sql)))

(defn update!
  [answer]
  (let [sql [
         "update answers
          set
            correct_flg = ?,
            program = ?,
            help_flg = ?,
            advice = ?
		      where question_id = ? and user_id = ?"
         (:correct_flg answer)
         (:program answer)
         (:help_flg answer)
         (:advice answer)
         (:question_id answer)
         (:user_id answer)]]
     (sql/query db sql)))

(defn upsert!
  [answer]
  (let [sql [
         "insert into answers (
            question_id,
            user_id,
            correct_flg,
            program,
            help_flg,
            advice
          ) values (?,?,?,?,?,?) 
          on conflict (question_id, user_id) 
          do update 
          set
            correct_flg = ?,
            program = ?,
            help_flg = ?,
            advice = ?
          "
         (:question_id answer)
         (:user_id answer)
         (:correct_flg answer)
         (:program answer)
         (:help_flg answer)
         (:advice answer)
         (:correct_flg answer)
         (:program answer)
         (:help_flg answer)
         (:advice answer)]]
     (sql/query db sql)))

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
