package com.example.map;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    NotificationManager notificationManager;


    private GoogleMap googleMap;
    SupportMapFragment supportMapFragment;
    GoogleMap gmap;
    Button draw, clear, mypoint;
    CheckBox checkBox;

    Polygon polygon = null;
    List<LatLng> latLngList = new ArrayList<>();
    List<Marker> markerList = new ArrayList<>();
    List<String> btlatLngList = new ArrayList<>();

    //GPS 기본셋팅
    private Button btnShowLocation;
    private TextView txtLat;
    private TextView txtLon;
    private final int PERMISSIONS_ACCESS_FINE_LOCATION = 1000;
    private final int PERMISSIONS_ACCESS_COARSE_LOCATION = 1001;
    private boolean isAccessFineLocation = false;
    private boolean isAccessCoarseLocation = false;
    private boolean isPermission = false;

    // GPSTracker class
    private Gpsinfo gps;
    private TextView textView_flowers;
    private String locObj;


    // 구글 맵 불러오는 구간 및 폴리곤 코드
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button=findViewById(R.id.btn_mypoint_gpsinfo);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myintent=new Intent(MainActivity.this,subActivity.class);
                startActivities(new Intent[]{myintent});

            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        //gps 화면 출력 셋팅
        btnShowLocation = (Button) findViewById(R.id.btn_start);
        textView_flowers = (TextView) findViewById(R.id.flowers);
        textView_flowers.setTextIsSelectable(true);



        // GPS 정보를 보여주기 위한 이벤트 클래스 등록
        btnShowLocation.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                // 권한 요청을 해야 함
                if (!isPermission) {
                    callPermission();
                    return;
                }
                gmap = googleMap;
                gps = new Gpsinfo(MainActivity.this);

                //gps 좌표로 마커가 찍힌다.
                if (gps.isGetLocation()) {

                    btlatLngList = new ArrayList<>();
                    Intent intent = getIntent();
                    ArrayList<String> flowers = intent.getStringArrayListExtra("flowers");
                    textView_flowers.setText("");

                    for(int i=0; i < flowers.size(); i++)
                    {
                        textView_flowers.setText(textView_flowers.getText() + flowers.get(i));
                        btlatLngList.add(flowers.get(i));
                        //String[] btlatLngListe = {flowers.get(i)};
                    }


                    double latitude = gps.getLatitude();
                    double longitude = gps.getLongitude();
                    LatLng latLng5 = new LatLng(latitude, longitude);
                    MarkerOptions markerOptions = new MarkerOptions()
                            .position(latLng5);
                    gmap.addMarker(markerOptions);
                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng5));
                    googleMap.moveCamera(CameraUpdateFactory.zoomTo(15));

                    if(isPointInPolygon(latLng5, latLngList))
                    {
                        Toast.makeText(getApplicationContext(), "inside",
                                Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        NotificationCompat.Builder builder = getbuilder("channel_1", "datamanager");
                        //안드로이드 이미지 설정
                        builder.setSmallIcon(android.R.drawable.ic_notification_overlay);
                        //제목설정
                        builder.setContentTitle("googlemap알림");
                        builder.setContentText("울타리를 탈출하였습니다.~~~~");
                        //알림과동시에 진동
                        builder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE | Notification.DEFAULT_LIGHTS);
                        //Notification객체 만들기
                        Notification notification = builder.build();
                        //Notification객체를 NotificationManager에 등록 - Notification을 발생
                        notificationManager.notify(1004, notification);

                        Toast.makeText(getApplicationContext(), "outside",
                                Toast.LENGTH_LONG).show();
                    }

                    Toast.makeText(
                            getApplicationContext(),
                            "보호대상의 위치\n" + textView_flowers.getText(),
                            Toast.LENGTH_LONG).show();
                } else {
                    // GPS 를 사용할수 없으므로
                    gps.showSettingsAlert();
                }
            }
        });


        callPermission();  // 권한 요청을 해야 함

        //버튼
        checkBox = findViewById(R.id.check_box);
        draw = findViewById(R.id.btn_draw_polygon);
        clear = findViewById(R.id.btn_Clear_polygon);

        checkBox.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                if (polygon == null) return;

                polygon.setFillColor(Color.BLUE);
            } else {
                //unchecked
                polygon.setFillColor(Color.TRANSPARENT);
            }
        });

        draw.setOnClickListener(view -> {
            //draw polygon on map
            if (polygon != null) {
                polygon.remove();
            }
            PolygonOptions polygonOptions = new PolygonOptions().addAll(latLngList)
                    .clickable(true);
            polygon = gmap.addPolygon(polygonOptions);

            //polygon fill & strok color
            polygon.setFillColor(Color.BLUE);
            polygon.setStrokeColor(Color.RED);

            if (checkBox.isChecked()) {
                polygon.setFillColor(Color.BLUE);
            }

        });

        clear.setOnClickListener(view -> {
            if (polygon != null) {
                polygon.remove();
            }
            for (Marker marker : markerList) marker.remove();
            latLngList.clear();
            markerList.clear();
            checkBox.setChecked(false);
        });

    }

    private boolean isPointInPolygon(LatLng tap, List<LatLng> latLngList) {
        int intersectCount = 0;
        for(int j=0; j<latLngList.size()-1; j++) {
            if( LineIntersect(tap, latLngList.get(j), latLngList.get(j+1)) ) {
                intersectCount++;
            }
        }
        return (intersectCount%2) == 1; // odd = inside, even = outside;

    }
    private boolean LineIntersect(LatLng tap, LatLng vertA, LatLng vertB) {
        double aY = vertA.latitude;
        double bY = vertB.latitude;
        double aX = vertA.longitude;
        double bX = vertB.longitude;
        double pY = tap.latitude;
        double pX = tap.longitude;
        if ( (aY>pY && bY>pY) || (aY<pY && bY<pY) || (aX<pX && bX<pX) ) {
            return false; }
        double m = (aY-bY) / (aX-bX);
        double bee = (-aX) * m + aY;                // y = mx + b
        double x = (pY - bee) / m;
        return x > pX;
    }


    // 핸드폰 경고문구 출력문
    public NotificationCompat.Builder getbuilder(String channel_id, String channel_name){
        //낮은 버전을 사용하는 사용자가 있는 경우에 대한 처리 - 8.0부터 채널을 통해서 관리하도록 처리
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {//안드로이드 8.0
            //채널을 만들고 등록
            NotificationChannel channel = new NotificationChannel(channel_id, channel_name, NotificationManager.IMPORTANCE_HIGH);
            //NotificationManager를 통해서 채널을 등록한다. - Builder에 의해서 만들어진 Notification은 채널에 의해 관리
            channel.enableLights(true); //LED사용유무
            channel.setLightColor(Color.RED);
            channel.enableVibration(true);//진동
            notificationManager.createNotificationChannel(channel);
            builder = new NotificationCompat.Builder(this,channel_id);

        }else{
            //이전버전은 옛날방식으로 Builder를 얻어오기
            builder = new NotificationCompat.Builder(this,channel_id);
        }
        return builder;
    }


    public void onMapReady(@NonNull GoogleMap googleMap) {

        gmap = googleMap;

        gmap.setOnMapClickListener(latLng2 -> {

            MarkerOptions markerOptions = new MarkerOptions().position(latLng2);
            Marker marker = gmap.addMarker(markerOptions);


            latLngList.add(latLng2);
            markerList.add(marker);
        });

        this.googleMap = googleMap;
        //37.56, 126.97 서울
        LatLng latLng = new LatLng(37.56, 126.97);
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMap.moveCamera(CameraUpdateFactory.zoomTo(7));


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
        } else {
            checkLocationPermissionWithRationale();
        }
    }


    // GPS 권위 설정
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    private void checkLocationPermissionWithRationale() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                new AlertDialog.Builder(this)
                        .setTitle("위치정보")
                        .setMessage("이 앱을 사용하기 위해서는 위치정보에 접근이 필요합니다. 위치정보 접근을 허용하여 주세요.")
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        }).create().show();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        googleMap.setMyLocationEnabled(true);
                    }
                } else {
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    // 전화번호 권한 요청
    private void callPermission() {
        // Check the SDK version and whether the permission is already granted or not.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_ACCESS_FINE_LOCATION);

        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){

            requestPermissions(
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    PERMISSIONS_ACCESS_COARSE_LOCATION);
        } else {
            isPermission = true;
        }
    }

}
