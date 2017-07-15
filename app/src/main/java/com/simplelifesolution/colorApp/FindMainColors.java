package com.simplelifesolution.colorApp;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class FindMainColors extends Activity {
	String serverResponseMessage;
	ImageView imgVw_upload;
    Button cameraBtn, galleryBtn, uploadButton;
    int serverResponseCode = 0;

       
    String upLoadServerUri = "http://simple-life-solutions.com/ceye/getcolours.php";

	private final int TAG_CAMERA = 1;
	private final int TAG_GALLERY = 2;
    
	HttpURLConnection conn = null;
	BufferedInputStream responseStream;

	private ProgressDialog pDialog;

	String strImgPath = null;
	Cursor mCursor;

    @Override
    public void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_maincolors);

		getActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#33B5E5")));


		initialize();
    }

	private void initialize() {
		cameraBtn = (Button)findViewById(R.id.btnCamera);
		galleryBtn = (Button)findViewById(R.id.btnGallery);

		uploadButton = (Button)findViewById(R.id.btnUpload);
		imgVw_upload = (ImageView)findViewById(R.id.imgVw);
	}


	public void buttonOnClick(View view)
	{
		switch (view.getId())
		{
			case R.id.btnCamera:
				startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE), TAG_CAMERA);

				break;

			case R.id.btnGallery:
				startActivityForResult(
						new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI), TAG_GALLERY);
				break;

			case R.id.btnUpload:
				if(strImgPath == null)
					Toast.makeText(this, "Please select an image first!", Toast.LENGTH_SHORT).show();
				else
					new AsyncUpload(strImgPath).execute(upLoadServerUri);
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

		String TAG = "uploadAsync";

		public AsyncUpload(String sourceFileUri)
		{
			super();

			fileName = sourceFileUri;
			sourceFile = new File(sourceFileUri);
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
			pDialog = new ProgressDialog(FindMainColors.this);
			pDialog.setMessage("Please wait...");
			pDialog.setCancelable(true);
			pDialog.setOnDismissListener(this);
			pDialog.show();
			//endregion

			if (!sourceFile.isFile())
			{
				pDialog.dismiss();

				Log.d(TAG, "Source File not exist !" + strImgPath);

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

				dos.writeBytes(twoHyphens + boundary + lineEnd);

				dos.writeBytes("Content-Disposition: form-data; name=\"source\";filename=\"" + fileName + "\"" + lineEnd);
				dos.writeBytes("Content-Type: image/jpeg" +lineEnd);

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


					   /*Log.i("uploadFile", "HTTP Response is : " + serverResponseMessage + ": " + serverResponseCode);*/

				if(serverResponseCode == 200)
				{
					try {
						responseStream = new BufferedInputStream(conn.getInputStream());


						BufferedReader responseStreamReader = new BufferedReader(new InputStreamReader(responseStream));

						String line = "";
						StringBuilder stringBuilder = new StringBuilder();

						while ((line = responseStreamReader.readLine()) != null)
						{
							stringBuilder.append(line);
						}
						responseStreamReader.close();

						response = stringBuilder.toString();
						conn.disconnect();

					}
					catch(Exception ex)
					{}

				}

				//close the streams //
				fileInputStream.close();
				dos.flush();
				dos.close();
		}
			//region --"catch statements"
			catch (MalformedURLException ex) {

			pDialog.dismiss();
			ex.printStackTrace();

			/*runOnUiThread(new Runnable() {
				public void run() {
					messageText.setText("MalformedURLException Exception : check script url.");
					Toast.makeText(FindMainColors.this, "MalformedURLException", Toast.LENGTH_SHORT).show();
				}
			});*/

			Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
		} catch (Exception e) {

			pDialog.dismiss();
			e.printStackTrace();

			/*runOnUiThread(new Runnable() {
				public void run() {
					messageText.setText("Got Exception : see logcat ");
					Toast.makeText(FindMainColors.this, "Got Exception : see logcat ",
							Toast.LENGTH_SHORT).show();
				}
			});*/
			Log.e("server Upload Exception", "Exception : " + e.getMessage(), e);
		}
			//endregion catchStatements

			return response;
		}


		protected void onProgressUpdate(Integer...a){
			//Log.d(TAG + " onProgressUpdate", "You are in progress update ... " + a[0]);
		}

		protected void onPostExecute(String result)
		{
			pDialog.dismiss();
			if(serverResponseCode == 200 && result != "")
			{
				Log.i("uploadFile_3", "HTTP Response is : " + result);

				Intent intnt = new Intent(FindMainColors.this,ResponseMainColors.class);
				intnt.putExtra("respose_str", result);
				startActivity(intnt);
			}

		}
	}


	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.v("resultCode","="+resultCode);
		if (resultCode == Activity.RESULT_OK) {
			mCursor = null;
			if (requestCode == TAG_GALLERY)
				onSelectFromGalleryResult(data);
			else if (requestCode == TAG_CAMERA)
				onCaptureImageResult(data);


		}
	}


	private void onCaptureImageResult(Intent data)
	{
		Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

		try {
			imgVw_upload.setImageBitmap(thumbnail);

			Uri tempUri = getCameraImageUri(getApplicationContext(), thumbnail);
			strImgPath = new File(getRealPathFromURI(tempUri)).toString(); //here new file creates & gets saved
			Log.v("resultCode","="+ strImgPath);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}


	private void onSelectFromGalleryResult(Intent data) {
		Bitmap thumbnail=null;

		if (data != null) {
			try {
				thumbnail = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());

				imgVw_upload.setImageBitmap(thumbnail);

				Uri selectedImageUri = data.getData();
				strImgPath = getRealPathFromURI(selectedImageUri).toString();
				Log.v("resultCode","="+ strImgPath);
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


}