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


    public User(FirebaseUser user, String password, String displayName){
        this.email = user.getEmail();
        this.displayName = displayName;
        this.password = password;
        this.key=user.getUid();
        this.numVal = 0;
        this.media = 0;

    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("email",this.email  );
        result.put("displayName", this.displayName);
        result.put("password",this.password );
        result.put("key",this.key );
        result.put("numVal",this.numVal );
        result.put("media", this.media);

        return result;
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
