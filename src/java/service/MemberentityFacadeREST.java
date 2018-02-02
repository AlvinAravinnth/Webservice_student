package service;

import Entity.Itementity;
import Entity.Lineitementity;
import Entity.Member;
import Entity.Memberentity;
import Entity.Qrphonesyncentity;
import Entity.ShoppingCartLineItem;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.GenericEntity;

@Stateless
@Path("entity.memberentity")
public class MemberentityFacadeREST extends AbstractFacade<Memberentity> {

    @PersistenceContext(unitName = "WebService")
    private EntityManager em;

    public MemberentityFacadeREST() {
        super(Memberentity.class);
    }

    @POST
    @Override
    @Consumes({"application/xml", "application/json"})
    public void create(Memberentity entity) {
        super.create(entity);
    }

    @DELETE
    @Path("{id}")
    public void remove(@PathParam("id") Long id) {
        super.remove(super.find(id));
    }

    @GET
    @Path("members")
    @Produces({"application/json"})
    public List<Memberentity> listAllMembers() {
        Query q = em.createQuery("Select s from Memberentity s where s.isdeleted=FALSE");
        List<Memberentity> list = q.getResultList();
        for (Memberentity m : list) {
            em.detach(m);
            m.setCountryId(null);
            m.setLoyaltytierId(null);
            m.setLineitementityList(null);
            m.setWishlistId(null);
        }
        List<Memberentity> list2 = new ArrayList();
        list2.add(list.get(0));
        return list;
    }

    @POST
    @Path("updateMember")
    @Consumes("application/x-www-form-urlencoded")
    public Response updateMember(@FormParam("name") String name, @FormParam("email") String email, @FormParam("phone") String phone,
            @FormParam("country") String country, @FormParam("address") String address, @FormParam("SecurityQn") int SecurityQn,
            @FormParam("SecurityAns") String SecurityAns, @FormParam("age") int age, @FormParam("income") int income,
            @FormParam("serviceLevelAgreement") int svcLvlAgreement, @FormParam("passwordSalt") String passwordSalt,@FormParam("passwordHash") String passwordHash) {
            
            try{
                String stmt = "";
                PreparedStatement ps;
                Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/islandfurniture-it07?zeroDateTimeBehavior=convertToNull&user=root&password=12345");
                
                if(passwordHash.isEmpty() && passwordSalt.isEmpty()){
                    stmt = "UPDATE memberentity SET NAME=?, PHONE=?, CITY=?, ADDRESS=?, SECURITYQUESTION=?,"
                            +"SECURITYANSWER=?, AGE=?, INCOME=?, SERVICELEVELAGREEMENT=? WHERE EMAIL=?";
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
                }
                else{
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
    
    
    //this function is used by ECommerce_MemberLoginServlet
    @GET
    @Path("login")
    @Produces("application/json")
    public Response loginMember(@QueryParam("email") String email, @QueryParam("password") String password) {
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
                return Response.ok(email, MediaType.APPLICATION_JSON).build();
            } else {
                System.out.println("Login credentials provided were incorrect, password wrong.");
                return Response.status(Response.Status.UNAUTHORIZED).build();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
    }
    
    @GET
    @Path("getMember")
    @Produces("application/json")
    public Response getMember(@QueryParam("email") String email) {
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/islandfurniture-it07?zeroDateTimeBehavior=convertToNull&user=root&password=12345");
            String stmt = "SELECT * FROM memberentity m WHERE m.EMAIL=?";
            PreparedStatement ps = conn.prepareStatement(stmt);
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            rs.next();
            Member member = new Member();
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
            
             GenericEntity<Member> entity = new GenericEntity<Member>(member) {
            };
             return Response
                    .status(200)
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
                    .header("Access-Control-Allow-Credentials", "true")
                    .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
                    .header("Access-Control-Max-Age", "1209600")
                    .entity(entity)
                    .build();
        } catch (Exception ex) {
            ex.printStackTrace();
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
    }
    
    @GET
    @Path("getOrderItem")
    @Produces("application/json")
    public Response getOrderItem(@QueryParam("memberId") long memberId) {
        ArrayList<ShoppingCartLineItem> itemList = new ArrayList<ShoppingCartLineItem>();
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
            
            GenericEntity<ArrayList<ShoppingCartLineItem>> entity = new GenericEntity<ArrayList<ShoppingCartLineItem>>(itemList) {
            };
            
             return Response
                    .status(200)
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
                    .header("Access-Control-Allow-Credentials", "true")
                    .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
                    .header("Access-Control-Max-Age", "1209600")
                    .entity(entity)
                    .build();
        } catch (Exception ex) {
            ex.printStackTrace();
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
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

    @GET
    @Path("uploadShoppingList")
    @Produces({"application/json"})
    public String uploadShoppingList(@QueryParam("email") String email, @QueryParam("shoppingList") String shoppingList) {
        System.out.println("webservice: uploadShoppingList called");
        System.out.println(shoppingList);
        try {
            Query q = em.createQuery("select m from Memberentity m where m.email=:email and m.isdeleted=false");
            q.setParameter("email", email);
            Memberentity m = (Memberentity) q.getSingleResult();
            List<Lineitementity> list = m.getLineitementityList();
            if (!list.isEmpty()) {
                for (Lineitementity lineItem : list) {
                    em.refresh(lineItem);
                    em.flush();
                    em.remove(lineItem);
                }
            }
            m.setLineitementityList(new ArrayList<Lineitementity>());
            em.flush();

            Scanner sc = new Scanner(shoppingList);
            sc.useDelimiter(",");
            while (sc.hasNext()) {
                String SKU = sc.next();
                Integer quantity = Integer.parseInt(sc.next());
                if (quantity != 0) {
                    q = em.createQuery("select i from Itementity i where i.sku=:SKU and i.isdeleted=false");
                    q.setParameter("SKU", SKU);
                    Itementity item = (Itementity) q.getSingleResult();

                    Lineitementity lineItem = new Lineitementity();

                    lineItem.setItemId(item);
                    lineItem.setQuantity(quantity);
                    System.out.println("Item: " + item.getSku());
                    System.out.println("Quantity: " + quantity);
                    m.getLineitementityList().add(lineItem);
                }
            }
            return "success";
            //return s;
        } catch (Exception e) {
            e.printStackTrace();
            return "fail";
        }
    }

    @GET
    @Path("syncWithPOS")
    @Produces({"application/json"})
    public String tieMemberToSyncRequest(@QueryParam("email") String email, @QueryParam("qrCode") String qrCode) {
        System.out.println("tieMemberToSyncRequest() called");
        try {
            Query q = em.createQuery("SELECT p from Qrphonesyncentity p where p.qrcode=:qrCode");
            q.setParameter("qrCode", qrCode);
            Qrphonesyncentity phoneSyncEntity = (Qrphonesyncentity) q.getSingleResult();
            if (phoneSyncEntity == null) {
                return "fail";
            } else {
                phoneSyncEntity.setMemberemail(email);
                em.merge(phoneSyncEntity);
                em.flush();
                return "success";
            }
        } catch (Exception ex) {
            System.out.println("tieMemberToSyncRequest(): Error");
            ex.printStackTrace();
            return "fail";
        }
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

}
