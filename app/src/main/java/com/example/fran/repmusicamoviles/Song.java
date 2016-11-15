package com.example.fran.repmusicamoviles;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Fran on 18/10/2016.
 */
public class Song implements Parcelable{

    public static final Parcelable.Creator<Song> CREATOR = new Parcelable.Creator<Song>(){
        @Override
        public Song createFromParcel(Parcel parcel) {
            return new Song(
                    parcel.readString(),
                    parcel.readString(),
                    parcel.readString(),
                    parcel.readString(),
                    parcel.readString());
        }

        @Override
        public Song[] newArray(int i) {
            return new Song[i];
        }
    };

    private String uri;
    private String title;
    private String artist;
    private String album;
    private String albumArt;

    public Song( String a,String b, String c, String d, String e){
        this.uri=a;
        this.title=b;
        this.artist=c;
        this.album=d;
        this.albumArt=e;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public String getAlbum() {
        return album;
    }

    public String getUri() {
        return uri;
    }

    public String getAlbumArt(){return albumArt;}

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(String.valueOf(this.uri));
        parcel.writeString(this.title);
        parcel.writeString(this.artist);
        parcel.writeString(this.album);
        parcel.writeString(this.albumArt);
    }
}