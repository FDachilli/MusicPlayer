package com.example.fran.repmusicamoviles;


import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;


import java.io.IOException;
import java.util.ArrayList;


/**
 * Created by Fran on 19/10/2016.
 */
public class PlayService extends Service {

    private final IBinder mBinder = new LocalService();
    private ArrayList<Song> canciones = new ArrayList<>();
    private boolean playing = false;
    private boolean pause = false;
    private int contador = 0;
    private String artista;
    private String album;
    private String albumArt;
    private MediaPlayer mediaPlayer;
    LocalBroadcastManager localBroadcastManager;
    NotificationCompat.Builder notificacion = new NotificationCompat.Builder(this);

    private MediaPlayer.OnPreparedListener onPreparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mediaPlayer) {
            mediaPlayer.start();
        }
    };

    private MediaPlayer.OnCompletionListener onCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {

            if (contador < canciones.size() - 1)
                PlayService.this.siguiente();
            Intent intent = new Intent(PlayerActivity.RECEIVE);
            intent.putExtra("Cancion", canciones.get(contador).getTitle());
            localBroadcastManager.sendBroadcast(intent);
        }
    };


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (album == null || (!album.equals(intent.getStringExtra (MediaStore.Audio.Albums.ALBUM)) && !artista.equals(intent.getStringExtra (MediaStore.Audio.Albums.ARTIST)))) {
            this.artista = intent.getStringExtra(MediaStore.Audio.Albums.ARTIST);
            this.album = intent.getStringExtra(MediaStore.Audio.Albums.ALBUM);
            this.albumArt = intent.getStringExtra(MediaStore.Audio.AlbumColumns.ALBUM_ART);
            this.pause = false;
            if (mediaPlayer == null) {
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            }
            this.armarListaDeCanciones();
            this.play();
            localBroadcastManager = LocalBroadcastManager.getInstance(this);
            return START_NOT_STICKY;
        }
       else return START_NOT_STICKY;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        if (this.mediaPlayer.isPlaying())
            this.mediaPlayer.stop();
        this.mediaPlayer.release();
        this.mediaPlayer = null;
        stopForeground(true);
        this.stopSelf();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private void armarListaDeCanciones (){
            String selection = MediaStore.Audio.Media.ARTIST + " =? AND " + MediaStore.Audio.Media.ALBUM + "=?";
            Cursor cursor = PlayService.this.getContentResolver().query(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    new String[]{MediaStore.Audio.Media._ID, MediaStore.Audio.Media.TITLE,
                            MediaStore.Audio.Media.DATA},
                    selection,
                    new String[]{PlayService.this.artista, PlayService.this.album},
                    null);

            PlayService.this.stop();
            PlayService.this.canciones.clear();
            PlayService.this.contador = 0;
            while (cursor.moveToNext()) {
                String uri = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                Song s = new Song(uri, title, artista, album, albumArt);
                canciones.add(s);
            }

            cursor.close();
    }

    public void play(){
        if (this.pause){
            this.mediaPlayer.start();
            this.pause=false;
            this.playing=true;
            this.activarNotificacion();
            return;
        }
        if (this.contador<this.canciones.size()){
            playing=true;
            Song s= this.canciones.get(contador);
            if (this.mediaPlayer.isPlaying())
                this.mediaPlayer.stop();
            this.mediaPlayer.reset();
            this.mediaPlayer.setOnCompletionListener(this.onCompletionListener);
            this.mediaPlayer.setOnPreparedListener(this.onPreparedListener);
            try{
                this.mediaPlayer.setDataSource(this, Uri.parse(String.valueOf(s.getUri())));
                this.mediaPlayer.prepareAsync();
                this.activarNotificacion();
            }

            catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    public String getCancion(){
        Song s= this.canciones.get(contador);
        return s.getTitle();
    }

    public boolean getPlaying(){
        return this.playing;
    }

    public void activarNotificacion () {

        Song s = this.canciones.get(contador);
        notificacion.setOngoing(true);
        int drawable = android.R.drawable.ic_media_pause;
        if (playing)
            drawable = android.R.drawable.ic_media_play;
        notificacion.setContentTitle("Reproduciendo...");
        notificacion.setContentText(s.getTitle());
        notificacion.setSmallIcon(drawable);
        notificacion.setTicker(s.getTitle());
        notificacion.setAutoCancel(false);
        Intent i = new Intent (this, PlayerActivity.class);
        i.putExtra(MediaStore.Audio.Albums.ARTIST, artista);
        i.putExtra(MediaStore.Audio.Albums.ALBUM, album);
        i.putExtra(MediaStore.Audio.AlbumColumns.ALBUM_ART, albumArt);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
        notificacion.setContentIntent(contentIntent);

        this.startForeground(1, notificacion.build());

    }

    public void pausa(){
        if (playing){
            playing = false;
            if (this.mediaPlayer.isPlaying()){
                this.mediaPlayer.pause();
                this.pause=true;
            }
            this.activarNotificacion();
        }
    }

    public void stop(){
        if (playing){
            playing = false;
            pause=false;
            if (this.mediaPlayer.isPlaying()){
                this.mediaPlayer.stop();
            }
        this.activarNotificacion();
        }
    }

    public void anterior(){

        if(contador>0) {
            contador--;}
        else {
            contador = canciones.size()-1;
        }
            this.pause = false;
            if(playing)
                this.play();
        this.activarNotificacion();

    }


    public void siguiente(){

        if(contador<this.canciones.size()-1) {
            contador++;}
        else {
            contador = 0;
        }
            this.pause = false;
            if(playing)
                this.play();
        this.activarNotificacion();

    }

    public class LocalService extends Binder {

        PlayService getService(){
            return PlayService.this;
        }
    }

}

