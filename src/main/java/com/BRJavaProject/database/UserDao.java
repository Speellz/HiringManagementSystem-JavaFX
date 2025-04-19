package com.BRJavaProject.database;

import com.BRJavaProject.model.User;
import com.BRJavaProject.utils.PasswordUtils;
import java.sql.*;

public class UserDao {
    private final Connection connection;

    public UserDao() {
        connection = DatabaseConnection.getConnection();
    }

    public boolean addUser(User user) {
        try {
            String query = "INSERT INTO Users (Name, Surname, email, Phone, Username, Password, UserRole) VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, user.getName());
            statement.setString(2, user.getSurname());
            statement.setString(3, user.getEmail());
            statement.setString(4, user.getPhone());
            statement.setString(5, user.getUsername());
            statement.setString(6, PasswordUtils.hashPassword(user.getPassword()));
            statement.setString(7, user.getUserRole());

            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error adding user: " + e.getMessage());
            return false;
        }
    }


    public User loginUser(String username, String password) {
        String query = "SELECT * FROM Users WHERE Username = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                String storedPassword = rs.getString("Password");

                if (PasswordUtils.verifyPassword(password, storedPassword)) {
                    return new User(
                            rs.getInt("UserID"),
                            rs.getString("Name"),
                            rs.getString("Surname"),
                            rs.getString("email"),
                            rs.getString("Phone"),
                            rs.getString("Username"),
                            storedPassword,
                            rs.getString("UserRole")
                    );
                } else {
                    System.err.println("Incorrect password for user: " + username);
                }
            } else {
                System.err.println("User not found: " + username);
            }
        } catch (SQLException e) {
            System.err.println("Error executing query: " + query + " for username: " + username);
            e.printStackTrace();
        }
        return null;
    }

    public User authenticate(String username, String password) {
        String query = "SELECT * FROM Users WHERE username = ? AND password = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new User(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("surname"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("role")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public String getUserNameById(int userId) {
        String query = "SELECT Name FROM Users WHERE UserID = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userId);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                return rs.getString("Name");
            }
        } catch (SQLException e) {
            System.err.println("Error fetching user name by ID: " + e.getMessage());
        }
        return "N/A";
    }

    public User getLoggedInUser(int userId) {
        String sql = "SELECT * FROM Users WHERE UserID = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new User(
                            rs.getInt("UserID"),
                            rs.getString("Name"),
                            rs.getString("Surname"),
                            rs.getString("email"),
                            rs.getString("Phone"),
                            rs.getString("Username"),
                            rs.getString("Password"),
                            rs.getString("UserRole")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean updateUser(User user) {
        String sql = "UPDATE Users SET Name = ?, Surname = ?, email = ?, Phone = ?, Username = ?, Password = ?, UserRole = ? WHERE UserID = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, user.getName());
            pstmt.setString(2, user.getSurname());
            pstmt.setString(3, user.getEmail());
            pstmt.setString(4, user.getPhone());
            pstmt.setString(5, user.getUsername());
            pstmt.setString(6, user.getPassword());
            pstmt.setString(7, user.getUserRole());
            pstmt.setInt(8, user.getUserId());
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error updating user: " + e.getMessage());
        }
        return false;
    }

    public boolean deleteUser(int userId) {
        String sql = "DELETE FROM Users WHERE UserID = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error deleting user: " + e.getMessage());
        }
        return false;
    }

    public User getUserByUsername(String username) {
        String query = "SELECT * FROM Users WHERE username = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new User(
                        rs.getInt("UserID"),
                        rs.getString("Name"),
                        rs.getString("Surname"),
                        rs.getString("email"),
                        rs.getString("Phone"),
                        rs.getString("Username"),
                        rs.getString("Password"),
                        rs.getString("UserRole")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void saveOTP(String email, String otp) {
        String sql = "UPDATE Users SET otp = ? WHERE email = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, otp);
            stmt.setString(2, email);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean verifyOTP(String email, String otp) {
        String sql = "SELECT otp FROM Users WHERE email = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String storedOtp = rs.getString("otp");
                return storedOtp != null && storedOtp.equals(otp);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void updatePassword(String email, String hashedPassword) {
        String sql = "UPDATE Users SET Password = ?, otp = NULL WHERE email = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, hashedPassword);
            stmt.setString(2, email);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean emailExists(String email) {
        String sql = "SELECT COUNT(*) FROM Users WHERE email = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

}