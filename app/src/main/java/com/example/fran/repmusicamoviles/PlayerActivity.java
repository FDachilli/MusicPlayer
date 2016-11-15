package com.example.fran.repmusicamoviles;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * Created by Fran on 19/10/2016.
 */
public class PlayerActivity extends AppCompatActivity {

    private TextView textArtista;
    private TextView textCancion;
    private ImageView imgAlbum;
    private ImageButton play;
    private ImageButton left;
    private ImageButton right;
    private String album;
    private String artista;
    private String albumArt;
    private boolean playing;
    private PlayService playService;
    boolean isBind = false;
    public static final String RECEIVE = "Broadcast recibido";

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String nomCancion = intent.getStringExtra("Cancion");
            textCancion.setText(nomCancion);
        }
    };

    LocalBroadcastManager localBroadcastManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        textCancion = (TextView) findViewById(R.id.textCancion);
        textArtista = (TextView) findViewById (R.id.textArtista);
        play = (ImageButton) findViewById(R.id.buttonPlay);
        left = (ImageButton) findViewById(R.id.buttonLeft);
        right = (ImageButton) findViewById(R.id.buttonRight);
        imgAlbum = (ImageView) findViewById(R.id.imagenAlbum);
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(RECEIVE);
        localBroadcastManager.registerReceiver(broadcastReceiver, intentFilter);

        Intent i = getIntent();
        if (i!=null){

                artista = i.getStringExtra(MediaStore.Audio.Albums.ARTIST);
                album = i.getStringExtra(MediaStore.Audio.Albums.ALBUM);
                albumArt = i.getStringExtra(MediaStore.Audio.AlbumColumns.ALBUM_ART);


                Intent intent = new Intent(this, PlayService.class);
                intent.putExtra(MediaStore.Audio.Albums.ARTIST, artista);
                intent.putExtra(MediaStore.Audio.Albums.ALBUM, album);
                intent.putExtra(MediaStore.Audio.AlbumColumns.ALBUM_ART, albumArt);
                this.startService(intent);
                bindService(intent,Mconnection,Context.BIND_AUTO_CREATE);
                textArtista.setText(artista);
                play.setImageResource(R.drawable.pause);
                if (albumArt!=null){
                Drawable img = Drawable.createFromPath(albumArt);
                imgAlbum.setImageDrawable(img);
                }
                else
                    imgAlbum.setImageResource(R.drawable.notimage);
        }


        this.play.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if (!playService.getPlaying()) {
                    play.setImageResource(R.drawable.pause);
                    playService.play();
                    String cancion = playService.getCancion();
                    textCancion.setText(cancion);
                }
                else{
                    play.setImageResource(R.drawable.play);
                    playService.pausa();
                }
            }
        });


        this.left.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                playService.anterior();
               String cancion = playService.getCancion();
                textCancion.setText(cancion);
            }
        });

        this.right.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                playService.siguiente();
                String cancion = playService.getCancion();
                textCancion.setText(cancion);
            }
        });

    }


    private ServiceConnection Mconnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {

            PlayService.LocalService localService = (PlayService.LocalService) service;
            playService = localService.getService();
            String c = playService.getCancion();
            textCancion.setText(c);

            isBind = true;
        }


        @Override
        public void onServiceDisconnected(ComponentName componentName) {
                isBind = false;
        }
    };

  @Override
    protected void onStop() {
        super.onStop();
        if (isBind == true){
            unbindService(Mconnection);
            isBind = false;

         localBroadcastManager.unregisterReceiver(broadcastReceiver);
        }
    }

}

