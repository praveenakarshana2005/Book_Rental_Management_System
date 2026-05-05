CREATE DATABASE IF NOT EXISTS bookrental CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE bookrental;

CREATE TABLE roles (
  id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(20) NOT NULL UNIQUE
);

CREATE TABLE users (
  id INT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(50) NOT NULL UNIQUE,
  password_hash VARCHAR(255) NOT NULL,
  full_name VARCHAR(100),
  role_id INT NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (role_id) REFERENCES roles(id)
);

CREATE TABLE books (
  id INT AUTO_INCREMENT PRIMARY KEY,
  isbn VARCHAR(20),
  title VARCHAR(255) NOT NULL,
  author VARCHAR(255),
  category VARCHAR(100),
  total_quantity INT NOT NULL DEFAULT 0,
  available_quantity INT NOT NULL DEFAULT 0,
  price DECIMAL(10,2) DEFAULT 0.00,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE customers (
  id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(200) NOT NULL,
  email VARCHAR(150),
  phone VARCHAR(30),
  address TEXT,
  membership_no VARCHAR(50) UNIQUE,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE rentals (
  id INT AUTO_INCREMENT PRIMARY KEY,
  rental_no VARCHAR(50) UNIQUE NOT NULL,
  customer_id INT NOT NULL,
  user_id INT NOT NULL,
  issue_date DATE NOT NULL,
  due_date DATE NOT NULL,
  return_date DATE DEFAULT NULL,
  status ENUM('issued','returned','partially_returned','cancelled') DEFAULT 'issued',
  total_fine DECIMAL(10,2) DEFAULT 0.00,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (customer_id) REFERENCES customers(id),
  FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE rental_items (
  id INT AUTO_INCREMENT PRIMARY KEY,
  rental_id INT NOT NULL,
  book_id INT NOT NULL,
  quantity INT NOT NULL DEFAULT 1,
  per_day_fine DECIMAL(8,2) DEFAULT 0.00,
  returned_quantity INT DEFAULT 0,
  FOREIGN KEY (rental_id) REFERENCES rentals(id) ON DELETE CASCADE,
  FOREIGN KEY (book_id) REFERENCES books(id)
);
