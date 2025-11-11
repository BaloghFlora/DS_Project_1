-- This script will run on startup
-- It inserts two users: 'admin' (password: adminpass) and 'user' (password: userpass)
-- The passwords have been pre-hashed using BCrypt.

-- Delete existing users to avoid conflicts on restart
DELETE FROM credentials;

-- Insert admin (password: "adminpass")
INSERT INTO credentials (id, username, password, role) 
VALUES 
('a1b2c3d4-0001-0001-0001-000000000001', 
 'admin', 
 '$2a$10$f.o./O.PTwe62.rW61s9O.sL.VqGQc17I5.n.Wd1uTjO1u.0/m0eC', 
 'ROLE_ADMIN,ROLE_USER');

-- Insert user (password: "userpass")
INSERT INTO credentials (id, username, password, role) 
VALUES 
('a1b2c3d4-0002-0002-0002-000000000002', 
 'user', 
 '$2a$10$U.B.P.t.a.c.Q.a.m.e.Q.u.j.y.z.k.R.r.C.qf2/9b2', 
 'ROLE_USER');