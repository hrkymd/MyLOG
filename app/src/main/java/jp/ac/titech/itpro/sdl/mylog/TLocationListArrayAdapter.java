package jp.ac.titech.itpro.sdl.mylog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

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
}
