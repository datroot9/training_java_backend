-- Default admin. Password: password (BCrypt). Remove or rotate in production.
INSERT INTO `user` (user_name, password, role)
VALUES ('admin@local.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'ADMIN');
