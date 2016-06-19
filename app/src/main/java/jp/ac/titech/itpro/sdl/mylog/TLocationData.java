package jp.ac.titech.itpro.sdl.mylog;

import android.util.Log;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;

public class TLocationData {
    private String fName; //場所の名前
    private double fLatitude; //場所の位置情報(緯度)
    private double fLongitude; //場所の位置情報(経度)
    private String fType; //場所の種類 (food, sightseeingなど)
    private String fDescription; //場所の説明
    private String fDate; //日付
    private MarkerOptions fMarkerOption; //マーカー情報

    /**
     * デフォルトコンストラクタ
     *
     */
    public TLocationData(){
        fName = "";
        fLatitude = Double.NaN;
        fLongitude = Double.NaN;
        fType = "";
        fDescription = "";
        fDate = "";
        fMarkerOption = new MarkerOptions();
    }

    /**
     * セットコンストラクタ
     * @param fName
     * @param fLatitude
     * @param fLongitude
     * @param fType
     * @param fDescription
     * @param fDate
     * @param fMarkerOption
     */
    public TLocationData(String fName, double fLatitude, double fLongitude, String fType, String fDescription, String fDate, MarkerOptions fMarkerOption) {
        this.fName = fName;
        this.fLatitude = fLatitude;
        this.fLongitude = fLongitude;
        this.fType = fType;
        this.fDescription = fDescription;
        this.fDate = fDate;
        this.fMarkerOption = fMarkerOption;
    }

    /**
     * コンストラクタ
     */
    public TLocationData(String fName, double fLatitude, double fLongitude ,String type){
        this.fName = fName;
        this.fLatitude = fLatitude;
        this.fLongitude = fLongitude;
        this.fType = type;

        Calendar calendar = Calendar.getInstance();
        fDate = calendar.get(Calendar.YEAR) + "/" + calendar.get(Calendar.MONTH) + "/" + calendar.get(Calendar.DATE);

        Log.d("a", fType);
        setMarkerOptions();


    }

    /**
     * コピ-コンストラクタ
     * @param src
     */
    public TLocationData(TLocationData src) {
        this.fName = src.fName;
        this.fLatitude = src.fLatitude;
        this.fLongitude = src.fLongitude;
        this.fType = src.fType;
        this.fDescription = src.fDescription;
        this.fDate = src.fDate;
        this.fMarkerOption = src.fMarkerOption;
    }

    /**
     * clone
     * cloneを生成する
     * @return
     */
    public TLocationData clone(){
        return  new TLocationData(this);
    }

    /**
     * toString
     * 情報を文字列で返す
     * --------------
     * fName
     * latitude (緯度)
     * longitude(経度)
     * fType
     * --------------
     * @return
     */
    public String toString(){
        String str = fName + "\n" + fLatitude + "\n" + fLongitude + "\n" + fType + "\n" + fDate + "\n";
        return str;
    }

    /**
     * writeTo
     * ファイルに書き込み
     * @param pw
     */
    public void writeTo(PrintWriter pw){
        pw.println(fName);
        pw.println(fLatitude);
        pw.println(fLongitude);
        pw.println(fType);
        pw.println(fDate);
    }

    /**
     * readFrom
     * ファイルから読み込み
     * @param br
     * @throws IOException
     */
    public void readFrom(BufferedReader br) throws IOException{
        fName = br.readLine();
        String strLatitude = br.readLine();
        String strLongitude = br.readLine();
        fLatitude = Double.parseDouble(strLatitude);
        fLongitude = Double.parseDouble(strLongitude);
        fType = br.readLine();
        fDate = br.readLine();

        setMarkerOptions();

    }


    /**
     * getfName
     * 場所名の取得
     * @return
     */
    public String getfName() {
        return fName;
    }

    /**
     * setfName
     * 場所の名前の設定
     * @param fName
     */
    public void setfName(String fName) {
        this.fName = fName;
    }

    /**
     * getfLatitude
     * 緯度の取得
     * @return
     */
    public double getfLatitude() {
        return fLatitude;
    }

    /**
     * setfLatitude
     * 緯度の設定
     * @param fLatitude
     */
    public void setfLatitude(double fLatitude) {
        this.fLatitude = fLatitude;
    }

    /**
     * getfLongitude
     * 経度の取得
     * @return
     */
    public double getfLongitude() {
        return fLongitude;
    }

    /**
     * setfLongitude
     * 経度の設定
     * @param fLongitude
     */
    public void setfLongitude(double fLongitude) {
        this.fLongitude = fLongitude;
    }


    /**
     * getfType
     * 場所の種類の取得
     * @return
     */
    public String getfType() {
        return fType;
    }

    /**
     * setfType
     * 場所の種類の設定
     * @param fType
     */
    public void setfType(String fType) {
        this.fType = fType;
    }

    /**
     * getfDescription
     * 説明の取得
     * @return
     */
    public String getfDescription() {
        return fDescription;
    }

    /**
     * setDescription
     * 説明の設定
     * @param fDescription
     */
    public void setfDescription(String fDescription) {
        this.fDescription = fDescription;
    }

    /**
     * getfDate
     * 日付の取得
     * @return
     */
    public String getfDate() {
        return fDate;
    }

    /**
     * setfDate
     * 日付の設定
     * @param fDate
     */
    public void setfDate(String fDate) {
        this.fDate = fDate;
    }


    /**
     * getfMarkerOption
     * マーカオプションの取得
     * @return
     */
    public MarkerOptions getfMarkerOption() {
        return fMarkerOption;
    }

    /**
     * setfMarkerOption
     * マーカーオプションの設定
     * @param fMarkerOption
     */
    public void setfMarkerOption(MarkerOptions fMarkerOption) {
        this.fMarkerOption = fMarkerOption;
    }

    private void setMarkerOptions(){

        String[] typeString = {"FOOD",
                "STORE",
                "SIGHTSEEING",
                "OTHER"};

        BitmapDescriptor[] icons = {BitmapDescriptorFactory.fromResource(R.drawable.ic_restaurant),
                BitmapDescriptorFactory.fromResource(R.drawable.ic_local_mall),
                BitmapDescriptorFactory.fromResource(R.drawable.ic_photo_camera),
                BitmapDescriptorFactory.fromResource(R.drawable.ic_star_border)};

        for(int i = 0; i < typeString.length; i++){
            if(this.fType.equals(typeString[i])){
                this.fMarkerOption = new MarkerOptions();
                this.fMarkerOption.position(new LatLng(this.fLatitude, this.fLongitude))
                        .title("Name : " + this.fName + " , type : " + fType + " Date : " + fDate )
                        .snippet( "Latitude : " + this.fLatitude + " , Longitude : " + this.fLongitude)
                        .icon(icons[i]);
                break;
            }
        }

    }
}

