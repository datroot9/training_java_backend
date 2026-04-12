-- Baseline schema (MySQL). Flyway runs this on empty databases.
CREATE TABLE IF NOT EXISTS `user` (
  user_id INT AUTO_INCREMENT PRIMARY KEY,
  user_name VARCHAR(255) NOT NULL UNIQUE,
  password VARCHAR(255) NOT NULL,
  role VARCHAR(32) NOT NULL DEFAULT 'USER'
);

CREATE TABLE IF NOT EXISTS student (
  student_id INT AUTO_INCREMENT PRIMARY KEY,
  student_name VARCHAR(255) NOT NULL,
  student_code VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS student_info (
  info_id INT AUTO_INCREMENT PRIMARY KEY,
  student_id INT NOT NULL,
  address VARCHAR(255) NOT NULL,
  average_score DOUBLE NOT NULL,
  date_of_birth DATE NOT NULL,
  CONSTRAINT fk_student_info_student
    FOREIGN KEY (student_id) REFERENCES student(student_id)
    ON DELETE CASCADE
);
