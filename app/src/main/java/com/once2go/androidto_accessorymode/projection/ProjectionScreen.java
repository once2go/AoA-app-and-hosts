package com.once2go.androidto_accessorymode.projection;


import android.app.Presentation;
import android.content.Context;

import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;

import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.VideoView;


import androidx.annotation.NonNull;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.once2go.androidto_accessorymode.R;


class ProjectionScreen extends Presentation {
    private VideoView videoView;
    private WebView webView;
    private MapView mapView;

    public ProjectionScreen(Context outerContext, Display display) {
        super(outerContext, display);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.projection_surface_layout);
        setup3DScene();
        setupMap(savedInstanceState);
        setupVideoPlayer();
        setupWebView();
    }

    private void setupWebView() {
        webView = findViewById(R.id.web_view);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("https://www.meetup.com/ToAndroidDev/events/264753416/");
    }

    private void setupVideoPlayer() {
        videoView = findViewById(R.id.video_view);
        videoView.setVideoPath("http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4");
        videoView.start();
    }

    private void setupMap(Bundle iState) {
        mapView = findViewById(R.id.map_view);
        mapView.onCreate(iState);
        mapView.getMapAsync(new OnMapReadyCallback() {

            @Override
            public void onMapReady(GoogleMap googleMap) {
                LatLng coordinates = new LatLng(43.654325, -79.379893);
                googleMap.addMarker(new MarkerOptions().position(coordinates).title("DDD"));
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coordinates, 15));
                mapView.onResume();
            }
        });
    }

    private void setup3DScene() {
        CubeRenderer renderer = new CubeRenderer(false);
        GLSurfaceView surfaceView = findViewById(R.id.gl_surface_view);
        surfaceView.setRenderer(renderer);
    }

    @Override
    public boolean dispatchKeyEvent(@NonNull KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_PAGE_UP:
                    webView.pageUp(false);
                    return true;
                case KeyEvent.KEYCODE_PAGE_DOWN:
                    webView.pageDown(false);
                    return true;
                case KeyEvent.KEYCODE_MEDIA_PLAY:
                    videoView.start();
                    return true;
                case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                    videoView.pause();
                    return true;
            }
        }
        return false;
    }
}
