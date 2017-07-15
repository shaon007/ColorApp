package com.simplelifesolution.colorApp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class ComplementaryUploadToServer extends Activity {
	String serverResponseMessage;
	EditText edtTxtColorCode;
    Button btn_camera,btn_gallery, btn_upload, btn_navSimilarColr;
	ImageView imgavw_chosen ;
    int serverResponseCode = 0;

	HttpURLConnection conn = null;

    String upLoadServerUri = "http://simple-life-solutions.com/ceye/getcomplement.php";

	private final int FLAG_CAMERA = 1;
	private final int FLAG_GALLERY = 2;

	String strImgPath_choosen = null;
	//Bitmap btmp;
	Cursor mCursor;


	String strImgPath_response = null;

	private ProgressDialog pDialog;
	private final int FLAG_navSimilarColorAct = 3;


    @Override
    public void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complementary_color);

		getActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#33B5E5")));

		initialize();
		receiveIntent();
    }

	private void initialize() {
		edtTxtColorCode = (EditText)findViewById(R.id.etv_colorCode);
		btn_camera = (Button)findViewById(R.id.btnCamera_complemantary);
		btn_gallery = (Button)findViewById(R.id.btnGallery_complemantary);
		btn_navSimilarColr = (Button)findViewById(R.id.btn_navSimilarColor);
		imgavw_chosen = (ImageView)findViewById(R.id.imgVw_choosenImg);

		btn_upload = (Button)findViewById(R.id.btnUpload_complementary);

		//edtTxtColorCode.setText("ffff00");
	}

	private void receiveIntent() {
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			edtTxtColorCode.setText(extras.getString("xtra_selectedColor"));
		} else {
			Toast.makeText(this, "There was a problem in the response!", Toast.LENGTH_SHORT).show();
		}
	}

	public void buttonOnClick(View view)
	{
		switch (view.getId())
		{
			case R.id.btnCamera_complemantary:
				startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE), FLAG_CAMERA);

				break;

			case R.id.btnGallery_complemantary:
				startActivityForResult(
						new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI),	FLAG_GALLERY);
				break;

			case R.id.btnUpload_complementary:
				if(strImgPath_choosen == null || edtTxtColorCode.getText().toString().trim().length() == 0)
					Toast.makeText(this, "Please select a color and an image first!", Toast.LENGTH_SHORT).show();
				else
					new AsyncUpload(strImgPath_choosen, edtTxtColorCode.getText().toString()).execute(upLoadServerUri);
				break;
			case R.id.btn_navSimilarColor:
				if(edtTxtColorCode.getText().toString().trim().length() == 0)
					Toast.makeText(this, "Please type a hexcolor code!", Toast.LENGTH_SHORT).show();
				else
					{Intent intnt_similar = new Intent(ComplementaryUploadToServer.this, SimilarColors.class);
						intnt_similar.putExtra("xtraColor", edtTxtColorCode.getText().toString());
					 startActivityForResult(intnt_similar, FLAG_navSimilarColorAct);
					}
				break;

			default:
				break;
		}

	}


	class AsyncUpload extends AsyncTask<String, Integer, String> implements DialogInterface.OnDismissListener
	{
		String fileName = "";

		DataOutputStream dos = null;
		String lineEnd = "\r\n";
		String twoHyphens = "--";
		String boundary = "*****";
		int bytesRead, bytesAvailable, bufferSize;
		byte[] buffer;
		int maxBufferSize = 1 * 1024 * 1024;
		File sourceFile  ;
		String clrCode = "";

		String TAG = "uploadAsync";

		public AsyncUpload(String sourceFileUri , String clrCod)
		{
			super();

			fileName = sourceFileUri;
			sourceFile = new File(sourceFileUri);
			clrCode = clrCod;

		}

		@Override
		public void onDismiss(DialogInterface dialog)
		{
			this.cancel(true);
		}


		protected void onPreExecute ()
		{
			super.onPreExecute();

			Log.d(TAG + " PreExceute", "On pre Exceute......");

			//region --"progress dialog"
			pDialog = new ProgressDialog(ComplementaryUploadToServer.this);
			pDialog.setMessage("Please wait...");
			pDialog.setCancelable(true);
			pDialog.setOnDismissListener(this);
			pDialog.show();
			//endregion

			if (!sourceFile.isFile())
			{
				pDialog.dismiss();

				Log.d(TAG, "Source File not exist !" + strImgPath_choosen);

				serverResponseCode = 0;
			}
		}


		protected String doInBackground(String...arg0)
		{
			//Log.d(TAG + " DoINBackGround","On doInBackground...");
			String response = "";

			try
			{
				URL url = new URL(arg0[0]);

				// Open a HTTP  connection to  the URL
				conn = (HttpURLConnection) url.openConnection();
				//region -"set connection Properties"
				conn.setDoInput(true); // Allow Inputs
				conn.setDoOutput(true); // Allow Outputs
				conn.setUseCaches(false); // Don't use a Cached Copy
				conn.setRequestMethod("POST");
				conn.setRequestProperty("Connection", "Keep-Alive");
				conn.setRequestProperty("ENCTYPE", "multipart/form-data");
				conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
				//endregion

				dos = new DataOutputStream(conn.getOutputStream());


			//...............Start color parameter
				dos.writeBytes(twoHyphens + boundary + lineEnd);
				dos.writeBytes("Content-Disposition: form-data; name=\"color\"" + lineEnd);
				dos.writeBytes(lineEnd);
				dos.writeBytes(clrCode);
				dos.writeBytes(lineEnd);


				dos.writeBytes(twoHyphens + boundary + lineEnd);

				//for image attach
				dos.writeBytes("Content-Disposition: form-data; name=\"source\";filename=\"" + fileName + "\"" + lineEnd);

				dos.writeBytes(lineEnd);

				//region -"read file & convert to bytes"
				FileInputStream fileInputStream = new FileInputStream(sourceFile);

				// create a buffer of  maximum size
				bytesAvailable = fileInputStream.available();

				bufferSize = Math.min(bytesAvailable, maxBufferSize);
				buffer = new byte[bufferSize];

				// read file and write it into form...
				bytesRead = fileInputStream.read(buffer, 0, bufferSize);

				//endregion


				//region -"write bytes to output stream"
				while (bytesRead > 0) {

					dos.write(buffer, 0, bufferSize);
					bytesAvailable = fileInputStream.available();
					bufferSize = Math.min(bytesAvailable, maxBufferSize);
					bytesRead = fileInputStream.read(buffer, 0, bufferSize);

				}

				// final characters for the end of request
				dos.writeBytes(lineEnd);
				dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

				dos.flush();
				dos.close();
				//endregion

				// Responses from the server (code and message)
				serverResponseCode = conn.getResponseCode();
				serverResponseMessage = conn.getResponseMessage();


				if(serverResponseCode == 200) {
					try {
						InputStream inpt = conn.getInputStream();
						responseImageStore(inpt);
						conn.disconnect();
					  }
					catch (Exception ex) {
					}
				}

				//close the streams //
				fileInputStream.close();
				dos.flush();
				dos.close();
		}
			//region --"catch statements"
			catch (MalformedURLException ex){
				pDialog.dismiss();
				ex.printStackTrace();
				Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
		    }
			catch (Exception e) {
				pDialog.dismiss();
				e.printStackTrace();
				Log.e("server Upload Exception", "Exception : " + e.getMessage(), e);
		   }
			//endregion catchStatements

			//return response;
			return String.valueOf(serverResponseCode);
		}


		protected void onProgressUpdate(Integer...a){
			//Log.d(TAG + " onProgressUpdate", "You are in progress update ... " + a[0]);
		}

		protected void onPostExecute(String result)
		{
			pDialog.dismiss();

			if(serverResponseCode == 200 && result != "")
			{
				if(strImgPath_choosen ==null || strImgPath_response == null)
					Toast.makeText(ComplementaryUploadToServer.this, "There was a problem with the response! Please try again! " + strImgPath_response, Toast.LENGTH_SHORT).show();
				else{
				Intent intn_response = new Intent(ComplementaryUploadToServer.this, ResponseComplementary.class);
				intn_response.putExtra("imgPath_original",strImgPath_choosen);
				intn_response.putExtra("imgPath_response",strImgPath_response);
				startActivity(intn_response);}
			}

		}

	} //end of asyncTask


	//region .. "for camera & gallery  +++ onActivityResult "

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.v("resultCode","="+resultCode);
		if (resultCode == Activity.RESULT_OK)
		{
			mCursor = null;
			if (requestCode == FLAG_GALLERY)
				onSelectFromGalleryResult(data);
			else if (requestCode == FLAG_CAMERA)
				onCaptureImageResult(data);
			else if(requestCode == FLAG_navSimilarColorAct)
			{	//edtTxtColorCode.setText("changed");

			//	Bundle extras = data.getExtras();
				String stt = data.getStringExtra("intnt_similarColor");
				if (data != null)
					edtTxtColorCode.setText(stt);


			}


		}

	}


	private void onCaptureImageResult(Intent data)
	{
		Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

		try {
			imgavw_chosen.setImageBitmap(thumbnail);

			Uri tempUri = getCameraImageUri(getApplicationContext(), thumbnail);
			strImgPath_choosen = new File(getRealPathFromURI(tempUri)).toString(); //here new file creates & gets saved
			Log.v("resultCode","="+ strImgPath_choosen);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}


	private void onSelectFromGalleryResult(Intent data) {
		Bitmap thumbnail=null;

		if (data != null) {
			try {
				thumbnail = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());

				imgavw_chosen.setImageBitmap(thumbnail);

				Uri selectedImageUri = data.getData();
				strImgPath_choosen = getRealPathFromURI(selectedImageUri).toString();
				Log.v("resultCode","="+ strImgPath_choosen);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	public Uri getCameraImageUri(Context inContext, Bitmap inImage) {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
		String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
		return Uri.parse(path);
	}

	public String getRealPathFromURI(Uri uri) {
		Cursor cursor = getContentResolver().query(uri, null, null, null, null);
		cursor.moveToFirst();
		int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
		return cursor.getString(idx);
	}

	//endregion "camera + gallery + onActivityResult"


	private void responseImageStore(InputStream inpt)
	{
		BitmapFactory.Options bmOptions = new BitmapFactory.Options();
		bmOptions.inSampleSize = 8;

		Bitmap thumbnail = BitmapFactory.decodeStream(inpt,null,bmOptions);

		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

		try {
			Uri tempUri = getCameraImageUri(getApplicationContext(), thumbnail);
			strImgPath_response = new File(getRealPathFromURI(tempUri)).toString(); //here new file creates & gets saved
			Log.v("resultCode"," responseImg="+ strImgPath_response);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}




}