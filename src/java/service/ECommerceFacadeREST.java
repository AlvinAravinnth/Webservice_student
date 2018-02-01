package service;

import java.net.URI;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("entity.commerce")
public class ECommerceFacadeREST {

    @Context
    private UriInfo context;

    public ECommerceFacadeREST() {
    }

    @GET
    @Produces("application/json")
    public String getJson() {
        //TODO return proper representation object
        throw new UnsupportedOperationException();
    }

    @POST
    @Path("CreateECommerceTransactionRecord")
    @Consumes("application/x-www-form-urlencoded")
    public Response CreateECommerceTransactionRecord(@FormParam("amountPaid") double amountPaid,
            @FormParam("memberId") int memberId, @FormParam("storeName") String storeName) {
            
            try{
                String stmt = "";
                PreparedStatement ps;
                Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/islandfurniture-it07?zeroDateTimeBehavior=convertToNull&user=root&password=12345");
                
                stmt = "SELECT * FROM storeentity WHERE NAME=?";
                ps = conn.prepareStatement(stmt);
                ps.setString(1, storeName);
                ResultSet rs = ps.executeQuery();
                rs.next();
                int storeId = rs.getInt("ID");
                
                stmt = "INSERT INTO salesrecordentity (AMOUNTDUE,AMOUNTPAID,CREATEDDATE,CURRENCY,MEMBER_ID,STORE_ID) VALUES (?,?,?,?,?,?)";
                ps = conn.prepareStatement(stmt,Statement.RETURN_GENERATED_KEYS);
                ps.setDouble(1, amountPaid);
                ps.setDouble(2, amountPaid);
                Date dt = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String datePurchased = sdf.format(dt);
                ps.setString(3, datePurchased);
                ps.setString(4, "SGD");
                ps.setInt(5, memberId);
                ps.setInt(6, storeId);
                ps.executeUpdate();
                
                int salesRecordId = -1;
                rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    salesRecordId = rs.getInt(1);
                }
                
                return Response.status(200).entity(salesRecordId).build();
            }catch(Exception ex){
                ex.printStackTrace();
                return Response.status(Response.Status.SERVICE_UNAVAILABLE).build();
            }
    }
    
    @POST
    @Path("CreateECommerceLineItemRecord")
    @Consumes("application/x-www-form-urlencoded")
    public Response CreateECommerceLineItemRecord(@FormParam("quantity") int quantity, @FormParam("SKU") String sku, @FormParam("transactionRecordId") int transactionRecordId) {
            
            try{
                String stmt = "";
                PreparedStatement ps;
                Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/islandfurniture-it07?zeroDateTimeBehavior=convertToNull&user=root&password=12345");
                
                stmt = "SELECT * FROM itementity WHERE SKU=?";
                ps = conn.prepareStatement(stmt);
                ps.setString(1, sku);
                ResultSet rs = ps.executeQuery();
                rs.next();
                int itemId = rs.getInt("ID");
                
                stmt = "INSERT INTO lineitementity (QUANTITY,ITEM_ID) VALUES (?,?)";
                ps = conn.prepareStatement(stmt,Statement.RETURN_GENERATED_KEYS);
                ps.setInt(1, quantity);
                ps.setInt(2, itemId);
                ps.executeUpdate();
                
                int lineItemId = -1;
                rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    lineItemId = rs.getInt(1);
                }
                
                stmt = "INSERT INTO salesrecordentity_lineitementity (SalesRecordEntity_ID,itemsPurchased_ID) VALUES (?,?)";
                ps = conn.prepareStatement(stmt);
                ps.setInt(1, transactionRecordId);
                ps.setInt(2, lineItemId);
                ps.executeUpdate();
                
                stmt = "SELECT * FROM lineitementity WHERE ITEM_ID=? AND ID IN ("
                        + "SELECT lineItems_ID FROM storagebinentity_lineitementity WHERE StorageBinEntity_ID = ("
                        + "SELECT ID FROM storagebinentity WHERE WAREHOUSE_ID = ("
                        + "SELECT WAREHOUSE_ID FROM storeentity WHERE NAME='ECommerce Store')"
                        + "))";
                ps = conn.prepareStatement(stmt);
                ps.setInt(1, itemId);
                rs = ps.executeQuery();
                rs.next();
                int dbQuantity = rs.getInt("QUANTITY");
                dbQuantity -= quantity;
                
                stmt = "UPDATE lineitementity SET QUANTITY=? WHERE ITEM_ID=? AND ID IN "
                        + "(SELECT lineItems_ID FROM storagebinentity_lineitementity WHERE StorageBinEntity_ID = "
                        + "(SELECT ID FROM storagebinentity WHERE WAREHOUSE_ID = "
                        + "(SELECT WAREHOUSE_ID FROM storeentity WHERE NAME = 'ECommerce Store')))";
                ps = conn.prepareStatement(stmt);
                ps.setInt(1, dbQuantity);
                ps.setInt(2, itemId);
                ps.executeUpdate();
                
                return Response
                    .status(200)
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
                    .header("Access-Control-Allow-Credentials", "true")
                    .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
                    .header("Access-Control-Max-Age", "1209600")
                    .build();
            }catch(Exception ex){
                ex.printStackTrace();
                return Response.status(Response.Status.SERVICE_UNAVAILABLE).build();
            }
    }
    
    
    /**
     * PUT method for updating or creating an instance of ECommerce
     *
     * @param content representation for the resource
     * @return an HTTP response with content of the updated or created resource.
     */
    @PUT
    @Consumes("application/json")
    public void putJson(String content) {
    }
}
