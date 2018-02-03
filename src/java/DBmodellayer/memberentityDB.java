/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DBmodelLayer;

import Entity.Member;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.*;
import java.util.Arrays;

/**
 *
 * @author mandf
 */
public class memberentityDB {

    private String stmt = "";
    private PreparedStatement ps;

    public void updateMemberDetails(String name, String email, String phone, String country, String address, int SecurityQn,
            String SecurityAns, int age, int income, int svcLvlAgreement, String passwordSalt, String passwordHash) throws SQLException{
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/islandfurniture-it07?zeroDateTimeBehavior=convertToNull&user=root&password=12345");

            if (passwordHash.isEmpty() && passwordSalt.isEmpty()) {
                stmt = "UPDATE memberentity SET NAME=?, PHONE=?, CITY=?, ADDRESS=?, SECURITYQUESTION=?,"
                        + "SECURITYANSWER=?, AGE=?, INCOME=?, SERVICELEVELAGREEMENT=? WHERE EMAIL=?";
                ps = conn.prepareStatement(stmt);
                ps.setString(1, name);
                ps.setString(2, phone);
                ps.setString(3, country);
                ps.setString(4, address);
                ps.setInt(5, SecurityQn);
                ps.setString(6, SecurityAns);
                ps.setInt(7, age);
                ps.setDouble(8, income);
                ps.setInt(9, svcLvlAgreement);
                ps.setString(10, email);
            } else {
                stmt = "UPDATE memberentity SET NAME=?, PHONE=?, CITY=?, ADDRESS=?, SECURITYQUESTION=?, SECURITYANSWER=?, AGE=?,"
                        + "INCOME=?, SERVICELEVELAGREEMENT=?, PASSWORDSALT=?, PASSWORDHASH=? WHERE EMAIL=?";
                ps = conn.prepareStatement(stmt);
                ps.setString(1, name);
                ps.setString(2, phone);
                ps.setString(3, country);
                ps.setString(4, address);
                ps.setInt(5, SecurityQn);
                ps.setString(6, SecurityAns);
                ps.setInt(7, age);
                ps.setDouble(8, income);
                ps.setInt(9, svcLvlAgreement);
                ps.setString(10, passwordSalt);
                ps.setString(11, passwordHash);
                ps.setString(12, email);
            }

            ps.executeUpdate();
        } catch (Exception ex) {
            throw ex;
        }
    }
    
    public boolean loginMember(String email, String password) throws SQLException {
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/islandfurniture-it07?zeroDateTimeBehavior=convertToNull&user=root&password=12345");
            String stmt = "SELECT * FROM memberentity m WHERE m.EMAIL=?";
            PreparedStatement ps = conn.prepareStatement(stmt);
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            rs.next();
            String passwordSalt = rs.getString("PASSWORDSALT");
            String passwordHash = generatePasswordHash(passwordSalt, password);
            if (passwordHash.equals(rs.getString("PASSWORDHASH"))) {
                return true;
            } else {
                return false;
            }
        } catch (Exception ex) {
            throw ex;
        }
    }
    
    public Member getMember(String email) throws SQLException {
        Member member = new Member();
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/islandfurniture-it07?zeroDateTimeBehavior=convertToNull&user=root&password=12345");
            String stmt = "SELECT * FROM memberentity m WHERE m.EMAIL=?";
            PreparedStatement ps = conn.prepareStatement(stmt);
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            rs.next();
            member.setName(rs.getString("NAME"));
            member.setEmail(rs.getString("EMAIL"));
            member.setCity(rs.getString("CITY"));
            member.setAddress(rs.getString("ADDRESS"));
            member.setAge(rs.getInt("AGE"));
            member.setCumulativeSpending(rs.getDouble("CUMULATIVESPENDING"));
            member.setId(rs.getLong("ID"));
            member.setIncome(rs.getInt("INCOME"));
            member.setLoyaltyPoints(rs.getInt("LOYALTYPOINTS"));
            member.setPhone(rs.getString("PHONE"));
            member.setSecurityAnswer(rs.getString("SECURITYANSWER"));
            member.setSecurityQuestion(rs.getInt("SECURITYQUESTION"));
            int sla = rs.getInt("SERVICELEVELAGREEMENT");
            if(sla == 0){
                member.setServiceLevelAgreement(false);
            }else if(sla == 1){
                member.setServiceLevelAgreement(true);
            }
        } catch (Exception ex) {
            throw ex;
        }
        return member;
    }
       
    public String generatePasswordSalt() {
        byte[] salt = new byte[16];
        try {
            SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
            sr.nextBytes(salt);
        } catch (NoSuchAlgorithmException ex) {
            System.out.println("\nServer failed to generate password salt.\n" + ex);
        }
        return Arrays.toString(salt);
    }

    public String generatePasswordHash(String salt, String password) {
        String passwordHash = null;
        try {
            password = salt + password;
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(password.getBytes());
            byte[] bytes = md.digest();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < bytes.length; i++) {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            passwordHash = sb.toString();
        } catch (NoSuchAlgorithmException ex) {
            System.out.println("\nServer failed to hash password.\n" + ex);
        }
        return passwordHash;
    }
}
