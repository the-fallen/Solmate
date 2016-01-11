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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import in.sarthy.solmate.httputils.MyCustomFeedManager;

public class otp_final2 extends Activity{

    private String phone="";
    private Button submit;
    private static final String TAG = otp_final2.class.getName();
    public static Activity fa;
    static EditText otp;
    private EditText name;
    private EditText email;
    private EditText college;
    private AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.8F);
    private Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fa = this;
        setContentView(R.layout.activity_otp_final2);
        Bundle b = this.getIntent().getExtras();
        otp=(EditText)findViewById(R.id.otp);
        name=(EditText)findViewById(R.id.name);
        email=(EditText)findViewById(R.id.email);
        college=(EditText)findViewById(R.id.college);

        otp.setCompoundDrawablesWithIntrinsicBounds(getScaledDrawable(R.drawable.ic_vpn_key_black_18dp),null,null,null);
        name.setCompoundDrawablesWithIntrinsicBounds(getScaledDrawable(R.drawable.ic_person_black_18dp),null,null,null);
        email.setCompoundDrawablesWithIntrinsicBounds(getScaledDrawable(R.drawable.ic_mail_black_18dp),null,null,null);
        college.setCompoundDrawablesWithIntrinsicBounds(getScaledDrawable(R.drawable.ic_school_black_18dp),null,null,null);
        otp.setCompoundDrawablePadding(dptopx(5));
        name.setCompoundDrawablePadding(dptopx(5));
        email.setCompoundDrawablePadding(dptopx(5));
        college.setCompoundDrawablePadding(dptopx(5));

        phone = b.getString("para");
        mContext = this;
        filereader read = new filereader();
        String key=read.readfile("token", getApplicationContext());
        if(key!="") {
            Intent intent = new Intent(mContext, Main.class);
            startActivity(intent);
            finish();
        }
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
    public int dptopx(int padding_in_dp)
    {
        final float scale = getResources().getDisplayMetrics().density;
        return  (int) (padding_in_dp * scale + 0.5f);

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
        String url = getApiEndpoint() + "/verify-otp";
        long unixtime=System.currentTimeMillis() / 1000L;
        String payload="/solmate/verify-otp"+unixtime;
        String sign = signature.hmacDigest(payload);
        task.execute(url, getapikey(), String.valueOf(unixtime), sign, phone, otp.getText().toString(),name.getText().toString(),email.getText().toString(),college.getText().toString());
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
            List<NameValuePair> post_params = new ArrayList<NameValuePair>();
            post_params.add(new BasicNameValuePair("apikey", params[1]));
            post_params.add(new BasicNameValuePair("timestamp", params[2]));
            post_params.add(new BasicNameValuePair("signature", params[3]));
            post_params.add(new BasicNameValuePair("phone", params[4]));
            post_params.add(new BasicNameValuePair("otp", params[5]));
            post_params.add(new BasicNameValuePair("name", params[6]));
            post_params.add(new BasicNameValuePair("email", params[7]));
            post_params.add(new BasicNameValuePair("college", params[8]));

            try {
                return MyCustomFeedManager.executePostRequest(params[0], post_params);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("applogs", "Exception: " + e.getMessage());
                Toast.makeText(mContext, "Please check your internet connection and try again!", Toast.LENGTH_LONG).show();
                cancel(true);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result!=null) {
                Submit_details submit_details= (Submit_details) MyCustomFeedManager.getMappedModel(result, Submit_details.class);
                try {
                    progDialog.dismiss();
                } catch (Exception e) {
                }
                if (!submit_details.getSuccess()) {
                    String error = "Something went wrong";
                    try{
                        if(submit_details.getType().equals("otp")) error = "wrong otp";
                    }catch (Exception e){}
                    Toast.makeText(mContext, error, Toast.LENGTH_LONG).show();
                    otp.setText("");
                } else {
                    FileOutputStream outputStream1;
                    FileOutputStream outputStream2;
                    FileOutputStream outputStream3;
                    FileOutputStream outputStream4;
                    FileOutputStream outputStream5;
                    FileOutputStream outputStream6;
                    FileOutputStream outputStream7;
                    FileOutputStream outputStream8;
                    FileOutputStream outputStream9;
                    try {

                        outputStream1 = openFileOutput("token", Context.MODE_PRIVATE);
                        outputStream2 = openFileOutput("expiresAt", Context.MODE_PRIVATE);
                        outputStream3 = openFileOutput("uid", Context.MODE_PRIVATE);
                        outputStream4 = openFileOutput("qsid", Context.MODE_PRIVATE);
                        outputStream5 = openFileOutput("qsUrl", Context.MODE_PRIVATE);
                        outputStream6 = openFileOutput("questions", Context.MODE_PRIVATE);
                        outputStream9 = openFileOutput("questions_status", Context.MODE_PRIVATE);
                        outputStream7 = openFileOutput("qsAllottedAt", Context.MODE_PRIVATE);
                        outputStream8 = openFileOutput("qsExpiresAt", Context.MODE_PRIVATE);
                        outputStream1.write(submit_details.getToken().getBytes());
                        outputStream2.write(submit_details.getExpiresAt());
                        outputStream3.write(submit_details.getUid().getBytes());
                        outputStream4.write(submit_details.getQsid().getBytes());
                        outputStream5.write(submit_details.getQsUrl().getBytes());
                        String[] qs = submit_details.getQuestions();
                        String qss = "",bits="";

                        for (int i = 0; i < qs.length - 1; i++) {
                            qss += qs[i];
                            qss += ";";
                            bits += "0";
                        }
                        bits+= "0";
                        qss += qs[qs.length - 1];
                        outputStream6.write(qss.getBytes());
                        outputStream7.write(submit_details.getQsAllottedAt().getBytes());
                        outputStream8.write(submit_details.getQsExpiresAt().getBytes());
                        outputStream9.write(bits.getBytes());
                        outputStream1.close();
                        outputStream2.close();
                        outputStream3.close();
                        outputStream4.close();
                        outputStream5.close();
                        outputStream6.close();
                        outputStream7.close();
                        outputStream8.close();
                        outputStream9.close();
                    } catch (Exception e) {
                        Log.e(TAG, "File not found: " + e.toString());
                    }
                    OTP.fa.finish();
                    Intent intent = new Intent(mContext, Main.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.enter_from_right, R.anim.exit_out_left);
                    finish();
                }
            }
            else {
                try {
                    progDialog.dismiss();
                } catch (Exception e) {
                }
                Toast.makeText(mContext,"Please check your internet connection and try again!", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        overridePendingTransition(R.anim.enter_from_left,R.anim.exit_out_right);
        finish();
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
        getMenuInflater().inflate(R.menu.menu_otp_final2, menu);
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
