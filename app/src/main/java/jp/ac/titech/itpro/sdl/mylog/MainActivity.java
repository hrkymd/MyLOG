package jp.ac.titech.itpro.sdl.mylog;

import android.Manifest;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

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
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private final static String TAG = "MainActivity";

    private GoogleMap googleMap = null;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private enum UpdatingState {STOPPED, REQUESTING, STARTED}

    private UpdatingState state = UpdatingState.STOPPED;

    private final static String[] PERMISSIONS = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
    };

    private final static int REQCODE_PERMISSIONS = 1111;

    private Location fNowLocation; //現在地の座標を保存
    private Marker fNowMarker; //現在地のマーカー

    /*
    場所のリスト
    アプリが起動されるとlogファイルからデータを読み込み
    リストに表示する
    アプリが終了する時に現在のリストの状態を保存する.
     */
    private ArrayList<TLocationData> fLocationList = null;
    String filename = "locationLog.txt"; //ログを入れるファイル
    private GridView fListView = null; //表示するリストビュー
    private TLocationListArrayAdapter fAdapter = null; //リスト表示のためのアダプター
    private LinearLayout fMainLayout;
    private InputMethodManager fInputMethodManager;

    //マーカーのリスト
    private ArrayList<Marker> fMarkerList = null;
    //種類選択のスピナー
    private Spinner typeSpinner;

    boolean firstPlace; // 最初に現在地を表示するためのフラフ

    RadioGroup fRadioGroup;

    /** 
     *onCreate 
     *Activityが初めて生成された時に呼ばれる初期化処理. 
     * @param savedInstanceState 
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.d(TAG, "onCrete");

        //deleteFile(filename);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Mapの表示のためのMapFragment
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map_fragment);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap map) {
                map.moveCamera(CameraUpdateFactory.zoomTo(1f));
                googleMap = map;
            }
        });

        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        locationRequest = new LocationRequest();
        locationRequest.setInterval(5000); //5秒間隔
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        //locationListの初期化
        fLocationList = new ArrayList<>();
        readfile(filename);

        //LayoutファイルのListViewのリソースID
        fListView = (GridView) findViewById(R.id.place_List);

        //row.xmlによるレイアウト
        fAdapter = new TLocationListArrayAdapter(this, R.layout.row, fLocationList);
        fMainLayout = (LinearLayout)findViewById(R.id.mainLayout);
        fInputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);

        fListView.setAdapter(fAdapter);

        //ListViewアイテムを選択した場合の動作
        fListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //キーを隠す
                fInputMethodManager.hideSoftInputFromWindow(fMainLayout.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                //背景にフォーカスを移す
                fListView.requestFocus();

                //カメラを動かす
                moveCamera(fLocationList.get(position));

                //選択したListViewアイテムを表示する
                GridView list = (GridView) parent;
                String selectedItem = list.getItemAtPosition(position).toString();
                //Toast.makeText(getApplicationContext(), selectedItem, Toast.LENGTH_SHORT).show();
                Log.d(TAG, selectedItem);

            }
        });

        //ListViewアイテムの長押しでListViewアイテムを削除する
        //リスナーはAdapterView.onItemLongClickListener()を利用する
        fListView.setOnItemLongClickListener(new GridView.OnItemLongClickListener(){
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG,"Longclick");

                //キーボードを隠す
                fInputMethodManager.hideSoftInputFromWindow(fMainLayout.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                GridView list = (GridView) parent;
                TLocationData selectedItem = (TLocationData) list.getItemAtPosition(position);

                Log.d(TAG, "Long click : " + selectedItem + "position : " + position);
                showDialogFragment(selectedItem, position);

                return true;
            }
        });

        //fMarkerListの初期化
        fMarkerList = new ArrayList<>();

        ArrayAdapter spinnerAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item);
        spinnerAdapter.add("FOOD");
        spinnerAdapter.add("STORE");
        spinnerAdapter.add("SIGHTSEEING");
        spinnerAdapter.add("OTHER");
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        typeSpinner = (Spinner) findViewById(R.id.type_spinner);
        typeSpinner.setAdapter(spinnerAdapter);

        //初回起動時にnowLocationにカメラを移動するためにfalseにする
        firstPlace = false;

        fRadioGroup = (RadioGroup)findViewById(R.id.RadioGroup);
        fRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int id) {
                if (-1 == id) {
                    Toast.makeText(MainActivity.this, "クリアされました", Toast.LENGTH_SHORT).show();
                }
                else {
                    Log.d(TAG, "sort");
                    RadioButton radioButton = (RadioButton) findViewById(id);
                    sortOfList((String) radioButton.getText());
                }
            }
        });

    }

    /** 
     * onStart 
     * Activityが開始された時に呼ばれる. 
     * onStopに対応する. 
     */
    @Override
    protected void onStart() {
        Log.d(TAG, "onStart");
        super.onStart();
        if(firstPlace != false) {
            readfile(filename);
            fAdapter = new TLocationListArrayAdapter(this, R.layout.row, fLocationList);
            fListView.setAdapter(fAdapter);
        }
        //deleteFile(filename);
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
        //readfile(filename);

        if (state != UpdatingState.STARTED && googleApiClient.isConnected()) {
            startLocationUpdate(true);
        }
        else {
            state = UpdatingState.REQUESTING;
        }
    }

    /** 
     * onPause 
     * 他のアクティビティが開始された時に呼ばれる.
     */
    @Override
    protected void onPause() {
        Log.d(TAG, "onPause");
        //writeFile(filename);
        //deleteFile(filename);

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
        //deleteFile(filename);
        //Listの内容を削除
        fLocationList.clear();

        for(int i = 0; i < fMarkerList.size(); i++){
            fMarkerList.get(i).remove();
        }
        fMarkerList.clear();

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

        //マーカーの表示
        addMarkerToMap();

        //マーカーをタップした時のイベント
        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                // タップされたマーカーのタイトルを取得
                //String name = marker.getTitle().toString();

                //取得したタイトルをトーストで表示
                //Toast.makeText(MainActivity.this, name, Toast.LENGTH_SHORT).show();
                return false;
            }
        });
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
        //fNowLocation.setLatitude(location.getLatitude());
        //fNowLocation.setLongitude(location.getLongitude());
        if(firstPlace == false) {
            googleMap.moveCamera(CameraUpdateFactory.zoomTo(18f));
            googleMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));
            firstPlace = true;
        }

        if(fNowMarker != null) {
            fNowMarker.remove();
        }

        fNowMarker = googleMap.addMarker(new MarkerOptions().position(new LatLng(fNowLocation.getLatitude(), fNowLocation.getLongitude()))
                .title("You're in here.").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_my_location)));
        //addMarkerToMap(fNowLocation);
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

        if(googleMap != null){

            //マップ長押し時のイベント
            googleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                @Override
                public void onMapLongClick(LatLng latLng) {
                    //キーボードを隠す
                    fInputMethodManager.hideSoftInputFromWindow(fMainLayout.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                    //EditTextから名前を取得
                    EditText editText = (EditText) findViewById(R.id.input_place_info);
                    String entry = editText.getText().toString();

                    //spinnerから種類を取得
                    String type = (String)typeSpinner.getSelectedItem();
                    int typeNum = typeSpinner.getSelectedItemPosition();

                    //EditTextから名前を取得
                    EditText descriptionText = (EditText) findViewById(R.id.description_text);
                    String desc = descriptionText.getText().toString();

                    //座標から住所を調べる
                    String address = null;
                    try {
                        address = getAddress(latLng.latitude, latLng.longitude);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Log.d(TAG, address);

                    TLocationData locationData = new TLocationData(entry, latLng.latitude,latLng.longitude, address, type, typeNum, desc);
                    //ICONのセット
                    setIcons(locationData);

                    //名前がない場合は保存されない。
                    if(locationData.getfName().equals("")){
                        Log.d(TAG, "Entry is empty");
                    } else {
                        moveCamera(locationData);
                        //fLocationList.add(locationData);
                        addMarkerToMap(locationData);
                        fAdapter.add(locationData);
                    }

                    //登録後にエントリ文字列を削除する
                    editText.setText("");
                    descriptionText.setText("");
                }
            });
        }
    }

    private void stopLocationUpdate() {
        Log.d(TAG, "stopLocationUpdate");
        LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
        state = UpdatingState.STOPPED;
    }

    public void pushButton(View view) throws IOException {
        System.out.println("push Button");

        //キーボードを隠す
        fInputMethodManager.hideSoftInputFromWindow(fMainLayout.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

        //EditTextから名前を取得
        EditText editText = (EditText) findViewById(R.id.input_place_info);
        String entry = editText.getText().toString();

        //spinnerから種類を取得
        String type = (String)typeSpinner.getSelectedItem();
        int typeNum = typeSpinner.getSelectedItemPosition();

        //EditTextから名前を取得
        EditText descriptionText = (EditText) findViewById(R.id.description_text);
        String desc = descriptionText.getText().toString();

        //座標から住所を調べる
        String address = getAddress(fNowLocation.getLatitude(), fNowLocation.getLongitude());
        Log.d(TAG, address);

        TLocationData locationData = new TLocationData(entry, fNowLocation.getLatitude(), fNowLocation.getLongitude(), address, type, typeNum, desc);
        //ICONのセット
        setIcons(locationData);

        //名前がない場合は保存されない。
        if(locationData.getfName().equals("")){
            Log.d(TAG, "Entry is empty");
        } else {
            moveCamera(locationData);
            //fLocationList.add(locationData);
            addMarkerToMap(locationData);
            fAdapter.add(locationData);
        }

        //ボタン押下後にエントリ文字列を削除する
        editText.setText("");
        descriptionText.setText("");

    }

    private void writeFile(String filename) {

        try{

            OutputStream out = openFileOutput(filename, MODE_PRIVATE);
            PrintWriter pw = new PrintWriter(new OutputStreamWriter(out, "UTF-8"));

            String str = String.valueOf(fLocationList.size());
            pw.write(str + "\n");

            for(int i = 0; i< fLocationList.size(); i++){
                //String strn = fLocationList.get(i).toString();
                //pw.write(strn);
                fLocationList.get(i).writeTo(pw);
            }

            pw.close();
        }catch (Exception e){
            System.out.println("ファイル書き込みエラー");
        }
        Log.d(TAG,"writefile");
    }

    private void readfile(String filename){
        try{
            InputStream inputStream = openFileInput(filename);
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            int locationListSize = Integer.parseInt(br.readLine());
            for(int i = 0; i < locationListSize; i++){
                TLocationData tmp = new TLocationData();
                tmp.readFrom(br);
                setIcons(tmp);
                fLocationList.add(tmp);
            }

            Log.d(TAG,"readfile");
        }catch (FileNotFoundException e){
            System.out.println("1:ファイル読み込みエラー");
        }catch (UnsupportedEncodingException e){
            Log.d(TAG,"UnsupportedEncodingException");
        }catch (IOException e){
            Log.d(TAG,"IOException");
        }
    }

    /**
     * リスト内の全ての要素のマーカーを表示
     */
    private void addMarkerToMap() {
        for(int j = 0;j < fLocationList.size(); j++) {
            fMarkerList.add(googleMap.addMarker(fLocationList.get(j).getfMarkerOption()));
        }
    }

    /**
     * 新しく追加された要素のマーカーの表示
     * @param locationData
     */
    private void addMarkerToMap(TLocationData locationData) {
        fMarkerList.add(googleMap.addMarker(locationData.getfMarkerOption()));
    }

    private void removeMarkerFromMap(int position){
        fMarkerList.get(position).remove(); //マーカーを削除
        fMarkerList.remove(position); //マーカーリストから削除
    }

    /**
     * リスト内の要素が選択された時にその場所にカメラを動かす
     * @param locationData
     */
    private void moveCamera(TLocationData locationData){
        googleMap.moveCamera(CameraUpdateFactory.zoomTo(18f));
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(locationData.getfLatitude(), locationData.getfLongitude())));
    }

    /**
     * Locationの場所に移動
     *
     * @param location
     */
    private void moveCamera(Location location){
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));
    }

    //FragmentManagerdeDialogを管理する
    private void showDialogFragment(TLocationData selectedItem, int position){
        FragmentManager manager = getFragmentManager();
        DeleteDialog dialog = new DeleteDialog();
        dialog.setSelectedItem(selectedItem, position);

        dialog.show(manager, "dialog");

    }

    /*
    削除ダイアログを生成する内部クラス
     */
    public static  class DeleteDialog extends DialogFragment {

        private static final String DEBUG = "DEBUG";
        //選択したListViewアイテム
        private TLocationData selectedItem = null;
        private int fPosition;

        //削除したダイアログの作成
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState){
            Log.d(DEBUG, "onCreateDialog()");

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Delete entry");
            builder.setMessage("Are you really?");

            //positiveを選択した場合の処理
            builder.setPositiveButton("Yes I'm serious.", new DialogInterface.OnClickListener(){

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    MainActivity activity =(MainActivity) getActivity();
                    activity.removeItem(selectedItem, fPosition);
                }

            });
            AlertDialog dialog = builder.create();
            return  dialog;
        }

        //選択したアイテムをセットする
        public void setSelectedItem(TLocationData selectedItem, int position){
            Log.d(DEBUG, "setSelectedItem() - item : " + selectedItem);
            this.selectedItem = selectedItem;
            this.fPosition = position;
        }
    }

    //選択したアイテムを削除する
    protected void removeItem(TLocationData selectedItem, int position){
        Log.d(TAG, "doPositiveClock() - item : " + selectedItem);
        fAdapter.remove(selectedItem);
//        fLocationList.remove(position);
        removeMarkerFromMap(position);
    }

    public void setIcons(TLocationData locationData){
        switch (locationData.getfType()){
            case "FOOD":
                locationData.setfIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_restaurant));
                break;
            case "STORE":
                locationData.setfIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_local_mall));
                break;
            case "SIGHTSEEING":
                locationData.setfIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_photo_camera));
                break;
            case "OTHER":
                locationData.setfIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_star_border));
                break;
            default:
                locationData.setfIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
                break;
        }
    }

    public void sortOfList(String sortType){
        fAdapter.sort(sortType);
    }

    //座標を住所のStringへ変換
    public String getAddress(double latitude, double longitude) throws IOException{

        String string = new String();

        //geocoderの宣言
        Geocoder geocoder = new Geocoder(this.getApplicationContext(), Locale.JAPAN);
        List<Address> list_address = geocoder.getFromLocation(latitude, longitude, 5); //引数末尾は返す検索結果数

        //ジオコーディングに成功したらStringへ
        if (!list_address.isEmpty()){

            Address address = list_address.get(0);
            StringBuffer strbuf = new StringBuffer();

            //adressをStringへ
            for (int i = 1; i <= address.getMaxAddressLineIndex(); i++){
                strbuf.append(address.getAddressLine(i));
            }
            string = strbuf.toString();
        }
        //listが空の場合
        else {
            Log.d(TAG, "Failed Geocoding");
        }

        return string;
    }
}
