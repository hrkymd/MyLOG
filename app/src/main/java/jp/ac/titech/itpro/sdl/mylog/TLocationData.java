package jp.ac.titech.itpro.sdl.mylog;

import android.graphics.Bitmap;
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
    private String fAddress; //
    private String fType; //場所の種類 (food, sightseeingなど)
    private int fTypeNum; //場所の種類のインデックス
    private String fDescription; //場所の説明
    private String fDate; //日付表示用
    private MarkerOptions fMarkerOption; //マーカー情報

    private Bitmap fIcon;//アイコン

    static public final String DATE_PATTERN ="yyyy/MM/dd/HH/mm:ss";

    /**
     * デフォルトコンストラクタ
     *
     */
    public TLocationData(){
        fName = "";
        fLatitude = Double.NaN;
        fLongitude = Double.NaN;
        fAddress = "";
        fType = "";
        fTypeNum = -1;
        fDescription = "";
        fDate = "";
        fMarkerOption = new MarkerOptions();
        fIcon = null;

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
    public TLocationData(String fName, double fLatitude, double fLongitude,String fAddress, String fType, int fTypeNum, String fDescription, String fDate, MarkerOptions fMarkerOption, Bitmap fIcon) {
        this.fName = fName;
        this.fLatitude = fLatitude;
        this.fLongitude = fLongitude;
        this.fAddress = fAddress;
        this.fType = fType;
        this.fTypeNum = fTypeNum;
        this.fDescription = fDescription;
        this.fDate = fDate;
        this.fMarkerOption = fMarkerOption;
        this.fIcon = fIcon;

    }

    /**
     * コンストラクタ
     */
    public TLocationData(String fName, double fLatitude, double fLongitude, String address,String type, int typeNum, String description){
        this.fName = fName;
        this.fLatitude = fLatitude;
        this.fLongitude = fLongitude;
        this.fAddress = address;
        this.fType = type;
        this.fTypeNum = typeNum;
        this.fDescription = description;
        this.fIcon = null;
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int date = calendar.get(Calendar.DATE);
        int hour;
        if (calendar.get(Calendar.AM_PM) == 0) {
            hour = calendar.get(Calendar.HOUR);
        }
        else {
            hour = calendar.get(Calendar.HOUR) + 12;
        }

        int minute = calendar.get(Calendar.MINUTE);
        fDate =  year + "/" + month + "/" + date + "/" + hour + ":" + minute ;

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
        this.fAddress = src.fAddress;
        this.fType = src.fType;
        this.fTypeNum = src.fTypeNum;
        this.fDescription = src.fDescription;
        this.fDate = src.fDate;
        this.fMarkerOption = src.fMarkerOption;
        this.fIcon = src.fIcon;
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
        String str = fName + "\n" + fLatitude + "\n" + fLongitude + "\n" + fAddress + "\n" + fType + "\n" + fTypeNum +  "\n" + fDate + "\n" + fDescription + "\n";
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
        pw.println(fAddress);
        pw.println(fType);
        pw.println(fTypeNum);
        pw.println(fDate);
        pw.println(fDescription);
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
        fAddress = br.readLine();
        fType = br.readLine();
        String strtypeNum = br.readLine();
        fTypeNum = Integer.parseInt(strtypeNum);
        fDate = br.readLine();
        fDescription = br.readLine();
        fIcon = null;

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
     * getfAddress
     * 住所の取得
     * @return
     */
    public String getfAddress() {
        return fAddress;
    }

    /**
     * setfAddress
     * 住所の設定
     * @param fAddress
     */
    public void setfAddress(String fAddress) {
        this.fAddress = fAddress;
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
     * getfTypeNum
     * 場所の種類インデックスの取得
     * @return
     */
    public int getfTypeNum() {
        return fTypeNum;
    }

    /**
     * setfTypeNum
     * 場所の種類インデックスの設定
     * @param fTypeNum
     */
    public void setfTypeNum(int fTypeNum) {
        this.fTypeNum = fTypeNum;
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
                        .snippet(this.fDescription)
                        .icon(icons[i]);
                break;
            }
        }

    }

    public Bitmap getfIcon() {
        return fIcon;
    }

    public void setfIcon(Bitmap fIcon) {
        this.fIcon = fIcon;
    }

    public String getListText(){
        return fName.toString() + "  " + fDate.toString() + "\n" + fAddress + "\n" + fDescription.toString();
    }

}

