package jp.ac.titech.itpro.sdl.mylog;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class TLocationListArrayAdapter extends ArrayAdapter<TLocationData> {
    // XMLからViewを生成するときに使う
    private LayoutInflater inflater;

    // リストアイテムのレイアウト
    private int textViewResourceId;

    // 表示するアイテム
    private ArrayList<TLocationData> items;

    /**
     * コンストラクタ
     * @param context
     * @param textViewResourceId
     * @param items
     */
    public TLocationListArrayAdapter(Context context, int textViewResourceId, ArrayList<TLocationData> items){
//    public TLocationListArrayAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId, items);
//        super(context, textViewResourceId);

        // リソースIDと表示アイテムを保持
        this.textViewResourceId = textViewResourceId;
        this.items = items;

        // ContextからLayoutInflaterを取得
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    /**
     * 1アイテム分のビューを取得
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        View view;

        // convertViewに入っていれば使う
        if(convertView != null){
            view = convertView;
        }
        // convertViewがnullなら新規作成
        else{
            view = inflater.inflate(textViewResourceId, null);
        }

        // 対象のアイテムを取得
        //TLocationData item = new TLocationData(items.get(position));

        // アイコンをイメージビューに表示
        ImageView imageView = (ImageView)view.findViewWithTag("icon");
        imageView.setImageBitmap(items.get(position).getfIcon());

        // テキストを表示
        TextView textView = (TextView)view.findViewWithTag("text");
        textView.setText(items.get(position).getListText());

        return view;
    }

    public void sort(final String sortType) {

        ArrayList<TLocationData> tmp;
        tmp = new ArrayList<>(items);

        Collections.sort(tmp, new Comparator<TLocationData>() {
            @TargetApi(Build.VERSION_CODES.KITKAT)
            @Override
            public int compare(TLocationData lhs, TLocationData rhs) {
                switch (sortType){
                    case ("DATE") : //日付の時
                        Log.d("A", "sortType = 0");
                        if(lhs.getfDate().compareTo(rhs.getfDate()) != 0)
                            return lhs.getfDate().compareTo(rhs.getfDate());
                        break;

                    case ("NAME") : //名前の時
                        Log.d("A", "sortType = 1");
                        if(lhs.getfName().compareTo(rhs.getfName()) != 0) {
                            return lhs.getfName().compareTo(rhs.getfName());
                        }
                        break;

                    case ("TYPE") : //種類の時
                        Log.d("A", "sortType = 2");
                        if(Integer.compare(lhs.getfTypeNum(), rhs.getfTypeNum()) != 0) {
                            return Integer.compare(lhs.getfTypeNum(), rhs.getfTypeNum());
                        }
                        break;
                }
                return 0;
            }
        });

        items.clear();
        items.addAll(tmp);
        this.notifyDataSetChanged();
    }
}
