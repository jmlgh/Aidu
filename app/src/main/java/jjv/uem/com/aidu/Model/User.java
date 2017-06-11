package jjv.uem.com.aidu.Model;

import com.google.firebase.auth.FirebaseUser;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Victor on 16/04/2017.
 */

public class User {
    private String email;
    private String displayName;
    private String password;
    private String key;
    private double numVal;
    private double media;
    private int points;

    public User() {
    }

    public User(FirebaseUser user, String password, String displayName){
        this.email = user.getEmail();
        this.displayName = displayName;
        this.password = password;
        this.key=user.getUid();
        this.numVal = 0;
        this.media = 0;
        this.points=0;

    }

    public User(){};

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public double getNumVal() {
        return numVal;
    }

    public void setNumVal(double numVal) {
        this.numVal = numVal;
    }

    public double getMedia() {
        return media;
    }

    public void setMedia(double media) {
        this.media = media;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public double getNumVal() {
        return numVal;
    }

    public void setNumVal(double numVal) {
        this.numVal = numVal;
    }

    public double getMedia() {
        return media;
    }

    public void setMedia(double media) {
        this.media = media;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("email",this.email  );
        result.put("displayName", this.displayName);
        result.put("password",this.password );
        result.put("key",this.key );
        result.put("numVal",this.numVal );
        result.put("media", this.media);
        result.put("points", this.points);

        return result;
    }

    public void addPoints(int i) {
        this.points+=i;
    }

    /*
    |-NOMBRE
|-EMAIL
|-VALORACIÓN MEDIA
|-SALDO DE PUNTOS
|-UBICACIÓN HABITUAL
|-FOTO PERFIL
|-LISTA VALORACIONES - PUNTUACIÓN
			|-------VALORACIÓN (COMENTARIO)
			|-------USUARIO QUE HA REALIZADO - NOMBRE Y KEY
|-COMUNIDADES
|-KEY COMUNIDAD
|-USUARIOS FAVS
|-KEY USUARIO
     */
}
