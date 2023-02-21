CREATE TABLE IF NOT EXISTS users (
	user_id INTEGER PRIMARY KEY AUTOINCREMENT,
	username TEXT NOT NULL UNIQUE,
	password TEXT NOT NULL,
	create_at TEXT NOT NULL DEFAULT (DATETIME('now', 'localtime')),
	update_at TEXT NOT NULL DEFAULT (DATETIME('now', 'localtime'))
);

CREATE TABLE IF NOT EXISTS question (
	question_id INTEGER PRIMARY KEY AUTOINCREMENT,
	title TEXT NOT NULL,
	statement TEXT NOT NULL,
	answer TEXT NOT NULL,
	level INTEGER,
	correct_count INTEGER NOT NULL DEFAULT 0,
	classifying_id TEXT NOT NULL DEFAULT '00',
	del_flg CHAR(1) NOT NULL DEFAULT '0',
	create_at TEXT NOT NULL DEFAULT (DATETIME('now', 'localtime')),
	update_at TEXT NOT NULL DEFAULT (DATETIME('now', 'localtime'))
);

CREATE TABLE IF NOT EXISTS answer (
	user_id INTEGER,
	question_id INTEGER,
	correct_flg CHAR(1) NOT NULL DEFAULT '0',
	program TEXT,
	help_flg CHAR(1) NOT NULL DEFAULT '0',
	reaction_flg CHAR(1) NOT NULL DEFAULT '0',
	comment TEXT,
	create_at TEXT NOT NULL DEFAULT (DATETIME('now', 'localtime')),
	update_at TEXT NOT NULL DEFAULT (DATETIME('now', 'localtime')),
	PRIMARY KEY(user_id, question_id)
);

CREATE TABLE IF NOT EXISTS message (
	message_id INTEGER PRIMARY KEY AUTOINCREMENT,
	message TEXT NOT NULL,
	send_from INTEGER NOT NULL,
	send_to INTEGER NOT NULL,
	read_flg INTEGER DEFAULT '0',
	create_at TEXT NOT NULL DEFAULT (DATETIME('now', 'localtime')),
	update_at TEXT NOT NULL DEFAULT (DATETIME('now', 'localtime'))
);

CREATE TRIGGER IF NOT EXISTS trg_users_upd AFTER UPDATE ON users
BEGIN
	UPDATE users
	SET update_at = DATETIME('now', 'localtime')
	WHERE rowid == NEW.rowid;
END;

CREATE TRIGGER IF NOT EXISTS trg_question_upd AFTER UPDATE ON question
BEGIN
	UPDATE question
	SET update_at = DATETIME('now', 'localtime')
	WHERE rowid == NEW.rowid;
END;

CREATE TRIGGER IF NOT EXISTS trg_answer_upd AFTER UPDATE ON answer
BEGIN
	UPDATE answer
	SET update_at = DATETIME('now', 'localtime')
	WHERE rowid == NEW.rowid;
END;

CREATE TRIGGER IF NOT EXISTS trg_message_upd AFTER UPDATE ON message
BEGIN
	UPDATE message
	SET update_at = DATETIME('now', 'localtime')
	WHERE rowid == NEW.rowid;
END;

