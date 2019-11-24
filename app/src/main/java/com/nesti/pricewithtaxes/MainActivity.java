package com.nesti.pricewithtaxes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback
{
    Location mlocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    private static final int Request_Code=101;
    TextView tutexto ;
    public String laddress = "";
    public String lcity = "";
    public String lstate = "";
    public String lzipCode = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
      fusedLocationProviderClient= LocationServices.getFusedLocationProviderClient( this);
      GetlastLocation();
        tutexto  = (TextView)findViewById(R.id.zipcode);


    }

    private void GetlastLocation() {

        if (ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},Request_Code);
            return;
        }
        Task<Location> task =fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null){
                    mlocation=location;
                    Toast.makeText(getApplicationContext() , mlocation.getLatitude()+ ""+mlocation.getLongitude(),
                            Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng latLng = new LatLng(mlocation.getLatitude(),mlocation.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("You Are Here");
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,6));
        googleMap.addMarker(markerOptions);


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    switch (requestCode) {
        case Request_Code:
            if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            GetlastLocation();
        }
        break;
}
    }

    public void GetZip()
    {
        final Geocoder gcd = new Geocoder(this, Locale.ENGLISH);
        List<Address> addresses = null;
        try {
            addresses = gcd.getFromLocation(mlocation.getLatitude(), mlocation.getLongitude(), 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Address address=null;
        String addr="";
        String zipCode = "";
        String city="";
        String state="";
        if (addresses != null && addresses.size() > 0) {

            addr = addresses.get(0).getAddressLine(0) + "," + addresses.get(0).getSubAdminArea();
            city = addresses.get(0).getLocality();
            state = addresses.get(0).getAdminArea();

            for (int i = 0; i < addresses.size(); i++) {
                address = addresses.get(i);
                if (address.getPostalCode() != null) {
                    zipCode =  address.getPostalCode();
                    break;
                }

            }
        }
        laddress = addr;
        lzipCode = zipCode;
        lcity = city;
        lstate = state;
    }

    public void buttonOnClick(View v) {
// do something when the button is clicked
        GetZip();
        if ( lzipCode != "")
        tutexto.setText(" Zip Code = " + lzipCode + "\n State = " + lstate + "\n City = " + lcity);
        else tutexto.setText("Outside of the country");

    }

}
