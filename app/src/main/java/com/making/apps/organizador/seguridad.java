package com.making.apps.organizador;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Clase que contiene los metodos para generar seguridad a la clave
 */
public class seguridad {


    /**
     * Metodo que devuelve clave cifrada teniendo en cuenta usuario y clave
     *
     * @param usuario  nombre de usuario
     * @param password clave
     */
    public static String get_SHA_512_SecurePassword(String password, String usuario) throws UnsupportedEncodingException {

        String generatedPassword = null;

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(usuario.getBytes("UTF-8"));
            byte[] bytes = md.digest(password.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < bytes.length; i++) {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            generatedPassword = sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return generatedPassword;
    }


}
