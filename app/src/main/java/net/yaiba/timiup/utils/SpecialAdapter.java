package net.yaiba.timiup.utils;

/**
 * Created by yang_lifeng on 2018/01/22.
 * listView 样式设置 重写SimpleAdapter
 */

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import net.yaiba.timiup.R;

public class SpecialAdapter extends SimpleAdapter {
    private int[] colors = new int[] { 0x30FF0000, 0x300000FF };

    public SpecialAdapter(Context context, List<? extends Map<String, ?>> items, int resource, String[] from, int[] to) {
        super(context, items, resource, from, to);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        //int colorPos = position % colors.length;
        //view.setBackgroundColor(colors[colorPos]);

        TextView t_hp=(TextView) view.findViewById(R.id.hp);
        TextView t_good_name=(TextView) view.findViewById(R.id.good_name);
        TextView t_lave_days=(TextView) view.findViewById(R.id.lave_days);
        TextView t_end_date=(TextView) view.findViewById(R.id.end_date);
        TextView t_status=(TextView) view.findViewById(R.id.status);

        String t_hp_tmp = t_hp.getText().toString().replaceAll("%", "");
        String t_status_tmp = t_status.getText().toString();

        if("0".equals(t_status_tmp)){//未使用

            if(Integer.valueOf(t_hp_tmp) >= 50){
                t_hp.setTextColor(Color.parseColor("#006400"));//<color name="darkgreen">#006400</color><!--暗绿色 -->
                t_hp.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));//加粗

                t_good_name.setTextColor(Color.parseColor("#000000"));
                t_lave_days.setTextColor(Color.parseColor("#000000"));
                t_end_date.setTextColor(Color.parseColor("#000000"));
                t_good_name.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
                t_lave_days.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
                t_end_date.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
                t_status.setText("√");
            } else if(Integer.valueOf(t_hp_tmp) >25 && Integer.valueOf(t_hp_tmp) <50 ){
                t_hp.setTextColor(Color.parseColor("#D2691E"));//<color name="chocolate">#D2691E</color><!--巧可力色 -->
                t_hp.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));//加粗

                t_good_name.setTextColor(Color.parseColor("#000000"));
                t_lave_days.setTextColor(Color.parseColor("#000000"));
                t_end_date.setTextColor(Color.parseColor("#000000"));
                t_good_name.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
                t_lave_days.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
                t_end_date.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
                t_status.setText("√");
            } else if(Integer.valueOf(t_hp_tmp) <= 25){
                t_hp.setTextColor(Color.parseColor("#DC143C"));//<color name="crimson">#DC143C</color><!--暗深红色 -->
                t_hp.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));//加粗
                //view.setBackgroundColor(Color.parseColor("#F4A460"));//<color name="sandybrown">#F4A460</color><!--沙褐色 -->

                t_good_name.setTextColor(Color.parseColor("#DC143C"));//<color name="crimson">#DC143C</color><!--暗深红色 -->
                t_lave_days.setTextColor(Color.parseColor("#DC143C"));//<color name="crimson">#DC143C</color><!--暗深红色 -->
                t_end_date.setTextColor(Color.parseColor("#DC143C"));//<color name="crimson">#DC143C</color><!--暗深红色 -->
                t_good_name.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));//加粗
                t_lave_days.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));//加粗
                t_end_date.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));//加粗
                t_status.setText("√");
            }

        } else {
            t_hp.setTextColor(Color.parseColor("#C0C0C0"));//<color name="lightgrey">#d3d3d3</color><!--亮灰色 -->
            t_good_name.setTextColor(Color.parseColor("#C0C0C0"));//<color name="lightgrey">#d3d3d3</color><!--亮灰色 -->
            t_lave_days.setTextColor(Color.parseColor("#C0C0C0"));//<color name="lightgrey">#d3d3d3</color><!--亮灰色 -->
            t_end_date.setTextColor(Color.parseColor("#C0C0C0"));//<color name="lightgrey">#d3d3d3</color><!--亮灰色 -->
            t_status.setText("X");
        }




        return view;
    }
}