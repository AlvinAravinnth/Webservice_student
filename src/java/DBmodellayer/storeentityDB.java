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

public class storeentityDB {
    private String stmt = "";
    private PreparedStatement ps;
    
    public int getStoreId(String name) throws SQLException{
        int id = -1;
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/islandfurniture-it07?zeroDateTimeBehavior=convertToNull&user=root&password=12345");

            stmt = "SELECT * FROM storeentity WHERE NAME=?";
            ps = conn.prepareStatement(stmt);
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            rs.next();
            id = rs.getInt("ID");
        } catch (Exception ex) {
            throw ex;
        }
        return id;
    }
    
    public int getQuantity(Long storeId, String sku) throws SQLException {
        int quantity = -1;
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/islandfurniture-it07?zeroDateTimeBehavior=convertToNull&user=root&password=12345");
            String stmt = "SELECT sum(l.QUANTITY) as sum FROM storeentity s, warehouseentity w, storagebinentity sb, storagebinentity_lineitementity sbli, lineitementity l, itementity i where s.WAREHOUSE_ID=w.ID and w.ID=sb.WAREHOUSE_ID and sb.ID=sbli.StorageBinEntity_ID and sbli.lineItems_ID=l.ID and l.ITEM_ID=i.ID and s.ID=? and i.SKU=?";
            PreparedStatement ps = conn.prepareStatement(stmt);
            ps.setLong(1, storeId);
            ps.setString(2, sku);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                quantity = rs.getInt("sum");
            }
        } catch (Exception ex) {
            throw ex;
        }
        return quantity;
    }
    
    public String getStoreAddress(String storeName) throws SQLException {
        String address = "";
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/islandfurniture-it07?zeroDateTimeBehavior=convertToNull&user=root&password=12345");
            String stmt = "SELECT * FROM storeentity WHERE NAME = ?";
            PreparedStatement ps = conn.prepareStatement(stmt);
            ps.setString(1, storeName);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                address = rs.getString("ADDRESS");
            }
        } catch (Exception ex) {
            throw ex;
        }
        return address;
    }
}
