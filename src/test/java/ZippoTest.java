import POJO.Location;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*; // import ettiğimiz kütüphanenin bütün özelliklerini kullanmak istiyorsak uzantıya "*" yazıyoruz.

public class ZippoTest {
    @Test
    public void statusCodeTest(){

        given()
                // hazırlık işlemleri bu kısımda yapılır. Gerekli veriler buraya girilir.(token, send body, ve parametreler)
                .when()
                // linki ve metodo burada vereceğiz.
                .get("http://api.zippopotam.us/us/90210")

                .then()
                // assertion ve verileri ele alma (extract)
                .log().all() // postman'da gelen sonuç ekranını yazdırmak için log body kullanıyoruz. log.All bütün response u gösterir.
                .statusCode(200)
        ;


    }

    @Test
    public void contentTypeTest(){

        given()

                .when()

                .get("http://api.zippopotam.us/us/90210")

                .then()

                .log().body() // postman'da gelen sonuç ekranını yazdırmak için log body kullanıyoruz. log.All bütün response u gösterir.
                .statusCode(200)
                .contentType(ContentType.JSON)
        ;


    }

    // REST ASSURED 2. GÜN
    @Test
    public void checkStateInResponseBody(){

        given()

                .when()

                .get("http://api.zippopotam.us/us/90210")

                .then()
                // body deki verileri almak için
                // body.country
                // body.'post code' --> eğer arada boşluk olan kelimeleri almak istiyorsak tek tırnak içinde yazıyoruz ki tek parça olduğunu anlasın
                // Eğer body de köşeli parantez içinde veri varsa bu onun bir dizi olduğunu gösterir. her köşeli parantez bir dizidir. bunu köşeli parantez içine index numarası vererek çağırıyoruz.
                // Örnek: body.places[0].'place name' --> body, places teki ilk dizinin place name'ini al demiş olduk.
                // Örnek: body.places[0].state --> body, places teki ilk dizinin state ini istedik.

                .log().body()
                // Hamcrest matcher özelliği body'in içinden herhangi bir parçayı almadan gelen ve beklenen verileri karşılaştırmamızı(doğrulamamızı) sağlıyor.
                // biz sorgulamalarda bu verileri kıyaslamak için Hamcrest Matcher ı kullanacağız.  bunun yanında diğer birkaç özelliği daha var .
                // Bu özellikleri Hamcrest Matcher ı  import ederek kullanacağız.(assertion gibi)
                // equalTo özelliği ile sorgulamadan gelen bilgi ile istediğimiz sonuç eşit mi onu sorguluyoruz.

                .body("country",equalTo("United States")) // burada veriler çift tırnak içinde veriliyor yani string.

                // ilk önce body deki hangi başlığı(ögeyi) kontrol edeceğimizi veriyoruz.(Country)
                // ikinci olarak "equalTo" ile kontrol etmek istediğimiz veriyi yazıyoruz.(United States)

                .statusCode(200)
        ;

    }

    @Test
    public void bodyJsonPathTest2(){

        given()

                .when()

                .get("http://api.zippopotam.us/us/90210")

                .then()

                .log().body()

                .body("places[0].state",equalTo("California")) // places teki ilk dizinin state inde California var mı?

                .statusCode(200)
        ;
    }
    @Test
    public void bodyJsonPathTest3(){

        given()

                .when()

                .get("http://api.zippopotam.us/tr/01000")

                .then()

                .log().body()

                .body("places.'place name'",hasItem("Çaputçu Köyü")) // places teki bütün place name lerin içinde "Çaputçu Köyü" var mı?
                // item olarak arayınca var. equalTo ile arayınca gümledi. dizinin içinde olduğu için köşeli parantez vermek gerekiyor. öyle vermeyince equalto ile gümlüyor.

                .statusCode(200)
        ;

    }

    @Test
    public void bodyArrayHasSizeTest(){

        given()

                .when()

                .get("http://api.zippopotam.us/us/90210")

                .then()

                .log().body()

                .body("places", hasSize(1)) // places te sadece bir eleman mı var ?
                // hasSize ile kaç eleman olduğunu kontrol ediyoruz.

                .statusCode(200)
        ;

    }

    @Test
    public void combiningTest(){ // aynı testte çoklu sorgulama yapacağız.

        given()

                .when()

                .get("http://api.zippopotam.us/us/90210")

                .then()

                .log().body()

                .body("places", hasSize(1))
                .body("places.state", hasItem("California"))
                .body("places[0].'place name'", equalTo("Beverly Hills"))
                .statusCode(200)
        ;

    }

    @Test
    public void pathParamTest(){

        given()
                .pathParam("Country", "us") // url nin sonuna eklenmesi için parametre gönderebiliyoruz. country için us girecek.
                .pathParam("ZipKod", "90210") // böylece linki de test sonucu bize gösteriyor. zipkod için 90210 yazacak
                .log().uri()

                .when()

                .get("http://api.zippopotam.us/{Country}/{ZipKod}")// yukarıda verdiğimiz parametreleri buraya otomatik giriyor.

                .then()

                .log().body()

                .statusCode(200)

        ;

    }

    @Test
    public void pathParamTest2(){
        // SORU: 90210 dan 90214 ye kadar test sonuçlarında places in size ının hepsinde 1 geldiğini test ediniz.

        for(int i=90210; i<90214 ; i++)

        given()
                .pathParam("Country", "us")
                .pathParam("ZipKod", i)
                .log().uri()

                .when()

                .get("http://api.zippopotam.us/{Country}/{ZipKod}")

                .then()

                .log().body()
                .body("places", hasSize(1))

                .statusCode(200)

        ;

    }

    @Test
    public void queryParamTest(){
        // https://gorest.co.in/public/v1/users?page=1  ---> sorguda bu linki kullanacağız.

        for(int pageNo=1; pageNo<11; pageNo++) {
            given()
                    .param("page", 1) // query sorgulamada sadece param kullanılıyor. parametre ve değeri kendisi otomatik yazıyor. soru işareti ve eşittiri de otomatik yazıyor.
                    // paramın kendine özgü bir stili var.
                    .log().uri() // request linkini ekrana yazdırıyor.

                    .when()

                    .get("http://gorest.co.in/public/v1/users")

                    .then()

                    .log().body()
                    .body("meta.pagination.page", equalTo(1))

                    .statusCode(200)
            ;
        }

    }

    RequestSpecification requestSpecs;

    ResponseSpecification responseSpecs;

    @BeforeClass
    void Setup(){
        baseURI="http://gorest.co.in/public/v1";
        requestSpecs = new RequestSpecBuilder()
                .log(LogDetail.URI)
                .setAccept(ContentType.JSON)
                .build();

        responseSpecs = new ResponseSpecBuilder()
                .expectStatusCode(200)
                .expectContentType(ContentType.JSON)
                .log(LogDetail.BODY)
                .build();


    }

    @Test
    public void requestResponseSpecification(){
        // https://gorest.co.in/public/v1/users?page=1


            given()
                    .param("page", 1)
                    .spec(requestSpecs)// yukarıda bir metod olarak yazdığımız istem tanımını burada çağırıyoruz. böylece aynı istemleri tanımlı bir classtan sürekli çağırdığımızda iş pratik hale geliyor.

                    .when()

                    .get("/users")// burada url nin başında http yoksa yukarıdaki linki otomatik ekler.

                    .then()
                    .body("meta.pagination.page", equalTo(1))
                    .spec(responseSpecs) // yine yukarıda beklediğimiz sonuç kıyasları için yazdığımız metodu burada çağırarak işi pratik hale getiriyoruz.

            ;
        }

    @Test
    public void extractingJsonPath(){

        String placeName=
        given()

                .when()
                .get("http://api.zippopotam.us/us/90210")

                .then()
                // .log().body()

                .statusCode(200)
                .extract().path("places[0].'place name'")
                // extract metodu ile given ile başlayan satır, bir değer döndürür hale geldi. extract en sonda olmalı.
                // extract ile bilgiyi sorgudan dışarı alıyoruz.


        ;

        System.out.println("placeName="+placeName); // dönen değeri ekrana yazdırdık.

    }

    @Test
    public void extractingJsonPathIn(){

        int limit= // sorgulamada baktık limit diye bir değer var. onu kontrol edeceğiz.
                given()

                        .when()
                        .get("https://gorest.co.in/public/v1/users")

                        .then()
                        //.log().body()

                        .statusCode(200)
                        .extract().path("meta.pagination.limit") // buradan dönecek değer bir sayı. o yüzden yukarıda int olarak tanımlamamız lazım.
                        // dönen değer çift tırnak içinde bir sayı ise bir stringtir. !!!!! bu kısımda dikkatli olmak lazım.
                // burada sorgulamadan dönen bir veriyi, bir değişken tanımlayıp oraya çıkarttık.
                ;

        System.out.println("limit = " + limit); // sonucu ekrana yazdırıyoruz.
        Assert.assertEquals(limit, 10, "Test result"); // assertion ile de gelen sonuç 10 mu diye doğrulatıyoruz.

    }

    @Test
    public void extractingJsonPathInt2(){

        int id= // dizideki 2. bölümünün id sini görmek istiyoruz.
                given()

                        .when()
                        .get("https://gorest.co.in/public/v1/users")

                        .then()
                        //.log().body()

                        .statusCode(200)
                        .extract().path("data[2].id")


                ;

        System.out.println("id = " + id);

    }

    @Test
    public void extractingJsonPathIntList(){

         List<Integer> idler= // biz testte bir listeleme istiyoruz. o yüzden burayı list yapıyoruz. yoksa test gümler.
                given()

                        .when()
                        .get("https://gorest.co.in/public/v1/users")

                        .then()
                        //.log().body()

                        .statusCode(200)
                        .extract().path("data.id") // burada bir dizi var o yüzden bir veriyi almak için dizideki kaçıcı bölümü istiyorsak köşeli parantezde veriyorduk.
                 // ama biz aynı dizideki bütün id leri istiyoruz. o zaman köşeli parantezi kaldırıyoruz. bu defa test gümlüyor.
                 // id leri listelediği için yukarıdaki değişkeni "List" olarak tanımlamamız gerekiyor. böylece test gümlemeyecek
                ;

        System.out.println("id = " + idler); // dizideki bütün id leri yazdı :)
        Assert.assertTrue(idler.contains(2442905)); // idlerin içinde 2442905 var mı? böyle bir doğrulama da  yapalım.

    }

    @Test
    public void extractingJsonPathStringList(){

        List<String> names= // Bu defa isimleri listeleteceğiz. o yüzden dönen değeri String belirttik.
                given()

                        .when()
                        .get("https://gorest.co.in/public/v1/users")

                        .then()
                        //.log().body()

                        .statusCode(200)
                        .extract().path("data.name")
                ;
        System.out.println("names = " + names); // isimleri ekrana yazdırıyoruz.
        Assert.assertTrue(names.contains("Chandan Gupta I")); // listede böyle bir isim var mı?
    }
    // EĞER!! bana sorgudaki verilerden bir çoğu lazımsa bütün hepsini çıkartıp alabilirim.
    // .extract().response() bana bütün dönen verileri çıkartıyor. onları alıp istediğim gibi işleyebilirim. ŞİMDİ:
    @Test
    public void extractingJsonPathAllThings(){

        Response body= // bütün body i alacağız.
                given()

                        .when()
                        .get("https://gorest.co.in/public/v1/users")

                        .then()
                        //.log().body()

                        .statusCode(200)
                        .extract().response()
                ;
        // bütün bodyi aldık. şimdi burada almak istediğim verileri yazıyorum.
        List<Integer> ids=body.path("data.id"); // idler
        List<Integer> names=body.path("data.name"); // isimler
        int limit=body.path("meta.pagination.limit"); // limit değeri

        // aldığımız verileri ekrana yazdırıyoruz.
        System.out.println("ids = " + ids);
        System.out.println("names = " + names);
        System.out.println("limit = " + limit);
    }

    @Test
    public void extractingJsonPOJO(){ // POJO: Plain Old Java Object

        Location yer=
        given()

                .when()

                .get("http://api.zippopotam.us/us/90210")

                .then() 

                .extract().as(Location.class); // location şablonu
        ;
        System.out.println("yer = " + yer);

        System.out.println("yer.getCountry() = " + yer.getCountry());
        System.out.println("yer.getPlaces().get(0).getPlacename() = " + yer.getPlaces().get(0).getPlacename());

    }



}
