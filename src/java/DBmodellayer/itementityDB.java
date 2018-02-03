/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DBmodelLayer;

import Entity.ShoppingCartLineItem;
import java.sql.*;
import java.util.ArrayList;

/**
 *
 * @author mandf
 */
public class itementityDB {
    private String stmt = "";
    private PreparedStatement ps;
    
    public int getItemId(String sku) throws SQLException {
        int id = -1;
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/islandfurniture-it07?zeroDateTimeBehavior=convertToNull&user=root&password=12345");
            stmt = "SELECT * FROM itementity WHERE SKU=?";
            ps = conn.prepareStatement(stmt);
            ps.setString(1, sku);
            ResultSet rs = ps.executeQuery();
            rs.next();
            id = rs.getInt("ID");
        } catch (Exception ex) {
            throw ex;
        }
        return id;
    }
    
    public ArrayList<ShoppingCartLineItem> getOrderedItems(long memberId) throws SQLException {
        ArrayList<ShoppingCartLineItem> itemList = new ArrayList<>();
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/islandfurniture-it07?zeroDateTimeBehavior=convertToNull&user=root&password=12345");
            String stmt = "SELECT i.SKU,i.NAME,ic.RETAILPRICE,li.QUANTITY,sr.CREATEDDATE,f.IMAGEURL,s.NAME AS 'STORENAME',s.ADDRESS,sr.ID "
                    + "FROM itementity i,item_countryentity ic,lineitementity li,salesrecordentity sr,"
                    + "salesrecordentity_lineitementity sl,furnitureentity f,storeentity s "
                    + "WHERE sr.MEMBER_ID=? AND "
                    + "i.ID=ic.ITEM_ID AND "
                    + "ic.COUNTRY_ID=25 AND "
                    + "li.ITEM_ID=i.ID AND "
                    + "sr.ID=sl.SalesRecordEntity_ID AND "
                    + "li.ID=sl.itemsPurchased_ID AND "
                    + "f.ID=i.ID AND "
                    + "s.ID = sr.STORE_ID";
            PreparedStatement ps = conn.prepareStatement(stmt);
            ps.setLong(1, memberId);
            
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ShoppingCartLineItem item = new ShoppingCartLineItem();
                item.setSKU(rs.getString("SKU"));
                item.setName(rs.getString("NAME"));
                item.setPrice(rs.getDouble("RETAILPRICE"));
                item.setQuantity(rs.getInt("QUANTITY"));
                item.setImageURL(rs.getString("IMAGEURL"));
                item.setDatePurchased(rs.getDate("CREATEDDATE"));
                item.setTimePurchased(rs.getTime("CREATEDDATE"));
                item.setStoreName(rs.getString("STORENAME"));
                item.setStoreAddress(rs.getString("ADDRESS"));
                item.setOrderId(rs.getInt("ID"));
                itemList.add(item);
            }
        } catch (Exception ex) {
            throw ex;
        }
        return itemList;
    }
}
