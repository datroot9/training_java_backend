SELECT s.*
FROM student s
-- JOIN student_info si ON s.student_id = si.student_id
WHERE 1=1
/*%if code != null*/
    AND s.student_code LIKE /* @infix(code) */'%STU001%'
/*%end*/
/*%if name != null*/
    AND s.student_name LIKE /* @infix(name) */'%Huynh Dat%'
/*%end*/
-- /*%if birthday != null*/
--   AND si.date_of_birth = /* birthday */'2004/01/01'
-- /*%end*/
ORDER BY s.student_id
LIMIT /* limit */10
OFFSET /* offset */0
