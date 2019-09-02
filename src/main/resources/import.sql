INSERT INTO roles (name, description, created_at) VALUES('Administrator', 'System Administrator', NOW());
INSERT INTO roles (name, description, created_at) VALUES('User', 'Simple system user', NOW());

INSERT INTO languages (name, created_at) VALUES('Español', NOW());
INSERT INTO languages (name, created_at) VALUES('Inglés', NOW());
INSERT INTO languages (name, created_at) VALUES('Alemán', NOW());

INSERT INTO users (name, last_name, username, email, password, rol_id, created_at) VALUES('Johnathon','Savage','johnathon','Johnathon@mail.com', '$2a$10$BX/NnUf7dzFVRt4qB0wPpOqXtL4lEiE5rWZ2jIaqZwUtaj7k3iUey', 1, NOW());
INSERT INTO users (name, last_name, username, email, password, rol_id, created_at) VALUES('Kien', 'Rosales','kien', 'Kien@mail.com', '$2a$10$PPomm6k2x19IErcvc4/OJeI8.uWwviO0mkjuPiYe/WjNPJJJZjocO', 2, NOW());
INSERT INTO users (name, last_name, username, email, password, rol_id, created_at) VALUES('Calvin', 'Buxton','calvin', 'Calvin@mail.com', '$2a$10$h2wAfda.riUifsrvJSIXKOXGmA0Q0bgqUFfvmAg9/lgWHqKvbPp8a', 2, NOW());
INSERT INTO users (name, last_name, username, email, password, rol_id, created_at) VALUES('Ella', 'Hogg','ella','Ella@mail.com', '$2a$10$DHX0CR77YvDbB4h5oRPDbOod.X0cFEPupYFnf0YG2i8t03VtTIzB.', 2, NOW());
INSERT INTO users (name, last_name, username, email, password, rol_id, created_at) VALUES('Stanislaw', 'Ward','stan', 'Stanislaw@mail.com', '2a$10$R7xzNA4GhmZOglipIzAhb.Z963bwsyotCiC/0oNFnx2aj3fzgVKpG', 2, NOW());

INSERT INTO configuration (user_id, language_id, phrase_type, activate_plugin, created_at) VALUES(1, 1, 1, 1, NOW());
INSERT INTO configuration (user_id, language_id, phrase_type, activate_plugin, created_at) VALUES(2, 1, 2, 1, NOW());
INSERT INTO configuration (user_id, language_id, phrase_type, activate_plugin, created_at) VALUES(3, 1, 1, 1, NOW());
INSERT INTO configuration (user_id, language_id, phrase_type, activate_plugin, created_at) VALUES(4, 1, 2, 1, NOW());
INSERT INTO configuration (user_id, language_id, phrase_type, activate_plugin, created_at) VALUES(5, 1, 0, 1, NOW());