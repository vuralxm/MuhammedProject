package Campus.Model;

// MVC --> yazılımın mimarisi, şablonu demektir.
// eskiden tek bir class açılır ve bütün yazılım bu classın içinde olurdu. front end, back end ve bunlar için gerekli class.
//front end--> buna "view" deniyor.
// back end --> buna "controller" dediler
// bunlar için gerekli class. ---> buna da "Model"(tip,class) dediler. hepsine birden Model Controller View(MCV) denildi.
// sonra daha kolay olsun diye bu üçünü ayırdılar. Model ayrı, Controller ayrı, View ayrı yazıldı. bunlar organize çalışyor ama ayrı yere yazıldı.
// böylece kod takibi ve kontrolü çok kolaylaştı.


public class Country {
    private String name;
    private String code;
    private String id;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Country{" +
                "name='" + name + '\'' +
                ", code='" + code + '\'' +
                ", id='" + id + '\'' +
                '}';
    }
}
