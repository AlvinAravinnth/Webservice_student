/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DBmodelLayer;

import java.sql.*;
import java.text.SimpleDateFormat;

/**
 *
 * @author mandf
 */
public class salesrecordentityDB {
    private String stmt = "";
    private PreparedStatement ps;
    
    public int insertSalesRecord(double amountPaid, int memberId, int storeId) throws SQLException{
        int salesRecordId = -1;
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/islandfurniture-it07?zeroDateTimeBehavior=convertToNull&user=root&password=12345");
            stmt = "INSERT INTO salesrecordentity (AMOUNTDUE,AMOUNTPAID,CREATEDDATE,CURRENCY,MEMBER_ID,STORE_ID) VALUES (?,?,?,?,?,?)";
            ps = conn.prepareStatement(stmt, Statement.RETURN_GENERATED_KEYS);
            ps.setDouble(1, amountPaid);
            ps.setDouble(2, amountPaid);
            java.util.Date dt = new java.util.Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String datePurchased = sdf.format(dt);
            ps.setString(3, datePurchased);
            ps.setString(4, "SGD");
            ps.setInt(5, memberId);
            ps.setInt(6, storeId);
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                salesRecordId = rs.getInt(1);
            }
        } catch(Exception ex) {
            throw ex;
        }
        return salesRecordId;
    }
}
