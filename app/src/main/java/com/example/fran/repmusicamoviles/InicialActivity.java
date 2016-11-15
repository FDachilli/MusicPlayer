package com.example.fran.repmusicamoviles;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class InicialActivity extends AppCompatActivity {
    private ListView albunes;
   private ArrayList<AlbumInfo> listAlbum = new ArrayList<AlbumInfo>();
    private CursorAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicial);
        int permissionCheck = ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
        albunes = (ListView) findViewById(R.id.listaAlbum);

        String[] projection = {
                "_id",
                MediaStore.Audio.AlbumColumns.ALBUM,
                MediaStore.Audio.AlbumColumns.ARTIST,
                MediaStore.Audio.AlbumColumns.ALBUM_ART
        };


        final String sortOrder = MediaStore.Audio.AlbumColumns.ALBUM + " COLLATE LOCALIZED ASC";
        Cursor cursor = null;
        try {
            Uri uri = android.provider.MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
            cursor = InicialActivity.this.getContentResolver().query(uri, projection, null, null, sortOrder);
            while(cursor.moveToNext()){
                String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AlbumColumns.ALBUM));
                String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AlbumColumns.ARTIST));
                String albumArt = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AlbumColumns.ALBUM_ART));
                AlbumInfo a = new AlbumInfo(album, artist, albumArt);
                listAlbum.add (a);

            }

        }
        catch (Exception e){
            e.printStackTrace();
        }

        adapter = new ArtistAdapter(this,cursor);
        albunes.setAdapter(adapter);
        albunes.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent intent = new Intent (InicialActivity.this,PlayerActivity.class);
                intent.putExtra(MediaStore.Audio.Albums.ALBUM, InicialActivity.this.listAlbum.get(i).getNombreAlbum());
                intent.putExtra(MediaStore.Audio.Albums.ARTIST, InicialActivity.this.listAlbum.get(i).getArtista());
                intent.putExtra(MediaStore.Audio.Albums.ALBUM_ART, InicialActivity.this.listAlbum.get(i).getAlbumArt());
                InicialActivity.this.startActivity(intent);
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }


    protected class ArtistAdapter extends CursorAdapter{
        LayoutInflater inflater;

        public ArtistAdapter(Context context, Cursor cursor) {
            super(context,cursor,0);
            this.inflater = LayoutInflater.from(InicialActivity.this);
        }
        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            View v = this.inflater.inflate(R.layout.muestra_album,parent,false);
            return v;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            String artist = cursor.getString(cursor.getColumnIndex("Artist"));
            TextView artista = (TextView) view.findViewById(R.id.textArtista);
            artista.setText(artist);
            String album = cursor.getString(cursor.getColumnIndex("Album"));
            TextView alb = (TextView)view.findViewById(R.id.textAlbumI);
            alb.setText(album);
            String albumArt = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AlbumColumns.ALBUM_ART));
            ImageView imagen = (ImageView) view.findViewById(R.id.imageAlbum);
            if (albumArt!=null){
            Drawable img = Drawable.createFromPath(albumArt);
            imagen.setImageDrawable(img);
            }
            else
                imagen.setImageResource(R.drawable.notimage);
        }

    }
}
