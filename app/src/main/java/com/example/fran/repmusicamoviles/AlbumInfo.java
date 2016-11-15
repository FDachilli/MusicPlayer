package com.example.fran.repmusicamoviles;

/**
 * Created by Fran on 24/10/2016.
 */
public class AlbumInfo {

    private String nombreAlbum;
    private String artista;
    private String albumArt;


    public AlbumInfo (String a, String b, String c){
            this.nombreAlbum=a;
            this.artista=b;
            this.albumArt=c;
    }

    public String getAlbumArt() {
        return albumArt;
    }

    public String getArtista() {
        return artista;
    }

    public String getNombreAlbum() {
        return nombreAlbum;
    }
}
