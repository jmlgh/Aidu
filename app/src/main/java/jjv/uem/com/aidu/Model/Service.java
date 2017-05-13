package jjv.uem.com.aidu.Model;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Victor on 16/04/2017.
 */

public class Service {
    private String title;
    private String icon;

    private String description;
    private String price_points;
    private String date;
    private String hour;
    private String location;
    private String category;
    private String kind;
    private Comunity comunity;
    private String[] photos;
    private String userkeyInterested;
    private String state;
    private String userkey;
    private String userName;
    private String serviceKey;

    /*
    //sustituir por clase usuario mejor

    private User user;



|-ESTADO : DISPONIBLE (Cuando se publica ) / ESPERA (Cuando se esta negociando por
            un servicio) /EN CURSO (Cuando lo a aceptado el que solicita el servicio ) /
    FINALIZADO(Cuando se confirma el servicio y se ha pagado)
|-KEY COMUNIDAD (POR SI FUESE UN SERVICIO ESPECÍFICO PARA LA
            COMUNIDAD)
|-LISTA FOTOS SERVICIO |-  LÍMITE:5
            |-URL

     */

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrice_points() {
        return price_points;
    }

    public void setPrice_points(String price_points) {
        this.price_points = price_points;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public Comunity getComunity() {
        return comunity;
    }

    public void setComunity(Comunity comunity) {
        this.comunity = comunity;
    }

    public String[] getPhotos() {
        return photos;
    }

    public void setPhotos(String[] photos) {
        this.photos = photos;
    }

    public String getUserkeyInterested() {
        return userkeyInterested;
    }

    public void setUserkeyInterested(String userkeyInterested) {
        this.userkeyInterested = userkeyInterested;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getUserkey() {
        return userkey;
    }

    public void setUserkey(String userkey) {
        this.userkey = userkey;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getServiceKey() {
        return serviceKey;
    }

    public void setServiceKey(String serviceKey) {
        this.serviceKey = serviceKey;
    }

    public Map<String, Object> toMap() { //creamos una lista con cada uno de los atibustos del objeto
        HashMap<String, Object> result = new HashMap<>();
        result.put("title", title);
        result.put("description", description);
        result.put("category", category);
        result.put("kind", kind);
        result.put("date", date);
        result.put("hour", hour);
        result.put("price_points", price_points);
        result.put("location", location);
        result.put("state", state);
        result.put("userkeyInterested", userkeyInterested);
        result.put("userkey", userkey);
        result.put("userName", userName);
        result.put("serviceKey",serviceKey);
        //TODO put comunities and photos
        return result;
    }

    @Override
    public String toString() {
        return "Service{" +
                "title='" + title + '\'' +
                ", icon='" + icon + '\'' +
                ", description='" + description + '\'' +
                ", price_points='" + price_points + '\'' +
                ", date='" + date + '\'' +
                ", hour='" + hour + '\'' +
                ", location='" + location + '\'' +
                ", category='" + category + '\'' +
                ", kind='" + kind + '\'' +
                ", comunity=" + comunity +
                ", photos=" + Arrays.toString(photos) +
                ", userkeyInterested='" + userkeyInterested + '\'' +
                ", state='" + state + '\'' +
                ", userkey='" + userkey + '\'' +
                ", userName='" + userName + '\'' +
                ", serviceKey='" + serviceKey + '\'' +
                '}';
    }
}
