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
WHERE 1=1
/*%if code != null*/
    AND s.student_code LIKE /* @infix(code) */'%STU001%'
/*%end*/
/*%if name != null*/
    AND s.student_name LIKE /* @infix(name) */'%Huynh Dat%'
/*%end*/
/*%if birthday != null*/
  AND DATE(si.date_of_birth) = DATE(/* birthday */'2004/01/01')
/*%end*/
ORDER BY s.student_id
LIMIT /* limit */10
OFFSET /* offset */0
