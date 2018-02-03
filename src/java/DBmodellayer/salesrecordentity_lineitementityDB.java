/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DBmodelLayer;

import java.sql.*;

/**
 *
 * @author mandf
 */

public class salesrecordentity_lineitementityDB {
    private String stmt = "";
    private PreparedStatement ps;
    
    public void insertSalesRecord_LineItemRecord(int transactionRecordId, int lineItemId) throws SQLException{
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/islandfurniture-it07?zeroDateTimeBehavior=convertToNull&user=root&password=12345");
            stmt = "INSERT INTO salesrecordentity_lineitementity (SalesRecordEntity_ID,itemsPurchased_ID) VALUES (?,?)";
            ps = conn.prepareStatement(stmt);
            ps.setInt(1, transactionRecordId);
            ps.setInt(2, lineItemId);
            ps.executeUpdate();
        } catch (Exception ex) {
            throw ex;
        }
    }
}
