SELECT COUNT(*)
FROM student s
WHERE 1=1
/*%if code != null*/
    AND s.student_code LIKE /* @infix(code) */'%STU001%'
/*%end*/
/*%if name != null*/
    AND s.student_name LIKE /* @infix(name) */'%Huynh Dat%'
/*%end*/
