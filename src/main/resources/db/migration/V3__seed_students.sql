-- Sample students for local/demo use (skips rows if student_code already exists).

INSERT INTO student (student_name, student_code)
SELECT 'Nguyen Van A', 'STU001'
WHERE NOT EXISTS (SELECT 1 FROM student WHERE student_code = 'STU001');

INSERT INTO student (student_name, student_code)
SELECT 'Tran Thi B', 'STU002'
WHERE NOT EXISTS (SELECT 1 FROM student WHERE student_code = 'STU002');

INSERT INTO student (student_name, student_code)
SELECT 'Le Van C', 'STU003'
WHERE NOT EXISTS (SELECT 1 FROM student WHERE student_code = 'STU003');

INSERT INTO student (student_name, student_code)
SELECT 'Pham Thi D', 'STU004'
WHERE NOT EXISTS (SELECT 1 FROM student WHERE student_code = 'STU004');

INSERT INTO student_info (student_id, address, average_score, date_of_birth)
SELECT s.student_id, '123 Le Loi, District 1, Ho Chi Minh City', 8.2, '2001-05-15'
FROM student s WHERE s.student_code = 'STU001'
AND NOT EXISTS (SELECT 1 FROM student_info si WHERE si.student_id = s.student_id);

INSERT INTO student_info (student_id, address, average_score, date_of_birth)
SELECT s.student_id, '45 Tran Hung Dao, Da Nang', 7.5, '2002-11-03'
FROM student s WHERE s.student_code = 'STU002'
AND NOT EXISTS (SELECT 1 FROM student_info si WHERE si.student_id = s.student_id);

INSERT INTO student_info (student_id, address, average_score, date_of_birth)
SELECT s.student_id, '9 Nguyen Hue, Hue City', 9.0, '2000-02-28'
FROM student s WHERE s.student_code = 'STU003'
AND NOT EXISTS (SELECT 1 FROM student_info si WHERE si.student_id = s.student_id);

INSERT INTO student_info (student_id, address, average_score, date_of_birth)
SELECT s.student_id, '88 Vo Van Tan, District 3, Ho Chi Minh City', 8.7, '2003-07-19'
FROM student s WHERE s.student_code = 'STU004'
AND NOT EXISTS (SELECT 1 FROM student_info si WHERE si.student_id = s.student_id);
