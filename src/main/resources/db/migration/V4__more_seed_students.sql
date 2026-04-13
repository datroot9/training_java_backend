-- Additional demo students (STU005–STU030). Idempotent: skips if code or info row exists.

INSERT INTO student (student_name, student_code)
SELECT v.student_name, v.student_code
FROM (
  SELECT 'Hoang Van E' AS student_name, 'STU005' AS student_code UNION ALL
  SELECT 'Vo Thi F', 'STU006' UNION ALL
  SELECT 'Dang Van G', 'STU007' UNION ALL
  SELECT 'Bui Thi H', 'STU008' UNION ALL
  SELECT 'Do Van I', 'STU009' UNION ALL
  SELECT 'Ngo Thi K', 'STU010' UNION ALL
  SELECT 'Ly Van L', 'STU011' UNION ALL
  SELECT 'Truong Thi M', 'STU012' UNION ALL
  SELECT 'Duong Van N', 'STU013' UNION ALL
  SELECT 'Dinh Thi P', 'STU014' UNION ALL
  SELECT 'Huynh Van Q', 'STU015' UNION ALL
  SELECT 'Phan Thi R', 'STU016' UNION ALL
  SELECT 'Vu Van S', 'STU017' UNION ALL
  SELECT 'Ta Thi T', 'STU018' UNION ALL
  SELECT 'Mai Van U', 'STU019' UNION ALL
  SELECT 'Cao Thi V', 'STU020' UNION ALL
  SELECT 'Lam Van X', 'STU021' UNION ALL
  SELECT 'Trieu Thi Y', 'STU022' UNION ALL
  SELECT 'Dang Van Khoa', 'STU023' UNION ALL
  SELECT 'Nguyen Thi Anh', 'STU024' UNION ALL
  SELECT 'Tran Van Binh', 'STU025' UNION ALL
  SELECT 'Le Thi Chi', 'STU026' UNION ALL
  SELECT 'Pham Van Duc', 'STU027' UNION ALL
  SELECT 'Hoang Thi Em', 'STU028' UNION ALL
  SELECT 'Vo Van Giang', 'STU029' UNION ALL
  SELECT 'Bui Thi Hoa', 'STU030'
) v
WHERE NOT EXISTS (SELECT 1 FROM student s WHERE s.student_code = v.student_code);

INSERT INTO student_info (student_id, address, average_score, date_of_birth)
SELECT s.student_id, v.address, v.average_score, v.date_of_birth
FROM student s
INNER JOIN (
  SELECT 'STU005' AS student_code, '12 Hang Bac, Hanoi' AS address, 7.8 AS average_score, '2001-08-22' AS date_of_birth UNION ALL
  SELECT 'STU006', '34 Bach Dang, Hai Phong', 8.0, '2002-01-10' UNION ALL
  SELECT 'STU007', '56 Nguyen Trai, Can Tho', 6.9, '2003-04-05' UNION ALL
  SELECT 'STU008', '78 Le Duan, Nha Trang', 8.5, '2000-09-14' UNION ALL
  SELECT 'STU009', '90 Tran Phu, Vung Tau', 7.2, '2004-02-18' UNION ALL
  SELECT 'STU010', '15 Ly Thuong Kiet, Da Lat', 8.9, '2001-12-01' UNION ALL
  SELECT 'STU011', '22 Phan Chu Trinh, Quy Nhon', 7.6, '2002-06-25' UNION ALL
  SELECT 'STU012', '33 Hai Ba Trung, Hue', 8.1, '2003-03-08' UNION ALL
  SELECT 'STU013', '44 Nguyen Du, Ho Chi Minh City', 9.1, '2000-11-20' UNION ALL
  SELECT 'STU014', '55 Le Hong Phong, Da Nang', 7.4, '2004-05-12' UNION ALL
  SELECT 'STU015', '66 Vo Thi Sau, Bien Hoa', 8.3, '2001-07-30' UNION ALL
  SELECT 'STU016', '77 Dien Bien Phu, Hanoi', 7.9, '2002-10-17' UNION ALL
  SELECT 'STU017', '88 Hoang Dieu, Ho Chi Minh City', 8.6, '2003-01-22' UNION ALL
  SELECT 'STU018', '99 Pasteur, Ho Chi Minh City', 7.1, '2000-04-09' UNION ALL
  SELECT 'STU019', '10 Tran Quoc Toan, Hue', 8.8, '2004-08-14' UNION ALL
  SELECT 'STU020', '20 Hung Vuong, Can Tho', 7.7, '2001-02-28' UNION ALL
  SELECT 'STU021', '30 Nguyen Van Linh, Da Nang', 8.4, '2002-05-19' UNION ALL
  SELECT 'STU022', '40 Cach Mang Thang Tam, Vung Tau', 6.8, '2003-09-03' UNION ALL
  SELECT 'STU023', '50 Ton Duc Thang, Ho Chi Minh City', 9.2, '2000-06-11' UNION ALL
  SELECT 'STU024', '60 Ly Tu Trong, Hai Phong', 8.0, '2004-12-25' UNION ALL
  SELECT 'STU025', '70 Quang Trung, Da Lat', 7.5, '2001-04-16' UNION ALL
  SELECT 'STU026', '80 Nguyen Hue, Nha Trang', 8.7, '2002-08-07' UNION ALL
  SELECT 'STU027', '90 Tran Hung Dao, Hue', 7.3, '2003-11-29' UNION ALL
  SELECT 'STU028', '100 Le Loi, Da Nang', 8.2, '2000-03-05' UNION ALL
  SELECT 'STU029', '110 Bach Dang, Ho Chi Minh City', 7.8, '2004-07-21' UNION ALL
  SELECT 'STU030', '120 Nguyen Trai, Can Tho', 8.5, '2002-12-12'
) v ON s.student_code = v.student_code
WHERE NOT EXISTS (SELECT 1 FROM student_info si WHERE si.student_id = s.student_id);
