package com.simplelifesolution.colorApp;


import android.app.Activity;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class Custom_SimilarColorListAdapter extends BaseAdapter
{
    private  Activity context;
    private ArrayList<BeanSimilarColor> _objSimilarColors;

    static class ViewHolder {
        public LinearLayout lnLayout_colorHolder;
        public TextView txtVwHexCode;
        public TextView txtVwTxt;
        public TextView txtVwHidden_hexCode;
    }

    public Custom_SimilarColorListAdapter(Activity context, ArrayList<BeanSimilarColor> names) {
        this.context = context;
        this._objSimilarColors = names;
    }

    @Override
    public int getCount() {
        return _objSimilarColors.size();
    }

    @Override
    public String getItem(int position) {
        return _objSimilarColors.get(position).toString() ;
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
            rowView = inflater.inflate(R.layout.similarcolor_listitem, null);

            // configure view holder
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.lnLayout_colorHolder = (LinearLayout) rowView.findViewById(R.id.listrow_similar_color);
            viewHolder.txtVwHexCode = (TextView) rowView.findViewById(R.id.listrow_similar_hexcode);
            viewHolder.txtVwTxt = (TextView) rowView.findViewById(R.id.listrow_similar_txt);
            viewHolder.txtVwHidden_hexCode = (TextView) rowView.findViewById(R.id.listrow_similar_hexcode_hiddden);

            rowView.setTag(viewHolder);
        }

        // fill data
        ViewHolder holder = (ViewHolder) rowView.getTag();

        String colrCode = "#"+_objSimilarColors.get(position).getColorHexCode();
      holder.lnLayout_colorHolder.setBackgroundColor(Color.parseColor(colrCode));
       // holder.lnLayout_colorHolder.setBackgroundColor(Color.parseColor("#f44298"));



      //  holder.txtVw.setTextColor(Integer.hex);

      //  holder.txtVwHexCode.setText(String.valueOf(getItemId(position)));
        holder.txtVwHexCode.setText("Hex Color code : " + _objSimilarColors.get(position).getColorHexCode());
       // Log.d("mysimilarcolors", "\n 2 "+ _objSimilarColors.get(position));
        holder.txtVwTxt.setText(_objSimilarColors.get(position).getColorType());

        holder.txtVwHidden_hexCode.setText(_objSimilarColors.get(position).getColorHexCode());

        return rowView;
    }


}
