package jp.ac.titech.itpro.sdl.mylog;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private final static String TAG = "MainActivity";

    private GoogleMap googleMap;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private boolean requestingLocationUpdate;
    private enum UpdatingState {STOPPED, REQUESTING, STARTED}

    private UpdatingState state = UpdatingState.STOPPED;

    private final static String[] PERMISSIONS = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
    };

    private final static int REQCODE_PERMISSIONS = 1111;


    private Location fNowLocation; //現在地の座標を保存
    /*
    場所のリスト
    アプリが起動されるとlogファイルからデータを読み込み
    リストに表示する
    アプリが終了する時に現在のリストの状態を保存する.
     */
    private ArrayList<TLocationData> fLocationList;
    String filename = "locationLog.txt"; //ログを入れるファイル



    /** 
     *onCreate 
     *Activityが初めて生成された時に呼ばれる初期化処理. 
     * @param savedInstanceState 
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Mapの表示のためのMapFragment
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map_fragment);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap map) {
                map.moveCamera(CameraUpdateFactory.zoomTo(15f));
                googleMap = map;
            }
        });

        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        locationRequest = new LocationRequest();
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(16);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        //locationListの初期化
        fLocationList = new ArrayList<>();

    }

    /** 
     * onStart 
     * Activityが開始された時に呼ばれる. 
     * onStopに対応する. 
     */
    @Override
    protected void onStart() {
        Log.d(TAG, "onStart");

        //locationListの初期化
        fLocationList = new ArrayList<>();
        readfile(filename, fLocationList);
        for(int i = 0;i < fLocationList.size(); i++) {
            Log.d(TAG, "fName [ " + i + " ]: " + fLocationList.get(i).getfName());
            Log.d(TAG, "fLatitude  [ " + i + " ]:  " + fLocationList.get(i).getfLatitude());
            Log.d(TAG, "fLongitude  [ " + i + " ]:  " + fLocationList.get(i).getfLongitude());
        }

        super.onStart();
        googleApiClient.connect();
    }

    /** 
     * onResume 
     * Activityが表示された時に呼ばれる. 
     * onPauseに対応する. 
     */
    @Override
    protected void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();
        if (state != UpdatingState.STARTED && googleApiClient.isConnected())
            startLocationUpdate(true);
        else
            state = UpdatingState.REQUESTING;
    }

    /** 
     * onPause 
     * 他のアクティビティが開始された時に呼ばれる.
     */
    @Override
    protected void onPause() {
        Log.d(TAG, "onPause");
        if (state == UpdatingState.STARTED)
            stopLocationUpdate();
        super.onPause();
    }

    /** 
     * onStop 
     * Activityが非表示になった時に呼ばれる. 
     */
    @Override
    protected void onStop() {
        Log.d(TAG, "onStop");
        writeFile(filename);
        deleteFile(filename);
        googleApiClient.disconnect();
        super.onStop();
    }

    /** 
     * onConnected 
     * GoogleApiClientの接続に成功した時に呼ばれる. 
     * @param bundle 
     * */
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "onConnected");
        if (state == UpdatingState.REQUESTING)
            startLocationUpdate(true);
    }

    /** 
     * onConnectionSuspended 
     * GoogleApiClientの接続が中断された時に呼ばれる. 
     * */
    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "onConnectionSuspented");
    }

    /**
     * onConnectionFailed 
     * GoogleApiClientの接続に失敗した時に呼ばれる. 
     * @param connectionResult 
     */
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed");
    }

    /** 
     * onLocationChanged 
     * 位置情報が更新されると呼ばれる. 
     * @param location 
     */
    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "onLocationChanged: " + location);
        fNowLocation = new Location(location);
        //nowLocation.setLatitude(location.getLatitude());
        //nowLocation.setLongitude(location.getLongitude());
        //googleMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));
    }

    @Override
    public void onRequestPermissionsResult(int reqCode,
                                           @NonNull String[] permissions, @NonNull int[] grants) {
        Log.d(TAG, "onRequestPermissionsResult");
        switch (reqCode) {
            case REQCODE_PERMISSIONS:
                startLocationUpdate(false);
                break;
        }
    }

    private void startLocationUpdate(boolean reqPermission) {
        Log.d(TAG, "startLocationUpdate: " + reqPermission);
          /*拡張for文 
          PERMISSIONS内の要素を全て取り出したら終了 
          取り出した要素はpermissionに入れる. 
          */
        for (String permission : PERMISSIONS) {
            //permissionのチェック
            if (ContextCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                if (reqPermission)
                    ActivityCompat.requestPermissions(this, PERMISSIONS, REQCODE_PERMISSIONS);
                else
                    Toast.makeText(this, getString(R.string.toast_requires_permission, permission),
                            Toast.LENGTH_SHORT).show();
                return;
            }
        }
        //位置情報の取得
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
        state = UpdatingState.STARTED;
    }

    private void stopLocationUpdate() {
        Log.d(TAG, "stopLocationUpdate");
        LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
        state = UpdatingState.STOPPED;
    }

    public void moveCamera(View view) {
        System.out.println("push Button");

        //if(fNowLocation.getLatitude() != Double.NaN && fNowLocation.getLongitude() != Double.NaN){
          //  googleMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(fNowLocation.getLatitude(), fNowLocation.getLongitude())));
//            googleMap.addMarker(new MarkerOptions().position(new LatLng(fNowLocation.getLatitude(), fNowLocation.getLongitude()))
//                    .title("Latitude : " + fNowLocation.getLatitude() + "Longitude : " + fNowLocation.getLongitude()));
//             googleMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(35.516963, 139.481467)));
//            googleMap.addMarker(new MarkerOptions().position(new LatLng(35.516963, 139.481467))
//                 .title("Latitude : " + 35.516963 + "Longitude : " + 139.481467));
//      }



        TLocationData locationData = new TLocationData("aaa", 35.516963, 139.481467);

        fLocationList.add(locationData);
        //writeFile(filename);

//        ArrayList<TLocationData> locationList = new ArrayList<>();

//        TLocationData readlocation = new TLocationData();

    }

    public void writeFile(String filename) {

        try{

            OutputStream out = openFileOutput(filename, MODE_PRIVATE);
            PrintWriter pw = new PrintWriter(new OutputStreamWriter(out, "UTF-8"));

            String str = String.valueOf(fLocationList.size());
            pw.append(str + "\n");

            for(int i = 0; i< fLocationList.size(); i++){
                str = fLocationList.get(i).toString();
                pw.append(str);
            }

            pw.close();
        }catch (Exception e){
            System.out.println("ファイル書き込みエラー");
        }
        Log.d(TAG,"writefile");
    }

    public void readfile(String filename, ArrayList<TLocationData> locationList){
        try{
            InputStream inputStream = openFileInput(filename);
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));

            int locationListSize = Integer.parseInt(br.readLine());
            TLocationData tmp = new TLocationData();
            for(int i = 0; i < locationListSize; i++){
                tmp.readFrom(br);
                locationList.add(tmp);
            }

            Log.d(TAG,"readfile");
        }catch (FileNotFoundException e){
            System.out.println("ファイル読み込みエラー");
        }catch (UnsupportedEncodingException e){
            Log.d(TAG,"UnsupportedEncodingException");
        }catch (IOException e){
            Log.d(TAG,"IOException");
        }
    }
}