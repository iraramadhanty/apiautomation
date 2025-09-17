import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import io.restassured.response.Response;
import static io.restassured.RestAssured.given;


public class ApiImplementation {
   String token;
   int idBooking;

   @BeforeClass
   @Test
    public void generate_token(){
        String requestBody = "{\r\n" + //
                        "    \"username\" : \"admin\",\r\n" + //
                        "    \"password\" : \"password123\"\r\n" + //
                        "}";
        Response response = given()
            .baseUri("https://restful-booker.herokuapp.com/auth")
            .header("Content-Type", "application/json")
            .body(requestBody)
        .when()
            .post();
        token = response.jsonPath().getString("token");
    }
   
   @Test
   public void get_booking_ids(){
        Response response = given()
            .baseUri("https://restful-booker.herokuapp.com/booking")
        .when() 
            .get();
    Assert.assertEquals(response.statusCode(), 200, "Status code should be 200");
   } 

   @Test
   public void get_booking(){
    Response response = given()
            .baseUri("https://restful-booker.herokuapp.com/booking/"+idBooking)
            .header("Content-Type", "application/json")
            .header("Accept", "application/json")
            .header("Cookie", "token=" +token)
            .log().all()
        .when()
            .get();
    Assert.assertEquals(response.statusCode(), 200, "Status code should be 200");
   }
   
    @Test 
    public void create_booking(){
        String requestBody = "{\r\n" + //
                        "    \"firstname\" : \"Jim\",\r\n" + //
                        "    \"lastname\" : \"Brown\",\r\n" + //
                        "    \"totalprice\" : 111,\r\n" + //
                        "    \"depositpaid\" : true,\r\n" + //
                        "    \"bookingdates\" : {\r\n" + //
                        "        \"checkin\" : \"2018-01-01\",\r\n" + //
                        "        \"checkout\" : \"2019-01-01\"\r\n" + //
                        "    },\r\n" + //
                        "    \"additionalneeds\" : \"Breakfast\"\r\n" + //
                        "}";
        Response response = given()
            .baseUri("https://restful-booker.herokuapp.com/booking")
            .header("Content-Type", "application/json")
            .body(requestBody)
        .when()
            .post();
        idBooking = response.jsonPath().getInt("bookingid");
        Assert.assertEquals(response.statusCode(), 200, "Status code should be 200");
        Assert.assertTrue(response.jsonPath().getString("booking.lastname").equals("Brown"), "Last name should be Brown" );
    }

    @Test (priority = 2)
    public void update_booking(){
        String requestBody = "{\r\n" + //
                        "    \"firstname\" : \"James\",\r\n" + //
                        "    \"lastname\" : \"Brown\",\r\n" + //
                        "    \"totalprice\" : 111,\r\n" + //
                        "    \"depositpaid\" : true,\r\n" + //
                        "    \"bookingdates\" : {\r\n" + //
                        "        \"checkin\" : \"2018-01-01\",\r\n" + //
                        "        \"checkout\" : \"2019-01-01\"\r\n" + //
                        "    },\r\n" + //
                        "    \"additionalneeds\" : \"Breakfast\"\r\n" + //
                        "}";
        Response response = given()
            .baseUri("https://restful-booker.herokuapp.com/booking/"+idBooking)
            .header("Content-Type", "application/json")
            .header("Accept", "application/json")
            .header("Cookie", "token=" +token)
            .body(requestBody)
            .log().all()
        .when()
            .put();
        System.out.println("Response"+response.asPrettyString());
        Assert.assertEquals(response.statusCode(), 200, "Status code should be 200");
        Assert.assertTrue(response.jsonPath().getString("firstname").equals("James"), "update first name to James");
    }

    @Test (dependsOnMethods = {"create_booking"})
    public void partial_update_booking(){
        String requestBody = "{\r\n" + //
                        "    \"firstname\" : \"James\",\r\n" + //
                        "    \"lastname\" : \"Brown\"\r\n" + //
                        "}";
        Response response = given()
            .baseUri("https://restful-booker.herokuapp.com/booking/"+idBooking)
            .header("Content-Type", "application/json")
            .header("Accept", "application/json")
            .header("Cookie", "token=" +token)
            .body(requestBody)
            .log().all()
        .when()
            .patch();
        System.out.println("Response"+response.asPrettyString());
        Assert.assertEquals(response.statusCode(), 200, "Status code should be 200");
        Assert.assertTrue(response.jsonPath().getString("firstname").equals("James"), "update first name to James");
    }

    @Test (priority = 3)
    public void delete_booking(){
        Response response = given()
            .baseUri("https://restful-booker.herokuapp.com/booking/"+idBooking)
            .header("Content-Type", "application/json")
            .header("Cookie", "token=" +token)
        .when()
            .delete();
        Assert.assertEquals(response.statusCode(), 201, "Status code should be 201");
    }

}
