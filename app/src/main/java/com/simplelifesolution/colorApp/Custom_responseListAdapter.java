package com.simplelifesolution.colorApp;


import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.app.Activity;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class Custom_responseListAdapter extends BaseAdapter
{
    private  Activity context;
    private ArrayList<String> names;

    static class ViewHolder {
        public LinearLayout lnLayout;
        public TextView txtVw;
    }

    public Custom_responseListAdapter(Activity context, ArrayList<String> names) {
        this.context = context;
        this.names = names;
        Log.d("mycolors", "1");
    }

    @Override
    public int getCount() {
        return names.size();
    }

    @Override
    public String getItem(int position) {
        return names.get(position) ;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    public View getView(int position, View convertView, ViewGroup parent)
    {
        View rowView = convertView;
        // reuse views
        if (rowView == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            rowView = inflater.inflate(R.layout.responselist_listitem, null);

            // configure view holder
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.lnLayout = (LinearLayout) rowView.findViewById(R.id.listrow_color);
            viewHolder.txtVw = (TextView)rowView.findViewById(R.id.grdrow_txt);

            rowView.setTag(viewHolder);
        }

        // fill data
        ViewHolder holder = (ViewHolder) rowView.getTag();

        /*int color_int = Integer.parseInt(names[position]);
        holder.lnLayout_colorHolder.setBackgroundColor(color_int);*/

        String colrCode = "#"+names.get(position);
      holder.lnLayout.setBackgroundColor(Color.parseColor(colrCode));
       // holder.lnLayout_colorHolder.setBackgroundColor(Color.parseColor("#f44298"));





      //  holder.txtVw.setTextColor(Integer.hex);


        holder.txtVw.setText(names.get(position));
        Log.d("mycolors", "\n 2 "+ names.get(position));

        return rowView;
    }


}
