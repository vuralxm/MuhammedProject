package POJO;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Place {
    private String placename;
    private String longitude;
    private String state;
    private String stateabbreviation;
    private String latitude;

    // testi çalıştırdık ve test gümledi. sebep  burada class isimlerinde birden fazla kelime olsada bitişik yazmak zorundayız. Ama api de ayrı yazılmış. bunu şöyle şözüyoruz:
    // orada dönüşüm yapacak bir ekleme yapacağız. Önce POMxml e POJO databind dependency i ekliyoruz.
    // Bu testin gümlemesine sebep olan "post code" ifadesi Location classta yer alıyor. oraya gidip "post code" tipinin üzerine bir ekleme yapacağız. çünkü eşleşme yapamıyor.


    public String getPlacename() {
        return placename;
    }

    @JsonProperty("place name")
    public void setPlacename(String placename) {
        this.placename = placename;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getStateabbreviation() {
        return stateabbreviation;
    }

    @JsonProperty("state abbreviation")
    public void setStateabbreviation(String stateabbreviation) {
        this.stateabbreviation = stateabbreviation;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    @Override
    public String toString() {
        return "Place{" +
                "placename='" + placename + '\'' +
                ", longitude='" + longitude + '\'' +
                ", state='" + state + '\'' +
                ", stateabbreviation='" + stateabbreviation + '\'' +
                ", latitude='" + latitude + '\'' +
                '}';
    }
}
