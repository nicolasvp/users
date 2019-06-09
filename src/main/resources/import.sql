INSERT INTO roles (name, description, created_at) VALUES('Administrator', 'System Administrator', NOW());
INSERT INTO roles (name, description, created_at) VALUES('User', 'Simple system user', NOW());

INSERT INTO languages (name, created_at) VALUES('Español', NOW());
INSERT INTO languages (name, created_at) VALUES('Inglés', NOW());
INSERT INTO languages (name, created_at) VALUES('Alemán', NOW());

INSERT INTO users (name, last_name, email, password, rol_id, created_at) VALUES('Johnathon', 'Savage', 'Johnathon@mail.com', '123123', 1, NOW());
INSERT INTO users (name, last_name, email, password, rol_id, created_at) VALUES('Kien', 'Rosales', 'Kien@mail.com', '123123', 2, NOW());
INSERT INTO users (name, last_name, email, password, rol_id, created_at) VALUES('Calvin', 'Buxton', 'Calvin@mail.com', '123123', 2, NOW());
INSERT INTO users (name, last_name, email, password, rol_id, created_at) VALUES('Ella', 'Hogg', 'Ella@mail.com', '123123', 2, NOW());
INSERT INTO users (name, last_name, email, password, rol_id, created_at) VALUES('Stanislaw', 'Ward', 'Stanislaw@mail.com', '123123', 2, NOW());

INSERT INTO user_config (user_id, language_id, phrase_type, activate_plugin, created_at) VALUES(1, 1, 1, 1, NOW());
INSERT INTO user_config (user_id, language_id, phrase_type, activate_plugin, created_at) VALUES(2, 1, 2, 1, NOW());
INSERT INTO user_config (user_id, language_id, phrase_type, activate_plugin, created_at) VALUES(3, 1, 1, 1, NOW());
INSERT INTO user_config (user_id, language_id, phrase_type, activate_plugin, created_at) VALUES(4, 1, 2, 1, NOW());
INSERT INTO user_config (user_id, language_id, phrase_type, activate_plugin, created_at) VALUES(5, 1, 1, 1, NOW());

INSERT INTO user_history (user_id, phrase_id, created_at) VALUES(1, 1, NOW());
INSERT INTO user_history (user_id, phrase_id, created_at) VALUES(1, 2, NOW());

INSERT INTO user_likes (user_id, phrase_id, created_at) VALUES(1, 1, NOW());
INSERT INTO user_likes (user_id, phrase_id, created_at) VALUES(1, 2, NOW());

INSERT INTO user_favorities (user_id, phrase_id, created_at) VALUES(1, 1, NOW());
INSERT INTO user_favorities (user_id, phrase_id, created_at) VALUES(1, 2, NOW());
INSERT INTO user_favorities (user_id, phrase_id, created_at) VALUES(1, 3, NOW());