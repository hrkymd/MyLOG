package jp.ac.titech.itpro.sdl.mylog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

public class TLocationData {
    private String fName; //場所の名前
    private double fLatitude; //場所の位置情報(緯度)
    private double fLongitude; //場所の位置情報(経度)
    private String fType; //場所の種類 (food, sightseeingなど)
    private String fDescription; //場所の説明
    private Date fDate; //日付

    /**
     * デフォルトコンストラクタ
     *
     */
    public TLocationData(){
        fName = null;
        fLatitude = Double.NaN;
        fLongitude = Double.NaN;
        fType = null;
        fDescription = null;
        fDate = new Date();
    }

    /**
     * セットコンストラクタ
     * @param fName
     * @param fLatitude
     * @param fLongitude
     * @param fType
     * @param fDescription
     * @param fDate
     */
    public TLocationData(String fName, double fLatitude, double fLongitude, String fType, String fDescription, Date fDate) {
        this.fName = fName;
        this.fLatitude = fLatitude;
        this.fLongitude = fLongitude;
        this.fType = fType;
        this.fDescription = fDescription;
        this.fDate = fDate;
    }

    public TLocationData(String fName, double fLatitude, double fLongitude ){
        this.fName = fName;
        this.fLatitude = fLatitude;
        this.fLongitude = fLongitude;

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
        String str = fName + "\n" + fLatitude + "\n" + fLongitude + "\n";
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
    public Date getfDate() {
        return fDate;
    }

    /**
     * setfDate
     * 日付の設定
     * @param fDate
     */
    public void setfDate(Date fDate) {
        this.fDate = fDate;
    }



}
