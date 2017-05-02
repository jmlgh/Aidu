package jjv.uem.com.aidu.Model;

/**
 * Created by Victor on 16/04/2017.
 */

public class Service {
    private String title;
    private String icon;
    /*
     private String description;
    private String price_points;
    private String date;
    private String hour;
    private String location;
    private String category;
    private String kind;
    //sustituir por clase usuario mejor
    private String userkey;
    private String userName;
    private String userkeyInterested;
    private String state;
    private Comunity comunity;
    private String[] photos;


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


}
