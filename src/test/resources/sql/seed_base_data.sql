INSERT INTO users (id, username, email, password, full_name, nim, angkatan, program_studi, jurusan, tahun_lulus, phone_number, alamat, status, role, created_at, updated_at)
VALUES
    (1, 'admin', 'admin@example.com', '$2a$10$I4z0CuHwd8hYIjzx40pjo.8ZsqI1evfTdMq.HkGyu1.I3br46lN4e', 'Test admin', '1000000000001', 62, 'D4', 'D4 statistik', 2024, '081234567891', 'Test Address Admin', 'ACTIVE', 'ADMIN', TIMESTAMP '2024-01-01 00:00:00', TIMESTAMP '2024-01-01 00:00:00'),
    (2, 'user.one', 'user1@example.com', '$2a$10$aisSWk3tLwMNqe2FYFpZferf2.bDpyQc/O0dXVLYKk.F/zo4jNVhC', 'Test user.one', '1000000000002', 62, 'D4', 'D4 komputasi statistik', 2024, '081234567892', 'Test Address User One', 'ACTIVE', 'USER', TIMESTAMP '2024-01-01 00:00:00', TIMESTAMP '2024-01-01 00:00:00'),
    (3, 'user.two', 'user2@example.com', '$2a$10$aisSWk3tLwMNqe2FYFpZferf2.bDpyQc/O0dXVLYKk.F/zo4jNVhC', 'Test user.two', '1000000000003', 62, 'D3', 'D3 statistik', 2024, '081234567893', 'Test Address User Two', 'ACTIVE', 'USER', TIMESTAMP '2024-01-01 00:00:00', TIMESTAMP '2024-01-01 00:00:00');

INSERT INTO institutions (id, name, type, province, city, address, description, created_at, updated_at)
VALUES
    (1, 'BPS Kota Jakarta', 'BPS_KOTA_KABUPATEN', 'DKI Jakarta', 'Jakarta', 'Jl. Jakarta No. 1', 'Integration test data for BPS Kota Jakarta', TIMESTAMP '2024-01-01 00:00:00', TIMESTAMP '2024-01-01 00:00:00'),
    (2, 'Perusahaan Swasta Bandung', 'SWASTA', 'Jawa Barat', 'Bandung', 'Jl. Bandung No. 2', 'Integration test data for Perusahaan Swasta Bandung', TIMESTAMP '2024-01-01 00:00:00', TIMESTAMP '2024-01-01 00:00:00');

INSERT INTO work_histories (id, user_id, institution_id, position, employment_type, start_date, end_date, is_current_job, description, created_at, updated_at)
VALUES
    (1, 2, 1, 'Statistician', 'ASN', DATE '2020-01-01', NULL, TRUE, 'Analyzes statistical data', TIMESTAMP '2024-01-01 00:00:00', TIMESTAMP '2024-01-01 00:00:00'),
    (2, 3, 2, 'Data Analyst', 'SWASTA', DATE '2021-05-01', NULL, TRUE, 'Works on private sector analytics', TIMESTAMP '2024-01-01 00:00:00', TIMESTAMP '2024-01-01 00:00:00');
