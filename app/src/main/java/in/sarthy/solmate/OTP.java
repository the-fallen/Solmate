package in.sarthy.solmate;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class OTP extends Activity {

    public Context mContext;
    private EditText phone=null;
    private AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.8F);
    private Button submit;
    private static final String TAG = OTP.class.getName();
    public static Activity fa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);
        fa=this;
        mContext = this;

        setContentView(R.layout.activity_otp);
        phone = (EditText)findViewById(R.id.editText);

        phone.setCompoundDrawablesWithIntrinsicBounds(getScaledDrawable(R.drawable.ic_phone_black_18dp), null, null, null);
        phone.setCompoundDrawablePadding(dptopx(5));
        phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {


            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.length()==10)
                {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(phone.getWindowToken(), 0);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        submit=(Button)findViewById(R.id.button);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                v.startAnimation(buttonClick);
                if (isInternetOn()) {
                    sendOTP();
                } else {
                    Toast.makeText(mContext, "Please check your internet connection and try again!", Toast.LENGTH_LONG).show();
                }
            }

        });


    }
    public int dptopx(int padding_in_dp)
    {
        final float scale = getResources().getDisplayMetrics().density;
        return  (int) (padding_in_dp * scale + 0.5f);

    }
    public final boolean isInternetOn() {
        ConnectivityManager connec =
                (ConnectivityManager)getSystemService(getBaseContext().CONNECTIVITY_SERVICE);

        if ( connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTED ||
                connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTED ) {

            return true;

        } else if (
                connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.DISCONNECTED ||
                        connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.DISCONNECTED  ) {

            return false;
        }
        return false;
    }
    private Drawable getScaledDrawable(int id) {

        int padding_in_dp = 20;
        final float scale = getResources().getDisplayMetrics().density;
        int padding_in_px = (int) (padding_in_dp * scale + 0.5f);

        int newWidth=padding_in_px, newHeight=padding_in_px;
        Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), id);

        Matrix matrix = new Matrix();
        float scaleWidth = ((float) newWidth) / bitmap.getWidth();
        float scaleHeight = ((float) newHeight) / bitmap.getHeight();
        matrix.postScale(scaleWidth, scaleHeight);
        matrix.postRotate(0);
        Bitmap scaledBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

        return new BitmapDrawable(this.getResources(), scaledBitmap);
    }

    public void sendOTP() {
        sendOTP task = new sendOTP();
        String url = getApiEndpoint() + "/send-otp";
        long unixtime=System.currentTimeMillis() / 1000L;
        String payload="/solmate/send-otp"+unixtime;
        String sign = signature.hmacDigest(payload);
        task.execute(url, getapikey(), String.valueOf(unixtime), sign, phone.getText().toString());

    }
    class sendOTP extends AsyncTask<String, Void, String> {
        ProgressDialog progDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progDialog = new ProgressDialog(mContext);
            progDialog.setMessage("Sending...");
            progDialog.setIndeterminate(false);
            progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progDialog.setCancelable(false);
            progDialog.show();

        }

        @Override
        protected String doInBackground(String... params) {
//            List<NameValuePair> post_params = new ArrayList<NameValuePair>();
//            post_params.add(new BasicNameValuePair("apikey", params[1]));
//            post_params.add(new BasicNameValuePair("timestamp", params[2]));
//            post_params.add(new BasicNameValuePair("signature", params[3]));
//            post_params.add(new BasicNameValuePair("phone", params[4]));
//            try {
//                HttpClient client = new DefaultHttpClient();
//                String postURL = params[0];
//                HttpPost post = new HttpPost(postURL);
//                UrlEncodedFormEntity ent = new UrlEncodedFormEntity(post_params, HTTP.UTF_8);
//                post.setEntity(ent);
//                HttpResponse responsePOST = client.execute(post);
//                HttpEntity resEntity = responsePOST.getEntity();
//                if (resEntity != null) {
//                    return EntityUtils.toString(resEntity);
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
            JSONObject jsonObject = new JSONObject();
            try {
                HttpResponse response;
                jsonObject.accumulate("apikey", params[1]);
                jsonObject.accumulate("timestamp", params[2]);
                jsonObject.accumulate("signature", params[3]);
                jsonObject.accumulate("phone", params[4]);
                String json = jsonObject.toString();
                URL url = new URL(params[0]);
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(url.toURI());
                httpPost.setEntity(new StringEntity(json, "UTF-8"));
//                httpPost.setHeader("Content-Type", "application/json");
//                httpPost.setHeader("Accept-Encoding", "application/json");
//                httpPost.setHeader("Accept-Language", "en-US");
                String sresponse = "";
                response = httpClient.execute(httpPost);
                if (response.getEntity() != null) {
                    sresponse = EntityUtils.toString(response.getEntity());
                }
//                Log.i("requestqs", params[0] + " " + sresponse + " " + json + " ");
                return sresponse;
            }
            catch (Exception e)
            {
                Log.e("error",e.getLocalizedMessage());
            }
//            try {
//                return MyCustomFeedManager.executePostRequest(params[0], post_params);
//            } catch (Exception e) {
//                e.printStackTrace();
//                Log.e("applogs", "Exception: " + e.getMessage());
//                Toast.makeText(mContext, "Please check your internet connection and try again!"+e.getMessage(), Toast.LENGTH_LONG).show();
//                cancel(true);
//            }
            return null;
        }


        @Override
        protected void onPostExecute(String result) {
            if (result!=null) {
                try {
                    final JSONObject jObject=new JSONObject(result);
                    progDialog.dismiss();
                if (!jObject.getBoolean("success")) {
                    String error = "Something went wrong";
                    try{
                        if(jObject.getString("type").equals("sms")) error = "Are you sure that's your number?";
                    }catch (Exception e){}
                    Toast.makeText(mContext, error, Toast.LENGTH_LONG).show();
                    phone.setText("");
                } else {
                    if (jObject.getBoolean("registered")) {
                        Intent intent = new Intent(mContext, otp_final.class);
                        Bundle b=new Bundle();
                        b.putStringArray("para", new String[]{jObject.getString("name"), phone.getText().toString()});
                        intent.putExtras(b);
                        startActivity(intent);
                        overridePendingTransition(R.anim.enter_from_right, R.anim.exit_out_left);
                    }
                    else
                    {
                        Intent intent = new Intent(mContext, otp_final2.class);
                        intent.putExtra("para", phone.getText().toString());
                        startActivity(intent);
                        overridePendingTransition(R.anim.enter_from_right, R.anim.exit_out_left);
                    }
                }} catch (Exception e) {
                }
            }else {
                try {
                    progDialog.dismiss();
                } catch (Exception e) {
                }
                Toast.makeText(mContext, " Please check your internet connection and try again!", Toast.LENGTH_LONG).show();
            }
        }
    }

    public String getApiEndpoint() {
        return "http://appsdev.sarthy.in/solmate";
    }

    private String getapikey()
    {
        return "dffa5c8fb41a6716b6268ce81e45fa61";
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_ot, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
