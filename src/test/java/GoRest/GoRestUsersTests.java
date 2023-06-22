package GoRest;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class GoRestUsersTests {

    @BeforeClass
    void Setup() {
        baseURI = "https://gorest.co.in/public/v2/";
    }

    public String getRandomName() {
        return RandomStringUtils.randomAlphabetic(8);
    }

    public String getRandomEmail() {return RandomStringUtils.randomAlphabetic(8) + "@gmail.com";}

    int userID = 0;
    User newUser; // burada newUser ı da global yapıp aşğıdan çağırabiliriz.

    @Test
    public void createUserObject() {

        newUser = new User();
        newUser.setName(getRandomName());
        newUser.setGender("male");
        newUser.setEmail(getRandomEmail());
        newUser.setStatus("active");

        userID =
                given() // Api metoduna gitmeden önceki hazırlıklar burada yapılıyordu.  token, gidecek body, parametreler

                        .header("Authorization", "Bearer 51f5f7b869b56ab1c893570974680bfc357c9eec93e4fe69f550d9cd0969ac36")
                        .contentType(ContentType.JSON)
                        .body(newUser) // daha önce burada bütün bilgileri veriyorduk. şimdi sadece yukarıdan çağırdık.
                        .log().body()
                        .when()
                        .post("users")

                        .then()
                        .log().body()
                        .statusCode(201)
                        .contentType(ContentType.JSON)
                        //.extract().path("id") bu kısmı aşağıdaki gibi de yazabiliriz. peki fark ne ?
                        // path: class veya tip dönüşümüne imkan vermeden direkt veriyi verir. List<String> gibi.
                        // Json path ise class ve tip dönüşümüne izin vererek veriyi istediğimiz formatta almamızı sağlar.
                        .extract().jsonPath().getInt("id")

        ;
        System.out.println("userID = " + userID);

    }

    @Test(dependsOnMethods = "createUserObject", priority = 1)
    // update için id yi createuser'dan alması lazım. Postmanda öyle yapmıştık. o yüzden burda depensonMethod ile onu da çalıştırıyoruz.
    public void updateUserObject() // postmande yaptığımız update işlemini şimdi burada yapalım.
    {
        // 1.YÖNTEM Map ile
        //Map<String, String> updateUser = new HashMap<>();
        //updateUser.put("name", "Muhammed VURAL"); // ismi kendimiz verelim. burada sadece update etmek istediğimiz verileri veriyoruz. sadece ismi değiştireceğiz.

        //2. YÖNTEM yukarıda global yaptığımız newUser ile yeni veri göndererek
        newUser.setName("Muhammed VURAL");



        given() // Api metoduna gitmeden önceki hazırlıklar burada yapılıyordu.  token, gidecek body, parametreler


                .header("Authorization", "Bearer 51f5f7b869b56ab1c893570974680bfc357c9eec93e4fe69f550d9cd0969ac36")
                .contentType(ContentType.JSON)
                .body(newUser) // daha önce burada bütün bilgileri veriyorduk. şimdi sadece yukarıdan çağırdık.
                .log().body()
                .pathParam("userID", userID)
                .when()
                .put("users/{userID}")// update için put la parametre vereceğiz.


                .then()
                .log().body()
                .statusCode(200)
                .body("name", equalTo("Muhammed VURAL"))
        ;
        System.out.println("userID = " + userID);

    }

    //   4 Ekim 3. Ders
    @Test(dependsOnMethods = "createUserObject", priority = 2)
    public void getUserByID() { // Postmanda id ile kullanıcı çağırmıştık. şimdi buarada yapacağız.


        given()

                .header("Authorization", "Bearer 51f5f7b869b56ab1c893570974680bfc357c9eec93e4fe69f550d9cd0969ac36")
                .contentType(ContentType.JSON)
                .log().body()
                .pathParam("userID", userID)

                .when()
                .get("users/{userID}")// get ile

                .then()
                .log().body()
                .statusCode(200)
                .body("id", equalTo(userID))
        ;
    }

    @Test(dependsOnMethods = "createUserObject", priority = 3)
    public void deleteUserByID() { // Postmanda yaptığımız delete user işlemini yapalım.

        given()

                .header("Authorization", "Bearer 51f5f7b869b56ab1c893570974680bfc357c9eec93e4fe69f550d9cd0969ac36")
                .contentType(ContentType.JSON)
                .log().body()
                .pathParam("userID", userID)

                .when()
                .delete("users/{userID}")// delete ile sileceğiz.

                .then()
                .log().body()
                .statusCode(204)
                // .body("id", equalTo(userID)) --> sildiğimiz kaydın body si olmaz. o yüzden bunu kapattım.
        ;
    }

    @Test(dependsOnMethods = "deleteUserByID")
    public void deleteAgainForNegativeTest() { // Sildiğimiz kaydı tekrar silmeye çalışacağız.

        given()

                .header("Authorization", "Bearer 51f5f7b869b56ab1c893570974680bfc357c9eec93e4fe69f550d9cd0969ac36")
                .contentType(ContentType.JSON)
                .log().body()
                .pathParam("userID", userID)

                .when()
                .delete("users/{userID}")// delete ile sileceğiz.

                .then()
                .log().body()
                .statusCode(404)
        ; // test nedense gümlüyor. derste de birçok arkadaş gümledi. karşıdan cevap alamıyor. !!!!
    }

    @Test // burada depend i kaldırdık çünkü öncesinde çalışması gereken bir kod yok.
    public void getUsersList() { // Postmanda bütün kullanıcıları listelemiştik. burada da aynısını yapacağız.
        Response response=
                (Response) given()

                        .header("Authorization", "Bearer 51f5f7b869b56ab1c893570974680bfc357c9eec93e4fe69f550d9cd0969ac36")

                        .when()
                        .get("users")// get ile

                        .then()
                        //.log().body()
                        .statusCode(200)
                        .extract().response()
                ;
        //TODO: 3. kullanıcının(user) id sini alınız. (hem path ile hem de JsonPath ile alınız.)
        int idUser3Path = response.path("[2].id");
        int idUser3JsonPath= response.jsonPath().getInt("[2].id");
        System.out.println("idUser3Path = " + idUser3Path);
        System.out.println("idUser3JsonPath = " + idUser3JsonPath);

        // TODO : Tüm gelen veriyi bir nesneye atınız (google araştırması)
        User[] usersPath=response.as(User[].class);
        // "as" kullandığımızda bütün veriyi almak zorundayız.
        // ama "jsonpath" kulladığımızda hepsini almak zorunda değiliz. bir kısmını alıp nesneye atabiliriz.
        System.out.println("Arrays.toString(usersPath) = " + Arrays.toString(usersPath) );

        List<User> usersJsonPath=response.jsonPath().getList("",User.class);
        System.out.println("usersJsonPath = " + usersJsonPath);

    }

    // TODO : GetUserByID testinde dönen user ı bir nesneye atınız.
    @Test
    public void getUserByIdExtract() { // Postmanda id ile kullanıcı çağırmıştık. şimdi buarada yapacağız.

        User user=
        given()

                .header("Authorization", "Bearer 51f5f7b869b56ab1c893570974680bfc357c9eec93e4fe69f550d9cd0969ac36")
                .contentType(ContentType.JSON)
                .pathParam("userID", 2861537)

                .when()
                .get("users/{userID}")// get ile

                .then()
                .log().body()
                .statusCode(200)
                //.extract().as(User.class) ---> bu şekilde alabiliriz.
                .extract().jsonPath().getObject("",User.class) // jsonPath ile de böyle aldık.
        ;
        System.out.println("user = " + user);
    }

        // 4 Ekim 1. ders
        @Test (enabled=false) // aynı işlemi 3 farklı yöntemle yazdık. kullanmayacaklarımızı enabled=false ile kapattık.
        public void createUser()
        {
            int userID =
                    given() // Api metoduna gitmeden önceki hazırlıklar burada yapılıyordu.  token, gidecek body, parametreler


                            .header("Authorization", "Bearer 51f5f7b869b56ab1c893570974680bfc357c9eec93e4fe69f550d9cd0969ac36")
                            .contentType(ContentType.JSON)
                            .body("{\"name\":\"" + getRandomName() + "\", \"gender\":\"male\", \"email\":\"" + getRandomEmail() + "\", \"status\":\"active\"}")
                            .when()
                            .post("users")


                            .then()
                            .log().body()
                            .statusCode(201)
                            .contentType(ContentType.JSON)
                            .extract().path("id");
            System.out.println("userID = " + userID);

        }


        @Test(enabled = false) // bunu da kapattık.
        public void createUserMap()
        {

            Map<String, String> newUser = new HashMap<>(); // yeni kullanıcı oluşturmayı otomatik hale getireceğiz. burada map ile body e gidecek bilgileri veriyoruz.
            newUser.put("name", getRandomName());
            newUser.put("gender", "male");
            newUser.put("email", getRandomEmail());
            newUser.put("status", "active");

            int userID =
                    given() // Api metoduna gitmeden önceki hazırlıklar burada yapılıyordu.  token, gidecek body, parametreler


                            .header("Authorization", "Bearer 51f5f7b869b56ab1c893570974680bfc357c9eec93e4fe69f550d9cd0969ac36")
                            .contentType(ContentType.JSON)
                            .body(newUser) // daha önce burada bütün bilgileri veriyorduk. şimdi sadece yukarıdan çağırdık.
                            .log().body()
                            .when()
                            .post("users")


                            .then()
                            .log().body()
                            .statusCode(201)
                            .contentType(ContentType.JSON)
                            .extract().path("id");
            System.out.println("userID = " + userID);

        }


        class User {
            private int id;
            private String name;
            private String gender;
            private String email;
            private String status;

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getGender() {
                return gender;
            }

            public void setGender(String gender) {
                this.gender = gender;
            }

            public String getEmail() {
                return email;
            }

            public void setEmail(String email) {
                this.email = email;
            }

            public String getStatus() {
                return status;
            }

            public void setStatus(String status) {
                this.status = status;
            }

            @Override
            public String toString() {
                return "User{" +
                        "id=" + id +
                        ", name='" + name + '\'' +
                        ", gender='" + gender + '\'' +
                        ", email='" + email + '\'' +
                        ", status='" + status + '\'' +
                        '}';
            }
        }


    }
