package com.example.kyshi.finding_lost_kids_application;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.example.kyshi.finding_lost_kid_application.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

public class mapFind  extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener{
String loc_name;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.place_search);
        //SupportMapFragment로 만든 레이아웃의 fragment 의 id 참조, 맵 호출
        SupportMapFragment mapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        // 구글 맵 객체를 불러온다.
        GoogleMap mMap;
        Intent intent = getIntent();
        String loc = (String) intent.getSerializableExtra("String");
        Toast.makeText(getApplicationContext(), loc, Toast.LENGTH_SHORT).show();
        loc_name = loc;
        final Geocoder geocoder = new Geocoder(this);

        List<Address> list = null;
        mMap = googleMap;

        try {
            list = geocoder.getFromLocationName(loc, 10);
        } catch (IOException ioe) {

        }
        if (list.size() == 0) {
            loc = "성균관대학교";
            Toast.makeText(getApplicationContext(), "Error: 주소가 존재하지않습니다.", Toast.LENGTH_LONG).show();


            try {
                list = geocoder.getFromLocationName(loc, 10);
                LatLng seoul = new LatLng(list.get(0).getLatitude(), list.get(0).getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(seoul, 15));
            } catch (IOException io) {

            }
        }
        // 검색 장소에 대한 위치 설정
        else
        {
            // LatLng seoul = new LatLng(list.get(0).getLatitude(), list.get(0).getLongitude());
            LatLng seoul = new LatLng(list.get(0).getLatitude(), list.get(0).getLongitude());

            //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(startingPoint,8));

            // 구글 맵에 표시할 마커에 대한 옵션 설정
            MarkerOptions makerOptions = new MarkerOptions();
            makerOptions
                    .position(seoul)
                    .title(loc);

            //마커를 생성한다.

            mMap.addMarker(makerOptions);
            //mMap.moveCamera(CameraUpdateFactory.newLatLng(seoul));
            //카메라를 여의도 위치로 옮긴다.
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(seoul, 15));

            // 마커 클릭 이벤트 리스너
            mMap.setOnMarkerClickListener(this);
        }
    }
    @Override
    public boolean onMarkerClick(Marker marker){
        Toast.makeText(getApplicationContext(),"Clicked",Toast.LENGTH_SHORT).show();
        final AlertDialog.Builder alertBuilder = new AlertDialog.Builder(
                mapFind.this);
        alertBuilder.setIcon(R.drawable.ic_menu_manage); // 아이콘 설정
        alertBuilder.setTitle(loc_name+": "+"선택하시겠습니까?"); // 타이틀 설정
        alertBuilder.setNegativeButton("네", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                boolean state = true; // true or false
                //서버 db 에서 가맹점이 맞는지 확인하고 구현
                Toast.makeText(getBaseContext(),"선택되었습니다.",Toast.LENGTH_SHORT);
                if(state)
                {
                    Intent intent = new Intent();
                    intent.putExtra("loc_name",loc_name);
                    setResult(2,intent);
                    finish();
                }
            }
        });

        alertBuilder.setPositiveButton("아니오", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertBuilder.show();
        return true;
    }
}
