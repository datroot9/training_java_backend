SELECT
  s.student_id AS id,
  s.student_name AS name,
  s.student_code AS code,
  si.info_id AS infoId,
  si.address AS address,
  si.average_score AS averageScore,
  si.date_of_birth AS birthday
FROM student s
LEFT JOIN student_info si ON s.student_id = si.student_id
WHERE s.student_id = /*id*/1
