package com.simplelifesolution.colorApp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;


public class ResponseMainColors extends Activity {
    GridView grdVw;
    String strResponseFromIntent = "";
	
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_response_colors);

        getActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#33B5E5")));

      //  messageText  = (TextView)findViewById(R.id.messageText2);
        grdVw = (GridView)findViewById(R.id.gridView);

        Bundle extras = getIntent().getExtras();
        if (extras != null)
             strResponseFromIntent = extras.getString("respose_str");
        else
            Toast.makeText(this, "There was a problem in the response!", Toast.LENGTH_SHORT).show();


   // region --"take arraylist from response string using coma separator and remove white spaces"

        ArrayList<String> array_color = new ArrayList<String>(Arrays.asList(strResponseFromIntent.split("\\s*,\\s*")));

        StringBuilder str_totalClr = new StringBuilder();

        for(String each_color: array_color)
        {
            if(!each_color.equals("") || each_color!="" )
               { str_totalClr.append(each_color).append("\n"); }
        }

      //  messageText.setText(str_totalClr + String.valueOf(str_totalClr.length()));
   //endregion

        Custom_responseListAdapter gridAdapter = new Custom_responseListAdapter(ResponseMainColors.this, array_color);
        grdVw.setAdapter(gridAdapter);

        grdVw.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id)
            {
                Toast.makeText(getApplicationContext(), "ID:: "+ ((TextView) v.findViewById(R.id.grdrow_txt)).getText(), Toast.LENGTH_SHORT).show();
                Intent intnt_nav = new Intent();
                intnt_nav.setClass(ResponseMainColors.this, ComplementaryUploadToServer.class);
                intnt_nav.putExtra("xtra_selectedColor", ((TextView) v.findViewById(R.id.grdrow_txt)).getText());
                startActivity(intnt_nav);

            }
        });
    }




}