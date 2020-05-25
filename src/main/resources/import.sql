INSERT INTO roles (id, name, description, created_at) VALUES('1', 'ROLE_ADMIN', 'System Administrator', NOW());
INSERT INTO roles (id, name, description, created_at) VALUES('2' ,'ROLE_USER', 'Simple system user', NOW());

INSERT INTO users (id, name, last_name, username, email, password, rol_id, created_at) VALUES('1', 'Johnathon','Savage','admin','Johnathon@mail.com', '$2a$10$pqcuQR8kmw0aE1DUKx.VnOK.OA67OdXr6PkTi7aFBhmtgwV5BCXmC', 1, NOW());
INSERT INTO users (id, name, last_name, username, email, password, rol_id, created_at) VALUES('2', 'Kien', 'Rosales','kien', 'Kien@mail.com', '$2a$10$PPomm6k2x19IErcvc4/OJeI8.uWwviO0mkjuPiYe/WjNPJJJZjocO', 2, NOW());
INSERT INTO users (id, name, last_name, username, email, password, rol_id, created_at) VALUES('3', 'Calvin', 'Buxton','calvin', 'Calvin@mail.com', '$2a$10$h2wAfda.riUifsrvJSIXKOXGmA0Q0bgqUFfvmAg9/lgWHqKvbPp8a', 2, NOW());
INSERT INTO users (id, name, last_name, username, email, password, rol_id, created_at) VALUES('4', 'Ella', 'Hogg','ella','Ella@mail.com', '$2a$10$DHX0CR77YvDbB4h5oRPDbOod.X0cFEPupYFnf0YG2i8t03VtTIzB.', 2, NOW());
INSERT INTO users (id, name, last_name, username, email, password, rol_id, created_at) VALUES('5', 'Stanislaw', 'Ward','stan', 'Stanislaw@mail.com', '2a$10$R7xzNA4GhmZOglipIzAhb.Z963bwsyotCiC/0oNFnx2aj3fzgVKpG', 2, NOW());