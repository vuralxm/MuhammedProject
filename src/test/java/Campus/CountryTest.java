package Campus;

import Campus.Model.Country;
import io.restassured.http.ContentType;
import io.restassured.http.Cookies;
import org.apache.commons.lang3.RandomStringUtils;
import org.testng.annotations.*;
import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class CountryTest  {// testler için her daim login olmamız lazım. bunun için before class ile login işlemlerini yapacağız.

    Cookies cookies; // cookies in içinde token var. bunu bütün sorgularda kullanmamız gerekiyor. o yüzden buraya global değişken olarak koyup testlerde çağıracağız.

    @BeforeClass
    public void loginCampus(){
        baseURI ="https://test.mersys.io/"; // hoca demo mersys ile girmişti ama biz test.mersys kullanıyoruz.

        Map<String, String> credential = new HashMap<>(); // tek sefer kullanacaksak nesne yapmamıza gerek yok. o yüzden map ile yazdık.
        credential.put("username","turkeyts");
        credential.put("password","TechnoStudy123");
        credential.put("rememberMe","true");

        cookies=
        given()
                .contentType(ContentType.JSON)
                .body(credential)

                .when()
                .post("auth/login")

                .then()
                //.log().cookies() // .log().all()ile her şeyi gördük. .log().cookies ile sadece cookies i aldık. içinde token lar var
                .statusCode(200)
                .extract().response().getDetailedCookies() //get detailedcookies ile sorgudan cookies olarak dönenleri alıyoruz.
                // login olduğumuzda site bize token veriyor ve her işlemde tokenı eklememiz lazım yoksa testler çalışmaz. şimdi sorguda dönen tokenı  aşağıya ekleyeceğiz.

                ;
    }
    String countryID;
    String countryName;
    String countryCode;

    @Test
    public void createCountry(){

        countryName=getRandomName(); //bunlar otomatik olarak oluşturulacak.
        countryCode=getRandomCode();

        Country country=new Country();
        country.setName(countryName);
        country.setCode(countryCode);

        countryID = // daha sonra kullanmak için id yi alacağız.

        given()
                .cookies(cookies)
                .contentType(ContentType.JSON)
                .body(country)


                .when()
                .post("school-service/api/countries")


                .then()
                .log().body()
                .statusCode(201)
                .extract().jsonPath().getString("id")

        ;

    }


    public String getRandomName(){ // ülke ismi ve kodunu otomatik alacaz.
        return RandomStringUtils.randomAlphabetic(8).toLowerCase();}
    public String getRandomCode(){
        return RandomStringUtils.randomAlphabetic(3).toLowerCase();}


    @Test(dependsOnMethods = "createCountry")
    public void createCountryNegativeTest(){

        Country country=new Country();
        country.setName(countryName);
        country.setCode(countryCode);

       //countryID = // daha sonra kullanmak için id yi alacağız.

                given()

                        .cookies(cookies)
                        .contentType(ContentType.JSON)
                        .body(country)


                        .when()
                        .post("school-service/api/countries")


                        .then()
                        .log().body()
                        .statusCode(400)
                        .body("message",equalTo("The Country with Name \""+countryName+"\" already exists."))

        ;

    }
    @Test(dependsOnMethods = "createCountry")
    public void updateCountry()
    {
        countryName=getRandomName();

        Country country=new Country();
        country.setId(countryID);
        country.setName(countryName);
        country.setCode(countryCode);

        given()

                .cookies(cookies)
                .contentType(ContentType.JSON)
                .body(country)

                .when()
                .put("school-service/api/countries")

                .then()
                .log().body()
                .statusCode(200)
                .body("name", equalTo(countryName))
        ;

    }

    @Test(dependsOnMethods = "updateCountry")
    public void deleteCountryById()
    {
        given()
                .cookies(cookies)
                .pathParam("countryID", countryID)

                .when()
                .delete("school-service/api/countries/{countryID}")

                .then()
                .log().body()
                .statusCode(208)
        ;
    }

    @Test(dependsOnMethods = "deleteCountryById")
    public void deleteCountryByIdNegative()
    {
        given()
                .cookies(cookies)
                .pathParam("countryID", countryID)
                .log().uri()
                .when()
                .delete("school-service/api/countries/{countryID}")

                .then()
                .log().body()
                .statusCode(400)
        ;
    }

    @Test(dependsOnMethods = "deleteCountryById")
    public void updateCountryNegativeTest()
    {
        countryName=getRandomName();

        Country country=new Country();
        country.setId(countryID);
        country.setName(countryName);
        country.setCode(countryCode);

        given()

                .cookies(cookies)
                .contentType(ContentType.JSON)
                .body(country)

                .when()
                .put("school-service/api/countries")

                .then()
                .log().body()
                .statusCode(400)
        ;

    }



}
