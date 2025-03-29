CREATE DATABASE IF NOT EXISTS movie_db;
CREATE USER 'movie_user'@'%' IDENTIFIED BY 'movie_password';
GRANT ALL PRIVILEGES ON movie_db.* TO 'movie_user'@'%';
FLUSH PRIVILEGES;