package jjv.uem.com.aidu.Model;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Victor on 16/04/2017.
 */

public class Community {

    /*
    COMUNIDADES - KEY COMUNIDAD
|-NOMBRE
|-PRIVADA / PÚBLICA
|-CONTRASEÑA
|-DESCRIPCIÓN
|-ICONO/FOTO
|-LISTA USUARIOS
|-KEY DE CADA USUARIO

     */

    private String name;
    private String key;
    private boolean publica;
    private String description;
    private int Icon;
    private String image;
    private ArrayList<String> members;
    private String owner;
    private String address;
    private double longitude;
    private double latitude;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public boolean isPublica() {
        return publica;
    }

    public void setPublica(boolean publica) {
        this.publica = publica;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getIcon() {
        return Icon;
    }

    public void setIcon(int icon) {
        Icon = icon;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public ArrayList<String> getMembers() {
        return members;
    }

    public void setMembers(ArrayList<String> members) {
        this.members = members;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public Map<String, Object> toMap() { //creamos una lista con cada uno de los atibustos del objeto
        HashMap<String, Object> result = new HashMap<>();
        result.put("name", name);
        result.put("key", key);
        result.put("publica", publica);
        result.put("description", description);
        result.put("Icon", Icon);
        result.put("image", image);
        result.put("members", members);
        result.put("owner", owner);
        result.put("address", address);
        result.put("longitude", longitude);
        result.put("latitude", latitude);
        //TODO put comunities and photos
        return result;
    }



}
