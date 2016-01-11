package in.sarthy.solmate;

import android.animation.Animator;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import in.sarthy.solmate.httputils.MyCustomFeedManager;

public class profile extends AppCompatActivity {
    private String[] mainText=new String[9];
    private Boolean temp=false;
    private int col;
    private String[] edit=new String[3];
    private Typeface typeface;
    private Context mContext = this;
    private FloatingActionButton fab ;
    private FloatingActionButton fab1;
    private FloatingActionButton fab2;
    private FloatingActionButton fab3;
    private TextView label1;
    private TextView label2;
    private TextView label3;
    private boolean collapsed=true;
    private boolean fabclickable=true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab1 = (FloatingActionButton) findViewById(R.id.fab1);
        fab2 = (FloatingActionButton) findViewById(R.id.fab2);
        fab3 = (FloatingActionButton) findViewById(R.id.fab3);
        label1 = (TextView) findViewById(R.id.label1);
        label2 = (TextView) findViewById(R.id.editsave);
        label3 = (TextView) findViewById(R.id.label3);
        Drawable[] drs=new Drawable[9];
        TextView datause = (TextView) findViewById(R.id.data);
        typeface=datause.getTypeface();
        col=datause.getCurrentTextColor();
        drs[0] = getScaledDrawable(R.drawable.ic_person_black_18dp);
        drs[1] = getScaledDrawable(R.drawable.ic_mail_black_18dp);
        drs[2] = getScaledDrawable(R.drawable.ic_school_black_18dp);
        drs[3] = getScaledDrawable(R.drawable.ic_phone_black_18dp);
        drs[4] = getScaledDrawable(R.drawable.ic_account_balance_black_18dp);
        drs[5] = getScaledDrawable(R.drawable.ic_content_copy_black_18dp);
        drs[6] = getScaledDrawable(R.drawable.ic_done_black_18dp);
        drs[7] = getScaledDrawable(R.drawable.ic_done_all_black_18dp);
        drs[8] = getScaledDrawable(R.drawable.ic_subject_black_18dp);
        TableLayout tbLayout = (TableLayout) findViewById(R.id.table);
        for(int i=0;i<10;i++)
        {
            TableRow row = (TableRow) tbLayout.getChildAt(i);
            TextView button = (TextView) row.getChildAt(0); // get child index on particular row
            if(i<6) button.setCompoundDrawablesWithIntrinsicBounds(drs[i], null, null, null);
            else if(i>6) button.setCompoundDrawablesWithIntrinsicBounds(drs[i - 1], null, null, null);
        }
        final int duration = 250;
        final Handler handler = new Handler();
        final Runnable r = new Runnable() {
            public void run() {
                fabclickable=true;
            }
        };
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int  base = 40;
                if(fabclickable) {
                    if (!collapsed) {
                        fab.animate().rotation(0).setDuration(duration).start();
                        fab1.animate().translationYBy(dptopx(26 + base)).setDuration(duration).start();
                        fab2.animate().translationYBy(dptopx(86 + base)).setDuration(duration).start();
                        fab3.animate().translationYBy(dptopx(146 + base)).setDuration(duration).start();
                        label1.animate().translationYBy(dptopx(26 + base)).setDuration(duration).start();
                        label2.animate().translationYBy(dptopx(86 + base)).setDuration(duration).start();
                        label3.animate().translationYBy(dptopx(146 + base)).setDuration(duration).start();
                        label1.animate().alpha(0).setDuration(duration).start();
                        label2.animate().alpha(0).setDuration(duration).start();
                        label3.animate().alpha(0).setDuration(duration).start();

                    } else {
                        fab.animate().rotation(45).setDuration(duration).start();
                        fab1.animate().translationYBy(-dptopx(26 + base)).setDuration(duration).start();
                        fab2.animate().translationYBy(-dptopx(86 + base)).setDuration(duration).start();
                        fab3.animate().translationYBy(-dptopx(146 + base)).setDuration(duration).start();
                        label1.animate().translationYBy(-dptopx(26 + base)).setDuration(duration).start();
                        label2.animate().translationYBy(-dptopx(86 + base)).setDuration(duration).start();
                        label3.animate().translationYBy(-dptopx(146 + base)).setDuration(duration).start();
                        label1.animate().alpha(1).setDuration(duration).start();
                        label2.animate().alpha(1).setDuration(duration).start();
                        label3.animate().alpha(1).setDuration(duration).start();

                    }
                    collapsed = !collapsed;
                    fabclickable=false;
                    handler.postDelayed(r, duration);
                }
                }
        });

        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callhistoryApi("0", "0");
            }
        });
        fab3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                submitdialog_logout();
            }
        });
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (temp) {
                    EditText nameedit = (EditText) findViewById(R.id.test1);
                    EditText emailedit = (EditText) findViewById(R.id.test2);
                    EditText collegeedit = (EditText) findViewById(R.id.test3);
                    edit[0] = nameedit.getText().toString();
                    edit[1] = emailedit.getText().toString();
                    edit[2] = collegeedit.getText().toString();
                    calleditApi();
                    noteditable();
                    temp = false;
                } else {
                    temp = true;
                    editable();
                }
            }
        });
        for(int i=0;i<8;i++) mainText[i]="._.";
//        callApi();



    }
    @Override
    public void onStart()
    {super.onStart();
        try
        {
            JSONObject jObject = new JSONObject(getIntent().getStringExtra("json"));
            try{mainText[0]=jObject.getString("name");}catch (Exception e){mainText[0]="";}
        try{    mainText[1]=jObject.getString( "email");}catch (Exception e){mainText[1]="";}
    try{mainText[2]=jObject.getString("college");}catch (Exception e){mainText[2]="";}
        try{mainText[3]=jObject.getString("phone");}catch (Exception e){mainText[3]="";}
        try{mainText[4]=jObject.getString("balance");}catch (Exception e){mainText[4]="";}
        try{mainText[5]=jObject.getString("numSets");}catch (Exception e){mainText[5]="";}
        try{mainText[6]=jObject.getString("numSubmitted");}catch (Exception e){mainText[6]="";}
        try{mainText[7]=jObject.getString("numApproved");}catch (Exception e){mainText[7]="";}
            try{mainText[8]=jObject.getString("numAllotted");}catch (Exception e){mainText[8]="";}
            TableLayout tbLayout = (TableLayout) findViewById(R.id.table);
            for(int i=0;i<10;i++)
            {   if(i<3){
                TableRow row = (TableRow) tbLayout.getChildAt(i);
                EditText button = (EditText) row.getChildAt(1); // get child index on particular row
                button.setText(mainText[i]);
                continue;
            }
                TableRow row = (TableRow) tbLayout.getChildAt(i);
                TextView button = (TextView) row.getChildAt(1); // get child index on particular row
                if(i==3) button.setText("+91-"+mainText[i]);
                else if(i==4) button.setText("₹ "+mainText[i]);
                else if(i==5) button.setText(mainText[i]);
                else if(i>6) button.setText(mainText[i-1]);
            }
            noteditable();

        }catch (Exception e){
            Toast.makeText(mContext,"Something went wrong",Toast.LENGTH_LONG).show();
        }
    }

    public void submitdialog_logout(){

        LayoutInflater li = LayoutInflater.from(mContext);

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
        builder.setTitle("Log Out");
        builder.setMessage(R.string.logout);
        builder.setCancelable(false).setPositiveButton("LogOut",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // get user input and set it to result
                        // edit text
                        logout();
                    }
                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        builder.show();

    }
    public void logout() {
        logout task = new logout();
        filereader read = new filereader();
        String url = getApiEndpoint() + "/logout";
        long unixtime=System.currentTimeMillis() / 1000L;
        String payload="/solmate/logout"+unixtime;
        String sign = signature.hmacDigest(payload);
        task.execute(url, getapikey(), String.valueOf(unixtime), sign, read.readfile("token", profile.this), read.readfile("uid", profile.this));
    }
    class logout extends AsyncTask<String, Void, String> {
        ProgressDialog progDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progDialog = new ProgressDialog(mContext);
            progDialog.setMessage("Sending...");
            progDialog.setIndeterminate(false);
            progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progDialog.setCancelable(true);
            progDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    logout.this.cancel(true);
                    Toast.makeText(mContext, "Request Cancelled", Toast.LENGTH_LONG).show();
                }
            });
            progDialog.show();

        }

        @Override
        protected String doInBackground(String... params) {
            List<NameValuePair> post_params = new ArrayList<NameValuePair>();
            post_params.add(new BasicNameValuePair("apikey", params[1]));
            post_params.add(new BasicNameValuePair("timestamp", params[2]));
            post_params.add(new BasicNameValuePair("signature", params[3]));
            post_params.add(new BasicNameValuePair("token", params[4]));
            post_params.add(new BasicNameValuePair("uid", params[5]));

            try {
                HttpClient client = new DefaultHttpClient();
                String postURL = params[0];
                HttpPost post = new HttpPost(postURL);
                UrlEncodedFormEntity ent = new UrlEncodedFormEntity(post_params, HTTP.UTF_8);
                post.setEntity(ent);
                HttpResponse responsePOST = client.execute(post);
                HttpEntity resEntity = responsePOST.getEntity();
                if (resEntity != null) {
                    return EntityUtils.toString(resEntity);
                }
            } catch (Exception e) {
                e.printStackTrace();
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
                otp_success details= (otp_success) MyCustomFeedManager.getMappedModel(result, otp_success.class);
                try {
                    progDialog.dismiss();
                } catch (Exception e) {
                }
                if (!details.getSuccess()) {
                    if(details.getType().equals("auth")) cleandetails();
                    Toast.makeText(mContext, result+"Something went wrong", Toast.LENGTH_LONG).show();
                }

                else {
                    cleandetails();
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

    public void noteditable(){
        EditText[] editTextPassword= new EditText[3];

        editTextPassword[0] = (EditText)findViewById(R.id.test1);
        editTextPassword[1] = (EditText)findViewById(R.id.test2);
        editTextPassword[2] = (EditText)findViewById(R.id.test3);
        for(int i = 0; i<3;i++){
            editTextPassword[i].setFocusable(false);
            editTextPassword[i].setFocusableInTouchMode(false); // user touches widget on phone with touch screen
            editTextPassword[i].setClickable(false);
            editTextPassword[i].setBackgroundColor(Color.TRANSPARENT);
            editTextPassword[i].setTextColor(col);
            editTextPassword[i].setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            editTextPassword[i].setTypeface(typeface);
        }
        TextView text=(TextView)findViewById(R.id.editsave);
        text.setText("Edit");
        FloatingActionButton fab2 = (FloatingActionButton) findViewById(R.id.fab2);
        fab2.setImageDrawable(getResources().getDrawable(R.drawable.ic_create_white_36dp));

    }
    public void editable() {
        EditText[] editTextPassword= new EditText[3];

        editTextPassword[0] = (EditText)findViewById(R.id.test1);
        editTextPassword[1] = (EditText)findViewById(R.id.test2);
        editTextPassword[2] = (EditText)findViewById(R.id.test3);

        for(int i = 0; i<3;i++) {
            editTextPassword[i].setFocusable(true);
            editTextPassword[i].setFocusableInTouchMode(true);
            editTextPassword[i].setClickable(true);
            editTextPassword[i].setBackgroundResource(R.drawable.rounded_bg);
            editTextPassword[i].setTextColor(Color.parseColor("#ffffff"));
        }
        TextView text=(TextView)findViewById(R.id.editsave);
        text.setText("Save Details");
        FloatingActionButton fab2 = (FloatingActionButton) findViewById(R.id.fab2);
        fab2.setImageDrawable(getResources().getDrawable(R.drawable.ic_send_white_36dp));
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

    public void cleandetails(){

        FileOutputStream outputStream1;
        FileOutputStream outputStream2;
        FileOutputStream outputStream3;
        FileOutputStream outputStream5;
        FileOutputStream outputStream7;
        FileOutputStream outputStream8;
        try {

            outputStream1 = openFileOutput("token", Context.MODE_PRIVATE);
            outputStream2 = openFileOutput("expiresAt", Context.MODE_PRIVATE);
            outputStream3 = openFileOutput("uid", Context.MODE_PRIVATE);
            outputStream5 = openFileOutput("qsUrl", Context.MODE_PRIVATE);
            outputStream7 = openFileOutput("qsAllottedAt", Context.MODE_PRIVATE);
            outputStream8 = openFileOutput("qsExpiresAt", Context.MODE_PRIVATE);
            outputStream1.write("".getBytes());
            outputStream2.write("".getBytes());
            outputStream3.write("".getBytes());
            outputStream5.write("".getBytes());
            outputStream7.write("".getBytes());
            outputStream8.write("".getBytes());
            outputStream1.close();
            outputStream2.close();
            outputStream3.close();
            outputStream5.close();
            outputStream7.close();
            outputStream8.close();
        } catch (Exception e) {
        }
        Intent intent = new Intent(profile.this, OTP.class);
        startActivity(intent);
        Main.fa.finish();
        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
        finish();
    }

    public void callhistoryApi(String num,String last) {
        callhistoryApi task = new callhistoryApi();
        String url = getApiEndpoint() + "/history";
        long unixtime=System.currentTimeMillis() / 1000L;
        String payload="/solmate/history"+unixtime;
        String sign = signature.hmacDigest(payload);
        filereader read=new filereader();
        task.execute(url, getapikey(), String.valueOf(unixtime), sign, read.readfile("token", profile.this), read.readfile("uid",profile.this),num,last);
    }
    class callhistoryApi extends AsyncTask<String, Void, String> {
        ProgressDialog progDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progDialog = new ProgressDialog(profile.this);
            progDialog.setMessage("loading...");
            progDialog.setIndeterminate(false);
            progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progDialog.setCancelable(true);
            progDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    callhistoryApi.this.cancel(true);
                    Toast.makeText(mContext, "Request Cancelled", Toast.LENGTH_LONG).show();
                }
            });
            progDialog.show();

        }

        @Override
        protected String doInBackground(String... params) {
            List<NameValuePair> post_params = new ArrayList<NameValuePair>();
            post_params.add(new BasicNameValuePair("apikey", params[1]));
            post_params.add(new BasicNameValuePair("timestamp", params[2]));
            post_params.add(new BasicNameValuePair("signature", params[3]));
            post_params.add(new BasicNameValuePair("token", params[4]));
            post_params.add(new BasicNameValuePair("uid", params[5]));
            post_params.add(new BasicNameValuePair("num", params[6]));
            post_params.add(new BasicNameValuePair("start", params[7]));
            try {
                HttpClient client = new DefaultHttpClient();
                String postURL = params[0];
                HttpPost post = new HttpPost(postURL);
                UrlEncodedFormEntity ent = new UrlEncodedFormEntity(post_params, HTTP.UTF_8);
                post.setEntity(ent);
                HttpResponse responsePOST = client.execute(post);
                HttpEntity resEntity = responsePOST.getEntity();
                if (resEntity != null) {
                    return EntityUtils.toString(resEntity);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
//            try {
//                return MyCustomFeedManager.executePostRequest(params[0], post_params);
//            } catch (Exception e) {
//                e.printStackTrace();
//                Log.e("applogs", "Exception: " + e.getMessage());
//                Toast.makeText(profile.this, "Please check your internet connection and try again!"+e.getMessage(), Toast.LENGTH_LONG).show();
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
                        if(jObject.get("type").equals("auth")) cleandetails();
                        Toast.makeText(profile.this, "Something went wrong", Toast.LENGTH_LONG).show();
                    } else {
                        Intent intent = new Intent(profile.this, history.class);
                        Bundle b = new Bundle();
                        b.putString("para", result);
                        intent.putExtras(b);
                        startActivity(intent);
                        overridePendingTransition(R.anim.enter_from_right, R.anim.exit_out_left);
                        }

                    } catch (Exception e) {
                }
            }else {
                try {
                    progDialog.dismiss();
                } catch (Exception e) {
                }
                Toast.makeText(profile.this, " Please check your internet connection and try again!", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void calleditApi() {
        calleditApi task = new calleditApi();
        String url = getApiEndpoint() + "/update-profile";
        long unixtime=System.currentTimeMillis() / 1000L;
        String payload="/solmate/update-profile"+unixtime;
        String sign = signature.hmacDigest(payload);
        filereader read=new filereader();
        task.execute(url, getapikey(), String.valueOf(unixtime), sign, read.readfile("token", profile.this), read.readfile("uid", profile.this), edit[0],edit[1], edit[2]);
    }
    class calleditApi extends AsyncTask<String, Void, String> {
        ProgressDialog progDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progDialog = new ProgressDialog(profile.this);
            progDialog.setMessage("loading...");
            progDialog.setIndeterminate(false);
            progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progDialog.setCancelable(true);
            progDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    calleditApi.this.cancel(true);
                    Toast.makeText(mContext, "Request Cancelled", Toast.LENGTH_LONG).show();
                }
            });progDialog.show();

        }

        @Override
        protected String doInBackground(String... params) {
            List<NameValuePair> post_params = new ArrayList<NameValuePair>();
            post_params.add(new BasicNameValuePair("apikey", params[1]));
            post_params.add(new BasicNameValuePair("timestamp", params[2]));
            post_params.add(new BasicNameValuePair("signature", params[3]));
            post_params.add(new BasicNameValuePair("token", params[4]));
            post_params.add(new BasicNameValuePair("uid", params[5]));
            post_params.add(new BasicNameValuePair("name", params[6]));
            post_params.add(new BasicNameValuePair("email", params[7]));
            post_params.add(new BasicNameValuePair("college", params[8]));
            try {
                HttpClient client = new DefaultHttpClient();
                String postURL = params[0];
                HttpPost post = new HttpPost(postURL);
                UrlEncodedFormEntity ent = new UrlEncodedFormEntity(post_params, HTTP.UTF_8);
                post.setEntity(ent);
                HttpResponse responsePOST = client.execute(post);
                HttpEntity resEntity = responsePOST.getEntity();
                if (resEntity != null) {
                    return EntityUtils.toString(resEntity);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
//            try {
//                return MyCustomFeedManager.executePostRequest(params[0], post_params);
//            } catch (Exception e) {
//                e.printStackTrace();
//                Log.e("applogs", "Exception: " + e.getMessage());
//                Toast.makeText(profile.this, "Please check your internet connection and try again!"+e.getMessage(), Toast.LENGTH_LONG).show();
//                cancel(true);
//            }
            return null;
        }
        public String getStringfromJson(JSONObject j,String key)
        {
            try{
                return j.getString(key);
            }
            catch (Exception e){
                try{
                    return String.valueOf(j.getInt(key));
                }
                catch (Exception e2) {
                    return "";
                }
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (result!=null) {
                try {
                    final JSONObject jObject=new JSONObject(result);
                    progDialog.dismiss();
                    if (!jObject.getBoolean("success")) {
                        if(jObject.get("type").equals("auth")) cleandetails();
                        Toast.makeText(profile.this, "Something went wrong", Toast.LENGTH_LONG).show();
                    }
                    else{
                    }
                } catch (Exception e) {
                }
            }else {
                try {
                    progDialog.dismiss();
                } catch (Exception e) {
                }
                Toast.makeText(profile.this, result+" Please check your internet connection and try again!", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_down);
        finish();
    }

    public String getApiEndpoint() {
        return "http://appsdev.sarthy.in/solmate";
    }

    private String getapikey()
    {
        return "dffa5c8fb41a6716b6268ce81e45fa61";
    }

}
