package in.sarthy.solmate;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import in.sarthy.solmate.lazylist.ImageLoader;

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
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.support.v7.app.AlertDialog;

import in.sarthy.solmate.httputils.MyCustomFeedManager;

public class Main extends AppCompatActivity {

    private String uid;
    private String token;
    private String comment="";
    private Button request;
    public static Activity fa;
    private ImageView image;
    public Context mContext;
    private boolean timeup;
    private boolean no_q;
    CountDownTimer tim;
    private ImageLoader imageLoader;
    private ProgressBar progressBar;
    private AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.8F);
    private static final String TAG = Main.class.getName();
    private TextView textView;
    private TextView progress;
    private Handler handler;
    private Runnable r;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext=this;
        fa=this;

        request=(Button)findViewById(R.id.request);
        image=(ImageView)findViewById(R.id.image);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        filereader read = new filereader();

        token = read.readfile("token", getApplicationContext());
        uid = read.readfile("uid", getApplicationContext());

        TextView myTextView=(TextView)findViewById(R.id.swipetext);
        Typeface typeFace= Typeface.createFromAsset(getAssets(), "fonts/Roboto-Thin.ttf");
        myTextView.setTypeface(typeFace);

        String t=read.readfile("qsExpiresAt", getApplicationContext());
        if(read.readfile("qsid", getApplicationContext()).equals("")) no_q=true;
        else if(ExpiresIn(t)==0) {clean_qdetails();no_q=true;}
        else no_q=false;
        if(this.getIntent().getBooleanExtra("submit-qs",false)) if (isInternetOn()) if(!no_q) {
            submitdialog();
        } else {
            Toast.makeText(mContext, "Please check your internet connection and try again!", Toast.LENGTH_LONG).show();
        }
        textView=(TextView)findViewById(R.id.percent);
        progress=(TextView)findViewById(R.id.progress);

        if (t.equals(""));
        else{
            long test=ExpiresIn(t);
            tim = new CountDownTimer(test, 1000) {

                public void onTick(long millisUntilFinished) {
                    int secs = (int) (millisUntilFinished/ 1000);
                    int mins = secs / 60;
                    secs = secs % 60;
                    int hours =  mins / 60;
                    int days = hours / 24;
                    String res="";
                    if(days!=0) res=days+" days "+ hours%24 + " hours";
                    else if(hours!=0) res=hours+" hours";
                    else {
                        res = mins + ":" + secs;
                        if(secs<10)
                            res = mins + ":0" + secs;
                        if(mins<10)
                            res = "0"+res;
                    }
                    textView.setText(res);
                }

                public void onFinish() {
                    textView.setText("Time Up!!");
                    timeup=true;
                }
            }.start();
        }

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setMax(100);
        progressBar.setProgress(getProgress());
        setPadding();
        String qs_url = read.readfile("qsUrl", getApplicationContext());

        imageLoader=new ImageLoader(mContext);
        imageLoader.DisplayImage("http://appsdev.sarthy.in"+qs_url, image);

        if(no_q)
        {
            invisble();
        }
        else request.setBackgroundColor(Color.parseColor("#888888"));

        image.setOnTouchListener(new SwipeTouchListener(mContext) {

            public void onSwipeLeft() {
                if(!timeup&&!no_q){
                    Intent intent = new Intent(mContext, upload.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.enter_from_right, R.anim.exit_out_left);
                }else
                    Snackbar.make(findViewById(R.id.request), "Please request another question", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();

            }

            @Override
            public void onClick() {
                super.onClick();
                if(imageLoader.getLoaded()){
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.parse( "file://"+imageLoader.getLocation()), "image/*");
                    startActivity(intent);
                }
            }

            public boolean onTouch(View v, MotionEvent event) {

                return gestureDetector.onTouchEvent(event);
            }

        });
        View empty = findViewById(R.id.empty);
        empty.setOnTouchListener(new SwipeTouchListener(mContext) {

            public void onSwipeLeft() {
                if(!timeup&&!no_q){
                    Intent intent = new Intent(mContext, upload.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.enter_from_right, R.anim.exit_out_left);
                }else
                    Snackbar.make(findViewById(R.id.request), "Please request another question", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();

            }

            public boolean onTouch(View v, MotionEvent event) {

                return gestureDetector.onTouchEvent(event);
            }

        });
        request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(buttonClick);
                if (isInternetOn()) {
                    requestqs();
                } else {
                    Toast.makeText(mContext, "Please check your internet connection and try again!", Toast.LENGTH_LONG).show();
                }
            }
        });

    }
    @Override
    public void onResume(){
        super.onResume();
        progressBar.setProgress(getProgress());
        setPadding();
    }
    public void setPadding()
    {
        float padding_in_dp = (float)(getProgress()*2.8)+5;
        final float scale = getResources().getDisplayMetrics().density;
        int padding_in_px = (int) (padding_in_dp * scale + 0.5f);
        progress.setPadding(padding_in_px, 0, 0, 0);
        progress.setText(getProgress()+"% attempted");
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
    public void submitdialog(){

        LayoutInflater li = LayoutInflater.from(mContext);
        View promptsView = li.inflate(R.layout.prompts, null);
        final EditText userInput = (EditText) promptsView.findViewById(R.id.editTextDialogUserInput);

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
        builder.setView(promptsView);
        builder.setTitle("Submit Answer");
        builder.setMessage(R.string.submission_message);
        builder.setCancelable(false).setPositiveButton("Submit",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // get user input and set it to result
                        // edit text
                        comment = userInput.getText().toString();
                        finalsubmission();
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
    public void invisble(){
        progressBar.setVisibility(View.INVISIBLE);
        progress.setVisibility(View.INVISIBLE);
        textView.setVisibility(View.INVISIBLE);
        findViewById(R.id.swipetext).setVisibility(View.INVISIBLE);
        findViewById(R.id.timeleft).setVisibility(View.INVISIBLE);
    }
    public void visble(){
        progressBar.setVisibility(View.VISIBLE);
        progress.setVisibility(View.VISIBLE);
        textView.setVisibility(View.VISIBLE);
        findViewById(R.id.swipetext).setVisibility(View.VISIBLE);
        findViewById(R.id.timeleft).setVisibility(View.VISIBLE);
    }

    public void callApi() {
        callApi task = new callApi();
        String url = getApiEndpoint() + "/profile";
        long unixtime=System.currentTimeMillis() / 1000L;
        String payload="/solmate/profile"+unixtime;
        String sign = signature.hmacDigest(payload);
        filereader read=new filereader();
        task.execute(url, getapikey(), String.valueOf(unixtime), sign, read.readfile("token", Main.this), read.readfile("uid", Main.this));
    }
    class callApi extends AsyncTask<String, Void, String> {
        ProgressDialog progDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progDialog = new ProgressDialog(Main.this);
            progDialog.setMessage("loading...");
            progDialog.setIndeterminate(false);
            progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progDialog.setCancelable(true);
            progDialog.setOnCancelListener(new DialogInterface.OnCancelListener(){
                @Override
                public void onCancel(DialogInterface dialog) {
                    callApi.this.cancel(true);
                    Toast.makeText(mContext,"Request Cancelled",Toast.LENGTH_LONG).show();

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
//                Toast.makeText(profile.this, "Please check your internet connection and try again!"+e.getMessage(), Toast.LENGTH_LONG).show();
//                cancel(true);
//            }
            return null;
        }
        public String getString(JSONObject j,String key)
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
                        Toast.makeText(Main.this, "Something went wrong", Toast.LENGTH_LONG).show();
                    } else {
                        try {
                            progDialog.dismiss();
                        } catch (Exception e) {
                        }
                        Intent intent = new Intent(mContext, profile.class);
                        intent.putExtra("json",result);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_up,R.anim.slide_out_up);
                    }
                } catch (Exception e) {
                }
            }else {
                try {
                    progDialog.dismiss();
                } catch (Exception e) {
                }
                Toast.makeText(Main.this, " Please check your internet connection and try again!", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void requestqs() {
        requestqs task = new requestqs();
        String url = getApiEndpoint() + "/request-qs";
        long unixtime=System.currentTimeMillis() / 1000L;
        String payload="/solmate/request-qs"+unixtime;
        String sign = signature.hmacDigest(payload);
        task.execute(url, getapikey(), String.valueOf(unixtime), sign, uid, token);
    }
    class requestqs extends AsyncTask<String, Void, String> {
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
            post_params.add(new BasicNameValuePair("uid", params[4]));
            post_params.add(new BasicNameValuePair("token", params[5]));
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
                try{
                    Request_result details= (Request_result) MyCustomFeedManager.getMappedModel(result, Request_result.class);

                    JSONObject jsonObject = new JSONObject(result);
                    try {
                        progDialog.dismiss();
                    } catch (Exception e) {
                    }
                    if (!jsonObject.getBoolean("success")) {
                        String error="";
                        String type=jsonObject.getString("type");
                        if(type.equals("noQuestionSetFound")) error="Out of questions it seems!";
                        if(type.equals("alreadyAlloted")) error="Please first do the allotted Set";
                        if (type.equals("auth")) cleandetails();
                        Snackbar.make(findViewById(R.id.request), error, Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }

                    else {
                        try{
                            imageLoader.imageclear();
                        }catch (Exception e){}

                        imageLoader=new ImageLoader(mContext);
                        imageLoader.DisplayImage("http://appsdev.sarthy.in"+details.getQsUrl(), image);

                        FileOutputStream outputStream4;
                        FileOutputStream outputStream5;
                        FileOutputStream outputStream6;
                        FileOutputStream outputStream7;
                        FileOutputStream outputStream8;
                        FileOutputStream outputStream9;
                        FileOutputStream outputStream10;
                        FileOutputStream outputStream11;
                        try {
                            timeup=false;
                            no_q=false;
                            outputStream4 = openFileOutput("qsid", Context.MODE_PRIVATE);
                            outputStream5 = openFileOutput("qsUrl", Context.MODE_PRIVATE);
                            outputStream6 = openFileOutput("questions", Context.MODE_PRIVATE);
                            outputStream9 = openFileOutput("questions_status", Context.MODE_PRIVATE);
                            outputStream10 = openFileOutput("topics", Context.MODE_PRIVATE);
                            outputStream11 = openFileOutput("levels", Context.MODE_PRIVATE);
                            outputStream7 = openFileOutput("qsAllottedAt", Context.MODE_PRIVATE);
                            outputStream8 = openFileOutput("qsExpiresAt", Context.MODE_PRIVATE);
                            outputStream4.write(details.getQsid().getBytes());
                            outputStream5.write(details.getQsUrl().getBytes());
                            String[] qs = details.getQuestions();
                            String qss = "",bits="",bits_="";
                            if(qs.length>0)
                            {
                                for (int i = 0; i < qs.length - 1; i++) {
                                    qss += qs[i];
                                    qss += ";";
                                    bits += "0";
                                    bits_ += "0;";
                                }

                                qss += qs[qs.length - 1];
                                bits += "0";
                                bits_ += "0";
                            }
                            outputStream6.write(qss.getBytes());
                            outputStream9.write(bits.getBytes());
                            outputStream10.write(bits_.getBytes());
                            outputStream11.write(bits.getBytes());
                            outputStream7.write(details.getAllottedAt().getBytes());
                            outputStream8.write(details.getExpiresAt().getBytes());
                            outputStream4.close();
                            outputStream5.close();
                            outputStream6.close();
                            outputStream7.close();
                            outputStream8.close();
                            outputStream9.close();
                            visble();
                            progressBar.setProgress(getProgress());
                            setPadding();
                            request.setBackgroundColor(Color.parseColor("#888888"));
                            long test = ExpiresIn(details.getExpiresAt());
                            try{tim.cancel();}catch (Exception e){}
                            tim = new CountDownTimer(test, 1000) {

                                public void onTick(long millisUntilFinished) {
                                    int secs = (int) (millisUntilFinished/ 1000);
                                    int mins = secs / 60;
                                    secs = secs % 60;
                                    int hours =  mins / 60;
                                    int days = hours / 24;
                                    String res="";
                                    if(days!=0) res=days+" days "+ hours%24 + " hours";
                                    else if(hours!=0) res=hours+" hours";
                                    else {
                                        res = mins + ":" + secs;
                                        if(secs<10)
                                            res = mins + ":0" + secs;
                                        if(mins<10)
                                            res = "0"+res;
                                    }
                                    textView.setText(res);
                                }

                                public void onFinish() {
                                    textView.setText("Time Up!!");
                                    timeup=true;
                                }
                            }.start();
                        } catch (Exception e) {
                            Log.e(TAG, "File not found: " + e.toString());
                        }
                    }}catch (Exception e) {
                    Toast.makeText(mContext, result+" Please check your internet connection and try again!", Toast.LENGTH_LONG).show();
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

    public void finalsubmission() {
        finalsubmission task = new finalsubmission();
        String url = getApiEndpoint() + "/submit-qs";
        long unixtime=System.currentTimeMillis() / 1000L;
        String payload="/solmate/submit-qs"+unixtime;
        String sign = signature.hmacDigest(payload);

        filereader read=new filereader();
        String questions_status =read.readfile("questions_status",getApplicationContext());
        String answer_urls = read.readfile("answer_urls", getApplicationContext());

        task.execute(url, getapikey(), String.valueOf(unixtime), sign, uid, token, comment, answer_urls, questions_status);
//        String[] solved= solved(questions_status);

    }

    class finalsubmission extends AsyncTask<String, Void, String> {
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

            String[] answers;
            String[] topics = topics();
            String[] levels = levels();
            ArrayList<ArrayList<Integer>> image_index = image_index(levels.length);
            if(params[7].length()>0)
                answers = params[7].substring(0, params[7].length() - 1).split(";");
            else answers=new String[]{""};
            boolean[] solved= solved(params[8]);

//            List<NameValuePair> post_params = new ArrayList<NameValuePair>();
//            post_params.add(new BasicNameValuePair("apikey", params[1]));
//            post_params.add(new BasicNameValuePair("timestamp", params[2]));
//            post_params.add(new BasicNameValuePair("signature", params[3]));
//            post_params.add(new BasicNameValuePair("uid", params[4]));
//            post_params.add(new BasicNameValuePair("token", params[5]));
//            post_params.add(new BasicNameValuePair("comment", params[6]));
//            for(String value: answers){
//                post_params.add(new BasicNameValuePair("images",value));
//            }
//            for(String value: solved){
//                post_params.add(new BasicNameValuePair("answers",value));
//            }
            JSONObject jsonObject = new JSONObject();
            try {
                HttpResponse response;
                jsonObject.accumulate("apikey", params[1]);
                jsonObject.accumulate("timestamp", params[2]);
                jsonObject.accumulate("signature", params[3]);
                jsonObject.accumulate("uid", params[4]);
                jsonObject.accumulate("token", params[5]);
                jsonObject.accumulate("comment", params[6]);
                JSONArray images_json = new JSONArray();
                for(String value: answers){
                    images_json.put(value);
                }
                jsonObject.accumulate("images",images_json);
                JSONArray answers_json = new JSONArray();
                int i=0;
                for(Boolean value: solved){
                    JSONObject ans_json = new JSONObject();
                    JSONArray indexes = new JSONArray();
                    for(int in: image_index.get(i)) {
                        indexes.put(in);
                    }
                    ans_json.accumulate("submitted", value);
                    if (value) {
                        ans_json.accumulate("images", indexes);
                        ans_json.accumulate("topic", topics[i]);
                        ans_json.accumulate("level", levels[i]);
                    }

                    i++;
                    answers_json.put(ans_json);
                }
                jsonObject.accumulate("answers",answers_json);
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
                Log.i("requestqs", " " + sresponse+ " "+json);
                return sresponse;
            }
            catch (Exception e)
            {
                Log.e("error", e.getLocalizedMessage());
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
                    progDialog.dismiss();
                } catch (Exception e) {
                }
                Boolean success=false;
                String type="";
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    success = jsonObject.getBoolean("success");
                    type = jsonObject.getString("type");
                }catch (Exception e){}
                if (!success) {
                    if(type.equals("auth")) cleandetails();
                    Toast.makeText(mContext, result+"Something went wrong", Toast.LENGTH_LONG).show();
                }

                else {
                    clean_qdetails();
                    no_q=true;
                    request.setBackgroundColor(Color.parseColor("#0077B8"));
                    imageLoader.imageclear();
                    image.setImageResource(R.drawable.stub);
                    invisble();
                }
            }else {
                try {
                    progDialog.dismiss();
                } catch (Exception e) {
                }
                Toast.makeText(mContext," Please check your internet connection and try again!", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        try {
            imageLoader.imageclear();
        }
        catch (Exception e){}
    }
    String[] spinnerArray2= new String[]{"hint","Probability","Definite Integration","Circles","Vectors","Theory of Equations","Sequences and Series","Complex Numbers","Three Dimensional Geometry","Matrices Determinants","Sets, Relations and Functions","Differential Coefficient","Application of Derivatives","Trigonometric Ratios and Identities", "Parabola", "Limits", "Heights and Distances","Straight Lines","Permutation and Combination","Continuity and Differentiability","Differential Equation", "Inverse Trigonometry", "Area","Indefinite Integration","Hyperbola", "Trigonometric Equations", "Ellipse", "Binomial Theorem", "Mathematical Induction", "Properties of Triangles", "Pairs of Straight Lines","Exponentials and Logarithms", "Dynamics", "Statistics"};
    public String[] topics()
    {
        filereader read = new filereader();
        String topics_raw = read.readfile("topics", mContext);
        Log.i("topics",topics_raw);
        String[] res = topics_raw.split(";");
        for (int i=0;i<res.length;i++)
            try {
                res[i]=spinnerArray2[Integer.valueOf(res[i])+1];
            } catch (Exception e) {
                res[i] = "";
                e.printStackTrace();
            }
        return res;
    }
    public String[] levels()
    {
        filereader read = new filereader();
        String levels_raw = read.readfile("levels", mContext);
        Log.i("levels",levels_raw);
        String[] res = new String[levels_raw.length()];
        for (int i=0;i<res.length;i++)
            if(levels_raw.charAt(i)=='1')
            res[i]="easy";
            else if(levels_raw.charAt(i)=='2')
            res[i]="medium";
            else
            res[i]="hard";
        return res;
    }
    public ArrayList<ArrayList<Integer>> image_index(int length)
    {
        filereader read = new filereader();
        String image_paths = read.readfile("image_paths", mContext);
        SolDetails[] sol = parse_soldetails(image_paths);
        ArrayList<ArrayList<Integer>> res = new ArrayList<>();
        for (int i=0;i<length;i++)
        {
            ArrayList<Integer> a = new ArrayList<>();
            for(int j=0;j<sol.length;j++)
            if(Arrays.asList(sol[j].questions).contains(i+"")||Arrays.asList(sol[j].questions_partial).contains(i+""))
                a.add(j);
            res.add(a);
        }
            return res;
    }
    public class SolDetails
    {
        String filepath="";
        String[] questions;
        String[] questions_partial;
    }
    public SolDetails[] parse_soldetails(String images)
    {
        images = images.substring(0,images.length()-1);
        String[] details = images.split(";");
        SolDetails[] sol = new SolDetails[details.length];
        for(int i=0;i<details.length;i++)
        {
            sol[i]=new SolDetails();
            try {
                sol[i].questions_partial = details[i].substring(details[i].lastIndexOf('/') + 1).split(",");
//                if(sol[i].questions_partial[0].equals("")) return null;
            }
            catch (Exception e)
            {
                sol[i].questions_partial = new String[]{""};
            }
            details[i] = details[i].substring(0, details[i].lastIndexOf('/'));
            try{
                sol[i].questions = details[i].substring(details[i].lastIndexOf('/')+1).split(",");
//                if(sol[i].questions[0].equals("")) return null;
            }
            catch (Exception e)
            {
                sol[i].questions = new String[]{""};
            }
            sol[i].filepath = details[i].substring(0,details[i].lastIndexOf('/'));
        }
        return sol;
    }
    public void clean_qdetails() {

        FileOutputStream outputStream4;
        FileOutputStream outputStream5;
        FileOutputStream outputStream6;
        FileOutputStream outputStream7;
        FileOutputStream outputStream8;
        FileOutputStream outputStream9;
        FileOutputStream outputStream10;
        FileOutputStream outputStream11;
        FileOutputStream outputStream12;
        FileOutputStream outputStream13;

        try {

            outputStream4 = openFileOutput("qsid", Context.MODE_PRIVATE);
            outputStream5 = openFileOutput("qsUrl", Context.MODE_PRIVATE);
            outputStream6 = openFileOutput("questions", Context.MODE_PRIVATE);
            outputStream9 = openFileOutput("questions_status", Context.MODE_PRIVATE);
            outputStream11 = openFileOutput("image_paths", Context.MODE_PRIVATE);
            outputStream12 = openFileOutput("levels", Context.MODE_PRIVATE);
            outputStream13 = openFileOutput("topics", Context.MODE_PRIVATE);
            outputStream7 = openFileOutput("qsAllottedAt", Context.MODE_PRIVATE);
            outputStream8 = openFileOutput("qsExpiresAt", Context.MODE_PRIVATE);
            outputStream10 = openFileOutput("answer_urls", Context.MODE_PRIVATE);
            outputStream4.write("".getBytes());
            outputStream5.write("".getBytes());
            outputStream6.write("".getBytes());
            outputStream7.write("".getBytes());
            outputStream8.write("".getBytes());
            outputStream9.write("".getBytes());
            outputStream10.write("".getBytes());
            outputStream11.write("".getBytes());
            outputStream12.write("".getBytes());
            outputStream13.write("".getBytes());
            outputStream4.close();
            outputStream5.close();
            outputStream6.close();
            outputStream7.close();
            outputStream8.close();
            outputStream9.close();
            outputStream10.close();
            outputStream11.close();
            outputStream12.close();
            outputStream13.close();
        } catch (Exception e) {
            Log.e(TAG, "File not found: " + e.toString());
        }
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
            Log.e(TAG, "File not found: " + e.toString());
        }
        Intent intent = new Intent(mContext, OTP.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
        finish();
    }

    public int getProgress(){
        filereader read= new filereader();
        String questions_status=read.readfile("questions_status",getApplicationContext());
        if(questions_status.length()==0) return 0;
        int total=0,left=0;
        for(;total<questions_status.length();total++) if(questions_status.charAt(total)=='1') left++;
        int percentage=(left)*100/total;
        return percentage;
    }

    public long ExpiresIn(String temp){
        long res=0;
        if(temp.equals("")) return 0;
        temp=temp.replace("T"," ");
        temp=temp.substring(0, temp.length() - 1);
        try{
            res = 55*6*60*1000+Timestamp.valueOf(temp).getTime() - System.currentTimeMillis();
        }catch (Exception e){}
        if(res<0) return 0;
        return res;
    }

    public boolean [] solved(String status ){
        boolean[] result=new boolean[status.length()];
        for(int i=0; i<status.length();i++){
            if(status.charAt(i)=='1') result[i]=true;
            else result[i]=false;
        }
        return result;
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
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.MyProfile) {
            callApi();
        }

        return super.onOptionsItemSelected(item);
    }
}
