package com.simplelifesolution.colorApp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class SimilarColors extends Activity {
    GridView grdVw;
    String strIntentrecvdColor = "";
    String urlSimilar = "http://simple-life-solutions.com/ceye/colour.php";

    ArrayList<BeanSimilarColor> list_SimilarColors;
	
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_similar_colors);

       getActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#33B5E5")));

        initialize();
        receiveIntent();
       new AsyncConvertHtmlTags().execute();

       }


    private void initialize() {
        grdVw = (GridView)findViewById(R.id.gridViewSimilar);
        list_SimilarColors = new ArrayList<BeanSimilarColor>();

    }

    private void receiveIntent() {
        Bundle extras = getIntent().getExtras();
        if (extras != null)
            strIntentrecvdColor = extras.getString("xtraColor");
        else
            Toast.makeText(this, "There was a problem in the response!", Toast.LENGTH_SHORT).show();
    }

//region --"for similar color- api call with Get"

    private  String getWebcall(String url)
    {
        HttpClient httpclient = new DefaultHttpClient();
        HttpGet httpget = new HttpGet(url);
        HttpResponse response;

        try {
            response = httpclient.execute(httpget);
            HttpEntity entity = response.getEntity();

            if (entity != null)
            {
                InputStream instream = entity.getContent();
                /////////////String result= convertStreamToString(instream);
                instream.close();

                return "";
            }
            else
                return null;

        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }
    //endregion

    class AsyncConvertHtmlTags extends AsyncTask<String, Integer, String>
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params)
        { String responseStr = "";

            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
           // strIntentrecvdColor = "ff5050";//////////delete this line
            nameValuePairs.add(new BasicNameValuePair("c", strIntentrecvdColor));
            String paramsString = URLEncodedUtils.format(nameValuePairs, "UTF-8");

            HttpClient httpclient = new DefaultHttpClient();
            HttpGet httpget = new HttpGet(urlSimilar + "?" + paramsString);
            HttpResponse response;

            try {
                response = httpclient.execute(httpget);
                HttpEntity entity = response.getEntity();

                if (entity != null)
                {
                    InputStream instream = entity.getContent();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(instream));
                    StringBuilder str = new StringBuilder();
                    String line = null;
                    while((line = reader.readLine()) != null)
                    {
                        str.append(line);
                    }

                    responseStr = str.toString();

                    Log.d("similarColor", responseStr);

                    instream.close();

                    return responseStr;
                }
                else
                    return null;

            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }


            return responseStr;
        }



        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            list_SimilarColors.clear();


            BeanSimilarColor _objSimilarColor ;

            Document doc = Jsoup.parse(s);
            Elements divs = doc.select("div");

            //regex matcher to get the background values pattern to look for all characters between "background:#" and "'"
            Pattern p = Pattern.compile("(?<=background:#)(.*)(?=\")");

            for(Element elem : divs)
            {
                _objSimilarColor = new BeanSimilarColor();

               // String ss =  elem.attr("style");// for all the style properties

                Matcher m = p.matcher(elem.attributes().toString());

                while(m.find()){
                    String str_backgroundDivColor = m.group();
                    Log.d("similarColor", "\n jSoup Div background : " + str_backgroundDivColor ); //background value
                    _objSimilarColor.setColorHexCode(str_backgroundDivColor);
                }

                // text after the div which is the next sibling of the div
                    // Log.d("similarColor", "\n after Div : " +  elem.nextSibling().toString().trim() );

                String str_aftrDiv = elem.nextSibling().toString().trim();
                Log.d("similarColor", " after Div split: " + str_aftrDiv);
                _objSimilarColor.setColorType(str_aftrDiv);

//region --"for more filtering inside the html result string"
///////////////////// for more filtering the html result string inside the below liones will be needed /////////////////////
                /*String[] splitedOnColonArray = str_aftrDiv.split("\\s*:\\s*");
                String str_splitedColorType = splitedOnColonArray[1];
                 Log.d("similarColor", " after Div split: " + str_splitedColorType);
                _objSimilarColor.setColorType(str_splitedColorType);


                Matcher mt = Pattern.compile("\\(([^)]+)\\)").matcher(str_aftrDiv);
                while(mt.find()) {
                    String str_rgbColorCode = mt.group(1);
                    Log.d("similarColor", " after pattern: " + str_rgbColorCode);
                    _objSimilarColor.setColorRgbCode(str_rgbColorCode);
                }*/



              /*  if(elem.nextElementSibling()!= null ) {
                    if( elem.nextElementSibling().select("div") == null) //checck whether its a div element
                    {
                        // text after first br tag; the nextElementsibling returns the br element next sibling of this br is the text after br
                        Log.d("similarColor", "\n after br : " + elem.nextElementSibling().nextSibling().toString());
                    }
                }*/
//////////////////////////////////////////////////////////////////////
//endregion

                Log.d("similarColor","-----------------------");

                list_SimilarColors.add(_objSimilarColor);

            }


            Custom_SimilarColorListAdapter gridAdapter = new Custom_SimilarColorListAdapter(SimilarColors.this, list_SimilarColors);
            grdVw.setAdapter(gridAdapter);

            grdVw.setOnItemClickListener(new AdapterView.OnItemClickListener()
            {
                @Override
                public void onItemClick(AdapterView<?> parent, View v, int position, long id)
                {
                    String str_colorCodeSimilar = ((TextView) v.findViewById(R.id.listrow_similar_hexcode_hiddden)).getText().toString();
                    Toast.makeText(getApplicationContext(), "ID:: "+ str_colorCodeSimilar , Toast.LENGTH_SHORT).show();

                    Intent retrnIntnt = new Intent();
                    retrnIntnt.putExtra("intnt_similarColor", str_colorCodeSimilar);
                    setResult(RESULT_OK, retrnIntnt);
                    finish();

                }
            });

        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }
    }


}