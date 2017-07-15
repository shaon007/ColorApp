package com.simplelifesolution.colorApp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

public class ResponseComplementary extends Activity
{
	ImageView imgvw_original, imgvw_response;

	String _strImgPathOriginal = "";
	String _strImgPathResponse = "";

	private ProgressDialog pDialog;
	Bitmap bmp;

    @Override
    public void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complementary_response);

		getActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#33B5E5")));

		initialize();

		receiveAndSetImage();

    }

	private void initialize() {
		imgvw_original = (ImageView)findViewById(R.id.imgVw_choosenImg_compare);
		imgvw_response = (ImageView)findViewById(R.id.imgVw_responseImg);
	}

	private void receiveAndSetImage()
	{
		Bundle extras = getIntent().getExtras();
		if (extras != null)
		{
			_strImgPathOriginal = extras.getString("imgPath_original");
			_strImgPathResponse  = extras.getString("imgPath_response");
		} else {
			Toast.makeText(this, "There was a problem in the response!", Toast.LENGTH_SHORT).show();
		}

	if(_strImgPathOriginal.equals("") || _strImgPathResponse.equals(""))
		Toast.makeText(this, "There was a problem in the response!", Toast.LENGTH_SHORT).show();
	else
	{
		bmp = BitmapFactory.decodeFile(_strImgPathOriginal);
		imgvw_original.setImageBitmap(bmp);

		bmp = BitmapFactory.decodeFile(_strImgPathResponse);
		imgvw_response.setImageBitmap(bmp);

	}

	}



}