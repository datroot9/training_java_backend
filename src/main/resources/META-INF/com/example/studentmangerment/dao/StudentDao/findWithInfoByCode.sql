SELECT
    s.student_id as id,
    s.student_name as name,
    s.student_code as code,
    si.address,
    si.average_score as averageScore,
    si.date_of_birth as birthday
FROM student s
LEFT JOIN student_info si ON s.student_id = si.student_id
WHERE s.student_code = /* code */'STU001'
