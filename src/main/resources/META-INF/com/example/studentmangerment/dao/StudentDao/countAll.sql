SELECT COUNT(*)
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
