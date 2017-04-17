package jjv.uem.com.aidu.Model;

/**
 * Created by Victor on 16/04/2017.
 */

public class User {
    private String email;
    private String displayName;
    private String password;

    public User(){}

    public User(String email, String name, String password){
        this.email = email;
        this.displayName = name;
        this.password = password;

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
