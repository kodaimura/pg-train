CREATE TABLE IF NOT EXISTS users (
	id INTEGER PRIMARY KEY AUTOINCREMENT,
	username TEXT NOT NULL UNIQUE,
	password TEXT NOT NULL,
	create_at TEXT NOT NULL DEFAULT (DATETIME('now', 'localtime')),
	update_at TEXT NOT NULL DEFAULT (DATETIME('now', 'localtime'))
);

CREATE TABLE IF NOT EXISTS questions (
	id INTEGER PRIMARY KEY AUTOINCREMENT,
	title TEXT NOT NULL,
	statement TEXT NOT NULL,
	hint TEXT,
	answer TEXT NOT NULL,
	level INTEGER,
	respondents INTEGER NOT NULL DEFAULT 0,
	classifying_id TEXT NOT NULL DEFAULT '00',
	del_flg TEXT NOT NULL DEFAULT '0',
	create_at TEXT NOT NULL DEFAULT (DATETIME('now', 'localtime')),
	update_at TEXT NOT NULL DEFAULT (DATETIME('now', 'localtime'))
);

CREATE TABLE IF NOT EXISTS answers (
	id INTEGER PRIMARY KEY AUTOINCREMENT,
	user_id INTEGER NOT NULL,
	question_id INTEGER NOT NULL,
	correct_flg TEXT NOT NULL DEFAULT '0',
	program TEXT,
	help_flg TEXT NOT NULL DEFAULT '0',
	advice TEXT,
	create_at TEXT NOT NULL DEFAULT (DATETIME('now', 'localtime')),
	update_at TEXT NOT NULL DEFAULT (DATETIME('now', 'localtime'))
);

CREATE TRIGGER IF NOT EXISTS trg_users_upd AFTER UPDATE ON users
BEGIN
	UPDATE users
	SET update_at = DATETIME('now', 'localtime')
	WHERE rowid == NEW.rowid;
END;

CREATE TRIGGER IF NOT EXISTS trg_questions_upd AFTER UPDATE ON questions
BEGIN
	UPDATE questions
	SET update_at = DATETIME('now', 'localtime')
	WHERE rowid == NEW.rowid;
END;

CREATE TRIGGER IF NOT EXISTS trg_answers_upd AFTER UPDATE ON answers
BEGIN
	UPDATE answers
	SET update_at = DATETIME('now', 'localtime')
	WHERE rowid == NEW.rowid;
END;

