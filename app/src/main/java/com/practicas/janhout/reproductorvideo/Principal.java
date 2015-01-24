package com.practicas.janhout.reproductorvideo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.VideoView;

public class Principal extends ActionBarActivity {

    private VideoView vv;
    private boolean reproduce;

    private static final int SELECTOR_VIDEO = 1;
    private static final int FULL_SCREEN = 2;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECTOR_VIDEO) {
            if (resultCode == Activity.RESULT_OK) {
                Uri seleccion = data.getData();
                vv.setVideoURI(seleccion);
                vv.setTag(seleccion);
                guardarPreferencias();
                vv.start();
            }
        } else if(requestCode == FULL_SCREEN){
            if (resultCode == Activity.RESULT_OK){
                Bundle b = data.getExtras();
                vv.seekTo(b.getInt(getString(R.string.actual), 0));
                if(b.getBoolean(getString(R.string.reproduciendo))) {
                    vv.start();
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
        vv = (VideoView)findViewById(R.id.videoView);

        MediaControllerF mediaController = new MediaControllerF(this);
        mediaController.setAnchorView(vv);
        vv.setMediaController(mediaController);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_principal, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.cargar_video) {
            guardarPreferencias();
            cargarVideo();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        guardarPreferencias();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        vv.setVideoURI((Uri)savedInstanceState.getParcelable(getString(R.string.videoActual)));
        vv.setTag((Uri) savedInstanceState.getParcelable(getString(R.string.videoActual)));
        vv.seekTo(savedInstanceState.getInt(getString(R.string.actual), 0));
        if(savedInstanceState.getBoolean(getString(R.string.reproduciendo))) {
            vv.start();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarPreferencias();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(getString(R.string.actual), vv.getCurrentPosition());
        outState.putParcelable(getString(R.string.videoActual), (Uri) vv.getTag());
        outState.putBoolean(getString(R.string.reproduciendo), vv.isPlaying());
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(reproduce) {
            vv.start();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(vv.isPlaying()){
            vv.pause();
            reproduce = true;
        } else {
            reproduce = false;
        }
    }

    private void cargarPreferencias(){
        SharedPreferences prefs = getSharedPreferences(getString(R.string.preferencias),Context.MODE_PRIVATE);
        String cadena = prefs.getString(getString(R.string.videoActual), null);
        if(cadena != null){
            Uri uri = Uri.parse(cadena);
            vv.setVideoURI(uri);
            vv.setTag(uri);
            vv.seekTo(prefs.getInt(getString(R.string.actual), 0));
        }
    }

    private void cargarVideo(){
        Intent pickMedia = new Intent(Intent.ACTION_GET_CONTENT);
        pickMedia.setType(getString(R.string.tipo));
        startActivityForResult(pickMedia, SELECTOR_VIDEO);
    }

    private void guardarPreferencias(){
        SharedPreferences prefs = getSharedPreferences(getString(R.string.preferencias), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        if(vv.getTag()!=null) {
            editor.putString(getString(R.string.videoActual), ((Uri) vv.getTag()).toString());
            editor.putInt(getString(R.string.actual), vv.getCurrentPosition());
        }else{
            editor.clear();
        }
        editor.apply();
    }


    private class MediaControllerF extends MediaController{

        public MediaControllerF(Context c){
            super(c);
        }

        @Override
        public void setAnchorView(View view) {
            super.setAnchorView(view);
            ImageButton fullScreen = new ImageButton(super.getContext());
            fullScreen.setImageResource(R.drawable.ic_action_full_screen);
            fullScreen.setBackgroundColor(Color.TRANSPARENT);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.END| Gravity.TOP;
            fullScreen.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(Principal.this, PantallaCompleta.class);
                    Bundle b = new Bundle();
                    b.putParcelable(Principal.this.getString(R.string.videoActual), (Uri)vv.getTag());
                    b.putInt(Principal.this.getString(R.string.actual), vv.getCurrentPosition());
                    b.putBoolean(Principal.this.getString(R.string.reproduciendo), vv.isPlaying());
                    i.putExtras(b);
                    Principal.this.startActivityForResult(i, FULL_SCREEN);
                }
            });
            addView(fullScreen, params);
        }
    }
}