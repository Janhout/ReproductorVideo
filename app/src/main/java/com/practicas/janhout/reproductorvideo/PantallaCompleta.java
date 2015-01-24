package com.practicas.janhout.reproductorvideo;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.VideoView;


public class PantallaCompleta extends ActionBarActivity {

    private VideoView vv;
    private boolean resultado;
    private boolean reproduce;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        vv = (VideoView)findViewById(R.id.videoView);
        MediaController mediaController = new MediaController(this);

        mediaController.setAnchorView(vv);
        vv.setMediaController(mediaController);

        Intent i = getIntent();
        Bundle b = i.getExtras();
        if(b !=null) {
            if(b.containsKey(getString(R.string.videoActual)) &&
                    b.containsKey(getString(R.string.actual)) &&
                    b.containsKey(getString(R.string.reproduciendo))) {
                resultado = true;
                vv.setVideoURI((Uri) b.getParcelable(getString(R.string.videoActual)));
                vv.seekTo(b.getInt(getString(R.string.actual)));
                if(b.getBoolean(getString(R.string.reproduciendo))){
                    vv.start();
                }
            }else{
                Uri data = i.getData();
                if(data!=null) {
                    resultado = false;
                    vv.setVideoURI(data);
                    vv.start();
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        terminar();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(vv.isPlaying()){
            vv.pause();
            reproduce = true;
        }else{
            reproduce = false;
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if(reproduce) {
            vv.start();
        }
    }

    private void terminar(){
        if(resultado){
            Intent i = new Intent();
            Bundle b = new Bundle();
            b.putBoolean(getString(R.string.reproduciendo), vv.isPlaying());
            b.putInt(getString(R.string.actual), vv.getCurrentPosition());
            i.putExtras(b);
            setResult(Activity.RESULT_OK, i);
        }
        finish();
    }
}
