(ns pg-train.env)


(def db_drive (System/getenv "DB_DRIVER"))
(def db_host (System/getenv "DB_HOST"))
(def db_user (System/getenv "DB_USER"))
(def db_pass (System/getenv "DB_PASS"))
(def jwt_secret (System/getenv "JWT_SECRET"))

(def env
  {:db_drive db_drive
   :db_host db_host
   :db_user db_user
   :db_pass db_pass
   :jwt_secret jwt_secret})

   
  