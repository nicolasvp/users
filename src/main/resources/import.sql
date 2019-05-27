INSERT INTO users (name, last_name, email, password, created_at) VALUES('Nicolas', 'Vera', 'nicolas@mail.com', '123123', NOW());
INSERT INTO roles (name, description, created_at) VALUES('Administrator', 'System Admistrator', NOW());
INSERT INTO user_config (user_id, language_id, created_at) VALUES(1, 1, NOW());

INSERT INTO languages (name, created_at) VALUES('Español', NOW());
INSERT INTO languages (name, created_at) VALUES('Inglés', NOW());
INSERT INTO languages (name, created_at) VALUES('Alemán', NOW());


INSERT INTO user_history (user_id, phrase_id, created_at) VALUES(1, 1, NOW());
INSERT INTO user_history (user_id, phrase_id, created_at) VALUES(1, 2, NOW());
INSERT INTO user_history (user_id, phrase_id, created_at) VALUES(1, 3, NOW());

INSERT INTO user_likes (user_id, phrase_id, created_at) VALUES(1, 1, NOW());
INSERT INTO user_likes (user_id, phrase_id, created_at) VALUES(1, 2, NOW());