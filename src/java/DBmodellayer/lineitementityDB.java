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
public class lineitementityDB {
    private String stmt = "";
    private PreparedStatement ps;
    
    public int insertLineItemRecord(int quantity, int itemId) {
        int recordId = -1;
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/islandfurniture-it07?zeroDateTimeBehavior=convertToNull&user=root&password=12345");
            stmt = "INSERT INTO lineitementity (QUANTITY,ITEM_ID) VALUES (?,?)";
            ps = conn.prepareStatement(stmt,Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, quantity);
            ps.setInt(2, itemId);
            ps.executeUpdate();
            
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                recordId = rs.getInt(1);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return recordId;
    }
    
    public int getECommerceStoreQuantity(int itemId) {
        int quantity = -1;
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/islandfurniture-it07?zeroDateTimeBehavior=convertToNull&user=root&password=12345");
            stmt = "SELECT * FROM lineitementity WHERE ITEM_ID=? AND ID IN ("
                    + "SELECT lineItems_ID FROM storagebinentity_lineitementity WHERE StorageBinEntity_ID = ("
                    + "SELECT ID FROM storagebinentity WHERE WAREHOUSE_ID = ("
                    + "SELECT WAREHOUSE_ID FROM storeentity WHERE NAME='ECommerce Store')"
                    + "))";
            ps = conn.prepareStatement(stmt);
            ps.setInt(1, itemId);
            ResultSet rs = ps.executeQuery();
            rs.next();
           quantity = rs.getInt("QUANTITY");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return quantity;
    }
    
    public void updateECommerceStoreQuantity(int quantity, int itemId) throws SQLException{
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/islandfurniture-it07?zeroDateTimeBehavior=convertToNull&user=root&password=12345");
            stmt = "UPDATE lineitementity SET QUANTITY=? WHERE ITEM_ID=? AND ID IN "
                    + "(SELECT lineItems_ID FROM storagebinentity_lineitementity WHERE StorageBinEntity_ID = "
                    + "(SELECT ID FROM storagebinentity WHERE WAREHOUSE_ID = "
                    + "(SELECT WAREHOUSE_ID FROM storeentity WHERE NAME = 'ECommerce Store')))";
            ps = conn.prepareStatement(stmt);
            ps.setInt(1, quantity);
            ps.setInt(2, itemId);
            ps.executeUpdate();
        } catch (Exception ex) {
            throw ex;
        }
    }
}
