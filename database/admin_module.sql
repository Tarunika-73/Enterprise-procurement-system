-- Admin module migration for the existing schema.
-- The application stores role names in title case and converts them to ROLE_ADMIN
-- when creating Spring Security authorities.
INSERT INTO roles (name, description, is_deleted)
SELECT 'Admin', 'System Administrator', FALSE
WHERE NOT EXISTS (SELECT 1 FROM roles WHERE LOWER(name) = 'admin');

-- Create the first admin only after choosing an existing department id and a BCrypt
-- password hash. Generate the hash with Spring's BCryptPasswordEncoder; do not store
-- plaintext passwords in SQL scripts.
--
-- INSERT INTO users
--   (employee_id, first_name, last_name, email, password_hash, role_id, department_id, is_active, is_deleted)
-- SELECT 'ADMIN001', 'System', 'Administrator', 'admin@your-company.com',
--        '<bcrypt-password-hash>', r.id, <existing_department_id>, TRUE, FALSE
-- FROM roles r
-- WHERE LOWER(r.name) = 'admin';
