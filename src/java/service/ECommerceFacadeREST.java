package service;

import DBmodelLayer.itementityDB;
import DBmodelLayer.lineitementityDB;
import DBmodelLayer.salesrecordentityDB;
import DBmodelLayer.salesrecordentity_lineitementityDB;
import DBmodelLayer.storeentityDB;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
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
    
    @POST
    @Path("CreateECommerceTransactionRecord")
    @Consumes("application/x-www-form-urlencoded")
    public Response CreateECommerceTransactionRecord(@FormParam("amountPaid") double amountPaid,
            @FormParam("memberId") int memberId, @FormParam("storeName") String storeName) {
            
            try{
                storeentityDB storeDb = new storeentityDB();
                int storeId = storeDb.getStoreId(storeName);
                
                salesrecordentityDB salesRecordDb = new salesrecordentityDB();
                int salesRecordId = salesRecordDb.insertSalesRecord(amountPaid, memberId, storeId);
                
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
            itementityDB itemDb = new itementityDB();
            int itemId = itemDb.getItemId(sku);

            lineitementityDB lineItemDb = new lineitementityDB();
            int lineItemId = lineItemDb.insertLineItemRecord(quantity, itemId);

            salesrecordentity_lineitementityDB srliDb = new salesrecordentity_lineitementityDB();
            srliDb.insertSalesRecord_LineItemRecord(transactionRecordId, lineItemId);

            int dbQuantity = lineItemDb.getECommerceStoreQuantity(itemId);
            dbQuantity -= quantity;

            lineItemDb.updateECommerceStoreQuantity(dbQuantity, itemId);

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
}
