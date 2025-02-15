package com.example.proximityfirebase;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.PolyUtil;

import java.io.IOException;

public class UsuarioB {

    Context context;
    public UsuarioB(Context context) {
        this.context = context;
    }

    public void checkLocation(GoogleMap map, UsuarioA userA) {
        Uri ringtone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        MediaPlayer player = MediaPlayer.create(context, ringtone);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        map.setMyLocationEnabled(true);
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        LocationListener locListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                LatLng myLoc = new LatLng(location.getLatitude(), location.getLongitude());
                //COMPROBANDO LA PROXIMIDAD AL CIRCULO
                map.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
                    @Override
                    public void onMyLocationChange(Location myLocation) {
                        float[] distance = new float[2];
                        Circle perim = userA.getCircle();
                        Location.distanceBetween( myLocation.getLatitude(), myLocation.getLongitude(),
                                perim.getCenter().latitude, perim.getCenter().longitude, distance);
                        // Comprobacion area
                        if( distance[0] < perim.getRadius() ){
                            Vibrator vib = (Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);
                            vib.vibrate(1000);
                        }

                        //COMPROBACION POLIGONO
                        LatLng myLocat = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
                        if(PolyUtil.containsLocation(myLocat, userA.getPoligono(), false)) {
                            if (!player.isPlaying()) {
                                player.start();
                            }
                        }
                        else {
                            if (player.isPlaying()) {
                                player.stop();
                                try {
                                    player.prepare();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        //FIN POLÍGONO
                    }
                });
                //FIN DE PERÍMETRO
            }

        };
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 2, 1000, locListener);
    }
}
