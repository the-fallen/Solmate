package in.sarthy.solmate;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeoutException;

public class upload extends AppCompatActivity {
    private static final int PICK_IMAGE = 1;
    private ImageView[] imgView;
    private Button[] button;
    private TextView[] quesdis;
    private Button submit;
    private Bitmap bitmap;
    private LinearLayout listView;
    private LinearLayout listView2;
    private String items_selected;
    private String levels;
    private String[] topics;
    private String new_items_selected="";
    private Context mContext;
    private String filepath;
    private String[] list;
    private AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.8F);
    private CheckBox dontShowAgain;
    private boolean all_done;
    private boolean onCreate_finished=false;
    private boolean no_question;
    private SolDetails[] sol;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        mContext=this;

        submit = (Button) findViewById(R.id.submit_solutions);
        listView = (LinearLayout) findViewById(R.id.list);
        listView2 = (LinearLayout) findViewById(R.id.list2);

        TextView attach_text=(TextView)findViewById(R.id.attach_text);
        TextView ques_text=(TextView)findViewById(R.id.ques_text);
        Typeface typeFace= Typeface.createFromAsset(getAssets(), "fonts/Roboto-Thin.ttf");
        attach_text.setTypeface(typeFace);
        ques_text.setTypeface(typeFace);

        int padding_in_dp = 15;
        final float scale = getResources().getDisplayMetrics().density;
        int padding_in_px = (int) (padding_in_dp*1.5 * scale + 0.5f);
        int padding_in_px2 = (int) (padding_in_dp * scale + 0.5f);

        final Drawable check = getScaledDrawable(padding_in_px,padding_in_px,R.drawable.ic_check_box_outline_blank_black_24dp);
        final Drawable checked = getScaledDrawable(padding_in_px,padding_in_px,R.drawable.ic_check_box_black_24dp);
        final Drawable partial = getScaledDrawable(padding_in_px,padding_in_px,R.drawable.ic_indeterminate_check_box_black_24dp);
        final Drawable partial_tick = getScaledDrawable(padding_in_px2,padding_in_px2,R.drawable.ic_done_black_24dp);
        final Drawable completed = getScaledDrawable(padding_in_px2,padding_in_px2,R.drawable.ic_done_all_black_24dp);

        filereader read=new filereader();
        String status=read.readfile("questions_status", getApplicationContext());
        levels=read.readfile("levels", getApplicationContext());
        topics=read.readfile("topics", getApplicationContext()).split(";");

        if(levels.equals("")||topics.equals(""))
        {
            String bits="",bits_="";
            if(status.length()>0)
            {
                for (int i = 0; i < status.length() - 1; i++) {
                    bits += "0";
                    bits_ += "0;";
                }
                bits += "0";
                bits_ += "0";
                levels = bits;
                topics = bits_.split(";");
            }
        }

        if(read.readfile("upload_token",getApplicationContext()).equals("")) {
            instructions();
        }
        if (!status.contains("0")&&!status.contains("2")) {
            all_done = true;
        }
        if(read.readfile("image_paths",getApplicationContext()).equals("")) {
        }
        else sol = parse_soldetails(read.readfile("image_paths", getApplicationContext()));
        String questions = read.readfile("questions", getApplicationContext());
        list = questions.split(";");
        if(questions.equals("")) no_question=true;
        items_selected = read.readfile("questions_status", getApplicationContext());
        for(int i=0;i<items_selected.length();i++) new_items_selected+="0";
        // Instantiating array adapter to populate the listView
        // The layout android.R.layout.simple_list_item_single_choice creates radio button for each listview item
        if(!no_question)for (int i = 0; i < list.length; i++) {
            final CheckedTextView v = new CheckedTextView(getApplicationContext());
            if(i!=0){    v.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            }
            else
            {
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                v.setLayoutParams(lp);
            }
            v.setTag(i);
            v.setText(list[i]);
            v.setGravity(Gravity.CENTER_VERTICAL);
            v.setBackgroundDrawable(getResources().getDrawable(R.drawable.back_checks));
            if (items_selected.charAt(i)!='1') {
                if(items_selected.charAt(i)=='2') v.setCompoundDrawablesWithIntrinsicBounds(null,null,partial_tick,null);
                v.setTextColor(Color.parseColor("#000000"));
                v.setCheckMarkDrawable(check);
            } else {
                v.setTextColor(Color.parseColor("#888888"));
                v.setCheckMarkDrawable(completed);
            }
            TypedValue value = new TypedValue();
            // you'll probably want to use your activity as context here:
            mContext.getTheme().resolveAttribute(android.R.attr.listChoiceIndicatorMultiple, value, true);
            // now set the resolved check mark resource id:
            v.setTextSize(2, 20);
            v.setTypeface(typeFace);
            v.setPadding(15, 15, 15, 15);
            if (items_selected.charAt(i)=='1') v.setOnTouchListener(new SwipeTouchListener(upload.this) {

                public void onSwipeRight() {
                    finish();
                    overridePendingTransition(R.anim.enter_from_left, R.anim.exit_out_right);
                }

                public boolean onTouch(View v, MotionEvent event) {
                    return gestureDetector.onTouchEvent(event);
                }

            });
            else v.setOnTouchListener(new SwipeTouchListener(upload.this) {

                public void onSwipeRight() {
                    finish();
                    overridePendingTransition(R.anim.enter_from_left, R.anim.exit_out_right);
                }

                @Override
                public void onClick() {
                    super.onClick();
                    int i=((int) v.getTag());
                    filereader read = new filereader();
                    if (new_items_selected.charAt(i)=='0') {
                        v.setCheckMarkDrawable(checked);
                        items_selected = items_selected.substring(0,i)+"1"+items_selected.substring(i+1);
                        new_items_selected = new_items_selected.substring(0,i)+"1"+new_items_selected.substring(i+1);
                    }
                    else if (new_items_selected.charAt(i)=='1') {
                        v.setCheckMarkDrawable(partial);
                        items_selected = items_selected.substring(0,i)+"2"+items_selected.substring(i+1);
                        new_items_selected = new_items_selected.substring(0,i)+"2"+new_items_selected.substring(i+1);
                    }
                    else if (new_items_selected.charAt(i)=='2') {
                        v.setCheckMarkDrawable(check);
                        items_selected = items_selected.substring(0,i)+"0"+items_selected.substring(i+1);
                        new_items_selected = new_items_selected.substring(0,i)+"0"+new_items_selected.substring(i+1);
                    }

                }

                public boolean onTouch(View v, MotionEvent event) {
                    return gestureDetector.onTouchEvent(event);
                }

            });

            listView.addView(v);
        }
        float totalHeight = 50 + list.length*45 + 170;
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float density = getResources().getDisplayMetrics().density;
        totalHeight *= density;
        totalHeight += height();
        if (outMetrics.heightPixels > totalHeight) {
            View v = new View(getApplicationContext());
            v.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    (int) (outMetrics.heightPixels - totalHeight)));
            listView.addView(v);

            v.setOnTouchListener(new SwipeTouchListener(upload.this) {

                public void onSwipeRight() {
                    finish();
                    overridePendingTransition(R.anim.enter_from_left, R.anim.exit_out_right);
                }

                public boolean onTouch(View v, MotionEvent event) {
                    return gestureDetector.onTouchEvent(event);
                }

            });
        }
        for (int i = 0; i < list.length; i++)
        {
            LinearLayout linearLayout = new LinearLayout(mContext);
            linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);

            final Spinner v = new Spinner(getApplicationContext());
            String[] spinnerArray= new String[]{"Difficulty select","easy","med","hard"};
            ArrayAdapter<String> spinnerArrayAdapter = new MySpinnerAdapter(this, R.layout.simple_spinner_item, spinnerArray); //selected item will look like a spinner set from XML
            spinnerArrayAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
            v.setAdapter(spinnerArrayAdapter);
            v.setTag(i);
            v.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
            v.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
//                    Log.i("adapter",position+"");
                    int i = (int) v.getTag();
                    if (position == 0) {
                        if ((((int) levels.charAt(i)) - 48)!=0) {
                            v.setSelection((((int) levels.charAt(i)) - 48),false);
                            return;
                        }
                        ((TextView) selectedItemView).setTextColor(Color.parseColor("#898989"));
                    } else {
                        levels = levels.substring(0, i) + position + "" + levels.substring(i + 1);
                        Log.i("levels", levels);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                    // your code here
                }

            });
            v.setOnTouchListener(new SwipeTouchListener(upload.this) {

                public void onSwipeRight() {
                    finish();
                    overridePendingTransition(R.anim.enter_from_left, R.anim.exit_out_right);
                }

                @Override
                public void onClick() {
                    super.onClick();
                    v.performClick();
                }
            });
//            v.setSelection(1);
            v.setSelection(((int) levels.charAt(i)) - 48);
            v.setBackgroundColor(Color.parseColor("#00efefef"));
//            v.setHint("Select shit");
//            v.setHintColor(Color.parseColor("#898989"));
//            v.setTextSize(2, 20);
//            v.setTypeface(typeFace);
            v.setGravity(Gravity.CENTER_HORIZONTAL);
            v.setPadding(15, 15, 15, 15);
            final Spinner v2 = new Spinner(getApplicationContext());

            String[] spinnerArray2= new String[]{"Select topic","Probability","Definite Integration","Circles","Vectors","Theory of Equations","Sequences and Series","Complex Numbers","Three Dimensional Geometry","Matrices Determinants","Sets, Relations and Functions","Differential Coefficient","Application of Derivatives","Trigonometric Ratios and Identities", "Parabola", "Limits", "Heights and Distances","Straight Lines","Permutation and Combination","Continuity and Differentiability","Differential Equation", "Inverse Trigonometry", "Area","Indefinite Integration","Hyperbola", "Trigonometric Equations", "Ellipse", "Binomial Theorem", "Mathematical Induction", "Properties of Triangles", "Pairs of Straight Lines","Exponentials and Logarithms", "Dynamics", "Statistics"};
            ArrayAdapter<String> spinnerArrayAdapter2 = new MySpinnerAdapter(this, R.layout.simple_spinner_item, spinnerArray2); //selected item will look like a spinner set from XML
            spinnerArrayAdapter2.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
            v2.setAdapter(spinnerArrayAdapter2);
            v2.setTag(i);
//            v2.setBackgroundColor(Color.parseColor("#00efefef"));
            v2.setBackground(getDrawable(R.drawable.spinner_bg));
            v2.setLayoutParams(new LinearLayout.LayoutParams(
                    0,
                    ViewGroup.LayoutParams.WRAP_CONTENT, 2f));
            v2.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
//                    Log.i("adapter",position+"");
                    if (position == 0) {
                        if (!topics[(int) v2.getTag()].equals("0")) {
                            v2.setSelection(Integer.valueOf(topics[(int) v2.getTag()]),false);
                            return;
                        }
                        ((TextView) selectedItemView).setTextColor(Color.parseColor("#898989"));
                    } else {
                        topics[(int) v2.getTag()] = position + "";
                        Log.i("v2", Arrays.asList(topics).toString());
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                    // your code here
                }

            });
            v2.setOnTouchListener(new SwipeTouchListener(upload.this) {

                public void onSwipeRight() {
                    finish();
                    overridePendingTransition(R.anim.enter_from_left, R.anim.exit_out_right);
                }

                @Override
                public void onClick() {
                    super.onClick();
                    v2.performClick();
//                    Log.i("click","yes");
                }
            });
            v2.setSelection(Integer.valueOf(topics[i]));
            v2.setBackgroundColor(Color.parseColor("#00ffffff"));
//            v.setHint("Select shit");
//            v.setHintColor(Color.parseColor("#898989"));
//            v.setTextSize(2, 20);
//            v.setTypeface(typeFace);
            v2.setGravity(Gravity.CENTER_HORIZONTAL);
            v2.setPadding(0, 15, 15, 15);
            linearLayout.addView(v);
            linearLayout.addView(v2);
            listView2.addView(linearLayout);
        }
        if(!getProgress())submit.setBackgroundColor(Color.parseColor("#888888"));
        submit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (getProgress()) {
                    if (check_topics_levels()) {
                        v.startAnimation(buttonClick);
                        Main.fa.finish();
                        Intent intent = new Intent(mContext, Main.class);
                        intent.putExtra("submit-qs", true);
                        startActivity(intent);
                        finish();
                        overridePendingTransition(R.anim.enter_from_left, R.anim.exit_out_right);
                    } else {
                        Snackbar.make(v, "Choose difficulty levels and topics of the submitted questions before submitting!" , Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                } else {
                    Snackbar.make(v, "You need to attempt a minimum 60% of the set to submit your response", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        });
        showimages();
    }

    public boolean check_topics_levels()
    {
        for(int i=0;i<items_selected.length();i++)
            if(items_selected.charAt(i)=='1') if(topics[i].equals("0")||levels.charAt(i)=='0') return false;
        return true;
    }

    public int dptopx(int padding_in_dp)
    {
        final float scale = getResources().getDisplayMetrics().density;
        return  (int) (padding_in_dp * scale + 0.5f);

    }
    @Override
    public void onStart()
    {
        super.onStart();
        onCreate_finished = true;
    }
    public void save_states()
    {
        try {
            FileOutputStream outputStream;
            FileOutputStream outputStream2;
            outputStream = openFileOutput("levels",Context.MODE_PRIVATE);
            outputStream2 = openFileOutput("topics",Context.MODE_PRIVATE);
            outputStream.write(levels.getBytes());
            String qss = "";
            for(int i=0;i<topics.length-1;i++)
            {qss+=topics[i];qss+=";";}
            qss+=topics[topics.length-1];
            outputStream2.write(qss.getBytes());
            outputStream.close();
            outputStream2.close();
            Log.i("destroy",levels+qss);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onDestroy()
    {
        save_states();
        super.onDestroy();
    }
    private static class MySpinnerAdapter extends ArrayAdapter<String> {
        // Initialise custom font, for example:
        Typeface typeFace= Typeface.createFromAsset(getContext().getAssets(), "fonts/Roboto-Thin.ttf");

        // (In reality I used a manager which caches the Typeface objects)
        // Typeface font = FontManager.getInstance().getFont(getContext(), BLAMBOT);

        private MySpinnerAdapter(Context context, int resource, String[] items) {
            super(context, resource, items);
        }

        // Affects default (closed) state of the spinner
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView view = (TextView) super.getView(position, convertView, parent);
            view.setTypeface(typeFace);
            return view;
        }

        // Affects opened state of the spinner
        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            TextView view = (TextView) super.getDropDownView(position, convertView, parent);
            view.setTypeface(typeFace);
//            if(view.getText().toString().equals("Select topic")||view.getText().toString().equals("Difficulty select")) view.setTextColor(Color.parseColor("#898989"));
            return view;
        }

    }
    public void showimages(){
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics ();
        display.getMetrics(outMetrics);

        float density  = getResources().getDisplayMetrics().density;
        float dpWidth  = outMetrics.widthPixels / density;

        int dimension = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpWidth/3-8, getResources().getDisplayMetrics());
        int pad= (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 6, getResources().getDisplayMetrics());

        LinearLayout imagelayout= (LinearLayout)findViewById(R.id.imageLayout);
        LinearLayout buttonlayout= (LinearLayout)findViewById(R.id.buttonLayout);
        LinearLayout textlayout= (LinearLayout)findViewById(R.id.addLayout);
        LinearLayout questionlayout= (LinearLayout)findViewById(R.id.questionLayout);

        int len=0;
        try {len=sol.length;}catch (Exception e){}

        imgView=new ImageView[len+1];
        button= new Button[len+1];
        quesdis= new TextView[len+1];
        int top=0;
        int left=-pad;
        for (int i=0;i<len+1;i++){
            final int j=i;
            final int x=len;

            //sets parameters
            LinearLayout.LayoutParams laypa =new LinearLayout.LayoutParams(dimension,dimension);
            laypa.setMargins(-left, top, 0, 0);
            LinearLayout.LayoutParams laypa2 =new LinearLayout.LayoutParams(dimension/3,dimension/3);
            laypa2.setMargins(-left+2*dimension/3, top+2*dimension/3, 0, 0);
            LinearLayout.LayoutParams laypa3 =new LinearLayout.LayoutParams(dimension,dimension);
            laypa3.setMargins(-left, top, 0, 0);

            //logic of the views
            if((i+1)%3==0){top+=dimension+pad;left+=3*(dimension+pad);}
            else if(i%3==0&& i!=0){left-=3*(dimension+pad);}

            //add image text
            if(i==0){
                Typeface typeFace= Typeface.createFromAsset(getAssets(), "fonts/Roboto-Thin.ttf");

                TextView text= new TextView(mContext);
                text.setLayoutParams(laypa);
                text.setText("Add image");
                text.setTypeface(typeFace);
                text.setGravity(Gravity.CENTER);
                text.setPadding(5,0,5,10);
                text.setTextSize(TypedValue.COMPLEX_UNIT_SP, 28);
                text.setTextColor(Color.parseColor("#2196F3"));
                textlayout.addView(text);
            }

            //questions
            String ques="";
            int ql=0;
            try{ql=sol[x-j].questions.length;}catch (Exception e){}
            for(int k=0;k<ql;k++){
                try{ques+=list[Integer.parseInt(sol[x-j].questions[k])]+",";}
                catch (Exception e){}
            }
            int qlp=0;
            try{qlp=sol[x-j].questions_partial.length;}catch (Exception e){}
            for(int k=0;k<qlp;k++){
                try{ques+=list[Integer.parseInt(sol[x-j].questions_partial[k])]+",";}
                catch (Exception e){}
            }
            try{
            if(ques.substring(0,1).equals(","))ques=ques.substring(1);
            }catch (Exception e){}
            try{
                if(ques.substring(ques.length()-1).equals(","))ques=ques.substring(0,ques.length()-1);
            }catch (Exception e){}

            quesdis[i]=new TextView(mContext);
            quesdis[i].setLayoutParams(laypa3);
            quesdis[i].setText(ques);
            quesdis[i].setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            quesdis[i].setTextColor(Color.parseColor("#ffffff"));
            quesdis[i].setGravity(Gravity.CENTER);
            questionlayout.addView(quesdis[i]);

            //button
            button[i]= new Button(mContext);
            button[i].setLayoutParams(laypa2);
            if(i==0)button[0].setBackgroundColor(Color.TRANSPARENT);
            else button[i].setBackgroundResource(R.drawable.remove_red);
            if(i!=0)button[i].setOnTouchListener(new SwipeTouchListener(upload.this) {

                public void onSwipeRight() {
                    finish();
                    overridePendingTransition(R.anim.enter_from_left, R.anim.exit_out_right);
                }

                public void onClick() {
                    button[j].startAnimation(buttonClick);
                    remove_dialog(x-j);
                }

                public boolean onTouch(View v, MotionEvent event) {
                    return gestureDetector.onTouchEvent(event);
                }

            });
            else button[0].setOnTouchListener(new SwipeTouchListener(upload.this) {

                public void onSwipeRight() {
                    finish();
                    overridePendingTransition(R.anim.enter_from_left, R.anim.exit_out_right);
                }

                public void onClick() {
                    imgView[0].startAnimation(buttonClick);
                    if (!all_done) {
                        if (anyselected()) {

                            try {
                                Intent intent = new Intent();
                                intent.setType("image/*");
                                intent.setAction(Intent.ACTION_PICK);
                                startActivityForResult(
                                        Intent.createChooser(intent, "Select Picture"),
                                        PICK_IMAGE);
                            } catch (Exception e) {
                                Log.e(e.getClass().getName(), e.getMessage(), e);
                            }
                        } else {
                            Snackbar.make(findViewById(R.id.submit_solutions),
                                    "Please select questions which you will be answering in the image", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }
                    } else {
                        Snackbar.make(findViewById(R.id.submit_solutions), "You have attempted all the questions!", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                }

                public boolean onTouch(View v, MotionEvent event) {
                    return gestureDetector.onTouchEvent(event);
                }

            });
            buttonlayout.addView(button[i]);

            //Image declarations
            imgView[i] =new ImageView(mContext);
            imgView[i].setLayoutParams(laypa);
            imgView[i].setBackgroundDrawable(getResources().getDrawable(R.drawable.background));
            imgView[i].setColorFilter(Color.argb(125, 0, 0, 0));
            imgView[i].setId(i);
            if(i!=0)
                try{Bitmap bit=decodesmall(Uri.fromFile(new File(sol[len - i].filepath)));
                    if(bit!=null)
                        imgView[i].setImageBitmap(bit);
                    else
                        imgView[i].setImageResource(R.drawable.notfound);
                }catch (Exception e){imgView[i].setBackgroundResource(R.drawable.notfound);}
            imagelayout.addView(imgView[i]);
            if(i==0)
            {
//                imgView[0].setImageResource(R.drawable.ic_note_add_black_48dp);
                imgView[0].setOnTouchListener(new SwipeTouchListener(upload.this) {

                    public void onSwipeRight() {
                        finish();
                        overridePendingTransition(R.anim.enter_from_left, R.anim.exit_out_right);
                    }

                    public void onClick() {
                        imgView[0].startAnimation(buttonClick);
                        if(!all_done){
                            if(anyselected()){

                                try {
                                    Intent intent = new Intent();
                                    intent.setType("image/*");
                                    intent.setAction(Intent.ACTION_PICK);
                                    startActivityForResult(
                                            Intent.createChooser(intent, "Select Picture"),
                                            PICK_IMAGE);
                                }
                                catch (Exception e) {
                                    Log.e(e.getClass().getName(), e.getMessage(), e);
                                }
                            }
                            else {
                                Snackbar.make(findViewById(R.id.submit_solutions),
                                        "Please select the question numbers to which this image contains answers", Snackbar.LENGTH_LONG)
                                        .setAction("Action", null).show();
                            }
                        }
                        else{
                            Snackbar.make(findViewById(R.id.submit_solutions), "You have attempted all the questions!", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }
                    }

                    public boolean onTouch(View v, MotionEvent event) {
                        return gestureDetector.onTouchEvent(event);
                    }

                });}
            else
                imgView[i].setOnTouchListener(new SwipeTouchListener(upload.this) {

                    public void onSwipeRight() {
                        finish();
                        overridePendingTransition(R.anim.enter_from_left, R.anim.exit_out_right);
                    }

                    public void onClick() {
                        imgView[j].startAnimation(buttonClick);
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.setDataAndType(Uri.fromFile(new File(sol[sol.length-j].filepath)), "image/*");
                        startActivity(intent);
                    }

                    public boolean onTouch(View v, MotionEvent event) {
                        return gestureDetector.onTouchEvent(event);
                    }

                });
        }
    }

    public void remove_dialog(int t){

        LayoutInflater li = LayoutInflater.from(mContext);
        final int temp=t;
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
        builder.setTitle("Remove answer");
        builder.setMessage("Are you sure you want to remove this image?");
        builder.setCancelable(false).setPositiveButton("Remove",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        remove_sol(temp);
                        Intent intent = upload.this.getIntent();
                        finish();
                        startActivity(intent);
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
    public boolean getProgress(){
        filereader read= new filereader();
        String questions_status=read.readfile("questions_status",getApplicationContext());
        if(questions_status.length()==0) return true;
        int total=0,left=0;
        for(;total<questions_status.length();total++) if(questions_status.charAt(total)=='1') left++;
        int percentage=(left)*100/total;
        if(percentage>=60) return true;
        return false;
    }
    private Drawable getScaledDrawable(int newWidth, int newHeight, int id) {

        Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), id);

        Matrix matrix = new Matrix();
        float scaleWidth = ((float) newWidth) / bitmap.getWidth();
        float scaleHeight = ((float) newHeight) / bitmap.getHeight();
        matrix.postScale(scaleWidth, scaleHeight);
        matrix.postRotate(0);
        Bitmap scaledBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

        return new BitmapDrawable(this.getResources(), scaledBitmap);
    }

    public boolean anyselected() {
        for(int i=0;i<new_items_selected.length();i++) if(new_items_selected.charAt(i)!='0') return true;
        return false;
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        overridePendingTransition(R.anim.enter_from_left, R.anim.exit_out_right);
        finish();
    }
    public int height(){
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics ();
        display.getMetrics(outMetrics);

        float density  = getResources().getDisplayMetrics().density;
        float dpWidth  = outMetrics.widthPixels / density;

        int dimension = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpWidth/3-8, getResources().getDisplayMetrics());
        int pad= (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 6, getResources().getDisplayMetrics());
        int x=0;
        try{x=sol.length/3+1;
        }catch (Exception e){x=1;}

        return x*dimension+(x-1)*pad;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }
    public void instructions(){

        LayoutInflater li = LayoutInflater.from(mContext);
        View promptsView = li.inflate(R.layout.prompts, null);
        final EditText userInput = (EditText) promptsView.findViewById(R.id.editTextDialogUserInput);

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
        builder.setTitle("Tips");

        SpannableString instructions = new SpannableString("    signifies that the question will be completely uploaded with this image\n\n");
        SpannableString instructions2 = new SpannableString("    signifies that the image partially includes this question\n\n");
        SpannableString instructions3 = new SpannableString("    signifies that the partial solution of this question has been uploaded\n\n");
        SpannableString instructions4 = new SpannableString("    signifies that the complete solution of this question has been uploaded\n\n");
        Drawable d = getResources().getDrawable(R.drawable.ic_check_box_black_24dp);
        d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
        ImageSpan span = new ImageSpan(d, ImageSpan.ALIGN_BASELINE);
        instructions.setSpan(span, 0, 3, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
//        SpannableString instructions2 = new SpannableString("");
        d = getResources().getDrawable(R.drawable.ic_indeterminate_check_box_black_24dp);
        d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
        span = new ImageSpan(d, ImageSpan.ALIGN_BASELINE);
        instructions2.setSpan(span, 0, 3, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        d = getResources().getDrawable(R.drawable.ic_done_black_24dp);
        d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
        span = new ImageSpan(d, ImageSpan.ALIGN_BASELINE);
        instructions3.setSpan(span, 0, 3, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        d = getResources().getDrawable(R.drawable.ic_done_all_black_24dp);
        d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
        span = new ImageSpan(d, ImageSpan.ALIGN_BASELINE);
        instructions4.setSpan(span, 0, 3, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);

        final ArrayList selectedItems=new ArrayList();

        LayoutInflater adbInflater = LayoutInflater.from(this);
        View eulaLayout = adbInflater.inflate(R.layout.checkbox, null);
        dontShowAgain=(CheckBox)eulaLayout.findViewById(R.id.skip);

        builder.setView(eulaLayout);
        builder.setMessage(TextUtils.concat(instructions, instructions2, instructions3, instructions4));
        builder.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // get user input and set it to result
                        // edit text
                        if (dontShowAgain.isChecked()) {
                            FileOutputStream outputStream1;
                            try {
                                outputStream1 = openFileOutput("upload_token", Context.MODE_PRIVATE);
                                outputStream1.write("meh".getBytes());
                                outputStream1.close();
                            } catch (Exception e) {
                            }
                        }
                    }
                })
        ;


        builder.show();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case PICK_IMAGE:
                if (resultCode == Activity.RESULT_OK) {
                    Uri selectedImageUri = data.getData();
                    String filePath = null;

                    try {
                        // OI FILE Manager
                        String filemanagerstring = selectedImageUri.getPath();

                        // MEDIA GALLERY
                        String selectedImagePath = getPath(selectedImageUri);

                        if (selectedImagePath != null) {
                            filePath = selectedImagePath;
                        } else if (filemanagerstring != null) {
                            filePath = filemanagerstring;
                        } else {
                            Toast.makeText(getApplicationContext(), "Unknown path",
                                    Toast.LENGTH_LONG).show();
                            Log.e("Bitmap", "Unknown path");
                        }

                        if (filePath != null) {
                            decodeFile(selectedImageUri);
                            filepath=getPath(selectedImageUri);
                            ImageUploadTask();
                        } else {
                            bitmap = null;
                        }
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), "Internal error",
                                Toast.LENGTH_LONG).show();
                        Log.e(e.getClass().getName(), e.getMessage(), e);
                    }
                }
                break;
            default:
        }
    }
    public class SolDetails
    {
        String filepath="";
        String[] questions;
        String[] questions_partial;
    }
    public String delete_i(String s,int index)
    {
        int start=-1;
        for(int i=0,k=0;i<s.length()&&k<index;i++) if(s.charAt(i)==';') {k++;start=i;}
        start++;
        int last = s.indexOf(';',start);
        s=s.substring(0,start)+s.substring(last+1);
        return s;
    }
    public boolean contains(String[] s,String a)
    {
        for(int i=0;i<s.length;i++) if(s[i].equals(a)) return true;
        return false;
    }
    public boolean contains(SolDetails[] s,String a,int index)
    {
        for(int i=0;i<s.length;i++) if(index!=i) if(contains(s[i].questions,a)||contains(s[i].questions_partial,a)) return true;
        return false;
    }
    public void remove_sol(int index) {
        filereader read = new filereader();
        String answer_urls = read.readfile("answer_urls", mContext);
        String questions_status = read.readfile("questions_status", mContext);
        String new_questions_status = "";
        String image_paths = read.readfile("image_paths", mContext);
        answer_urls = delete_i(answer_urls, index);
        image_paths = delete_i(image_paths, index);

        for (int i = 0; i < questions_status.length(); i++){
            if (contains(sol[index].questions,String.valueOf(i)) || contains(sol[index].questions_partial, String.valueOf(i))) {
                if (contains(sol, String.valueOf(i), index)) {
                    new_questions_status += "2";
                }
                else new_questions_status += "0";
            }else new_questions_status += (questions_status.charAt(i) + "");
        }
        FileOutputStream outputStream1;
        FileOutputStream outputStream2;
        FileOutputStream outputStream3;
        try {
            outputStream1 = openFileOutput("answer_urls", Context.MODE_PRIVATE);
            outputStream2 = openFileOutput("questions_status", Context.MODE_PRIVATE);
            outputStream3 = openFileOutput("image_paths", Context.MODE_PRIVATE);
            outputStream1.write((answer_urls).getBytes());
            outputStream2.write((new_questions_status).getBytes());
            outputStream3.write(image_paths.getBytes());
            outputStream1.close();
            outputStream2.close();
            outputStream3.close();
        }catch (IOException e){}

    }
    public String getPath(Uri uri) {
        // just some safety built in
        if( uri == null ) {
            // TODO perform some logging or show user feedback
            return null;
        }
        // try to retrieve the image from the media store first
        // this will only work for images selected from gallery
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        if( cursor != null ){
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        // this is our fallback here
        return uri.getPath();
    }
    public String new_items_selected(String new_items_selected,char tag)
    {
        String res="";
        for(int i=0;i<new_items_selected.length();i++) if(new_items_selected.charAt(i)==tag) res+=i+",";
        try{res=res.substring(0,res.length()-1);}
        catch (Exception  e) {res="";}
        return res;
    }
    public void ImageUploadTask() {
        ImageUploadTask task = new ImageUploadTask();
        String url = getApiEndpoint() + "/upload";
        long unixtime=System.currentTimeMillis() / 1000L;
        String payload="/solmate/upload"+unixtime;
        String sign = signature.hmacDigest(payload);
        filereader read = new filereader();
        task.execute(url, getapikey(), String.valueOf(unixtime), sign, read.readfile("token", upload.this), read.readfile("uid", upload.this));

    }
    class ImageUploadTask extends AsyncTask <String, Void, String>{

        ProgressDialog progDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progDialog = new ProgressDialog(upload.this);
            progDialog.setMessage("Uploading...");
            progDialog.setIndeterminate(false);
            progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progDialog.setCancelable(false);
            progDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    cancel(true);
                    dialog.dismiss();
                }
            });
            progDialog.show();
//            progDialog = new ProgressDialog(upload.this);
//            progDialog.setMessage("Sending...");
//            progDialog.setIndeterminate(false);
//            progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//            progDialog.setCancelable(true);
//            progDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
//                @Override
//                public void onCancel(DialogInterface dialog) {
//                    // actually could set running = false; right here, but I'll
//                    // stick to contract.
//                    cancel(true);
//                }
//            });
//            progDialog.show();

        }

        @Override
        protected String doInBackground(String... params) {
            while (!isCancelled()){
                try {
                    HttpClient client = new DefaultHttpClient();
                    String postURL = getApiEndpoint()+"/upload";
                    HttpPost post = new HttpPost(postURL);
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    MultipartEntityBuilder entity= MultipartEntityBuilder.create();
                    entity.addPart("apikey",new StringBody(params[1], ContentType.APPLICATION_JSON));
                    entity.addPart("timestamp",new StringBody(params[2], ContentType.APPLICATION_JSON));
                    entity.addPart("signature",new StringBody(params[3], ContentType.APPLICATION_JSON));
                    entity.addPart("token", new StringBody(params[4], ContentType.APPLICATION_JSON));
                    entity.addPart("uid", new StringBody(params[5], ContentType.APPLICATION_JSON));
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                    byte[] data = bos.toByteArray();
                    ByteArrayBody bin = new ByteArrayBody(data, "image");
                    entity.addBinaryBody("image",data,ContentType.create("image/png"), "image.png");
//                entity.addPart("image", new FileBody(f, "image/png"));
//                post.setHeader("Accept", "application/json");
//                post.setHeader("Content-type", "application/json");
//                post.setHeader("enctype","multipart/form-data");
//                post.setHeader("accept-charset","UTF-8");
                    post.setEntity(entity.build());
                    HttpResponse response = client.execute(post);
                    HttpEntity resEntity = response.getEntity();
                    if (resEntity != null) {
                        return EntityUtils.toString(resEntity);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return e.toString();
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... unsued) {

        }

        @Override
        protected void onPostExecute(String sResponse) {
            try{
                if (sResponse!=null) {
                    JSONObject JResponse = new JSONObject(sResponse);
                    if (!JResponse.getBoolean("success")) {
                        if(JResponse.get("type").equals("auth")) cleandetails();
                        Toast.makeText(upload.this, "Something went wrong", Toast.LENGTH_LONG).show();
                    }
                    else {
                        String url_image = JResponse.getString("url");
                        //update file and write url
                        FileOutputStream outputStream1;
                        FileOutputStream outputStream2;
                        FileOutputStream outputStream3;
                        try {
                            outputStream1 = openFileOutput("answer_urls", Context.MODE_APPEND);
                            outputStream2 = openFileOutput("questions_status", Context.MODE_PRIVATE);
                            outputStream3 = openFileOutput("image_paths", Context.MODE_APPEND);
                            outputStream1.write((url_image+";").getBytes());
                            outputStream3.write((filepath + "/" + new_items_selected(new_items_selected, '1') + "/" + new_items_selected(new_items_selected,'2')+";").getBytes());
                            String meh="";
                            for(int i=0;i<items_selected.length();i++) {
                                if (items_selected.charAt(i) == '1' || new_items_selected.charAt(i) == '1' ) meh += "1";
                                else if(items_selected.charAt(i) == '2') meh+= "2";
                                else meh+="0";
                            }
                            outputStream2.write(meh.getBytes());
                            outputStream1.close();
                            outputStream2.close();
                            outputStream3.close();
                        }catch (IOException e){}

                        Toast.makeText(getApplicationContext(),
                                "Photo added successfully",
                                Toast.LENGTH_SHORT).show();
                        try {
                            progDialog.dismiss();
                        } catch (Exception e) {
                        }
                        save_states();
                        Intent intent = upload.this.getIntent();
                        finish();
                        startActivity(intent);
                    }
                }else {
                    try {
                        progDialog.dismiss();
                    } catch (Exception e) {
                    }
                    Toast.makeText(getApplicationContext()," Please check your internet connection and try again!", Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(),"Something went wrong",
                        Toast.LENGTH_LONG).show();
                Log.e(e.getClass().getName(), e.getMessage(), e);
            }
        }
    }


    public void cleandetails(){

        FileOutputStream outputStream1;
        FileOutputStream outputStream2;
        FileOutputStream outputStream3;
        FileOutputStream outputStream5;
        FileOutputStream outputStream6;
        FileOutputStream outputStream7;
        FileOutputStream outputStream8;
        try {

            outputStream1 = openFileOutput("token", Context.MODE_PRIVATE);
            outputStream2 = openFileOutput("expiresAt", Context.MODE_PRIVATE);
            outputStream3 = openFileOutput("uid", Context.MODE_PRIVATE);
            outputStream5 = openFileOutput("qsUrl", Context.MODE_PRIVATE);
            outputStream6 = openFileOutput("questions", Context.MODE_PRIVATE);
            outputStream7 = openFileOutput("qsAllottedAt", Context.MODE_PRIVATE);
            outputStream8 = openFileOutput("qsExpiresAt", Context.MODE_PRIVATE);
            outputStream1.write("".getBytes());
            outputStream2.write("".getBytes());
            outputStream3.write("".getBytes());
            outputStream5.write("".getBytes());
            outputStream6.write("".getBytes());
            outputStream7.write("".getBytes());
            outputStream8.write("".getBytes());
            outputStream1.close();
            outputStream2.close();
            outputStream3.close();
            outputStream5.close();
            outputStream6.close();
            outputStream7.close();
            outputStream8.close();
        } catch (Exception e) {
        }
        Intent intent = new Intent(upload.this, OTP.class);
        Main.fa.finish();
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
        finish();
    }

    public void decodeFile(Uri uri) {
        ParcelFileDescriptor parcelFD = null;
        try {
            parcelFD = getContentResolver().openFileDescriptor(uri, "r");
            FileDescriptor imageSource = parcelFD.getFileDescriptor();

            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeFileDescriptor(imageSource, null, o);

            // the new size we want to scale to
            final int REQUIRED_SIZE = 2048;

            // Find the correct scale value. It should be the power of 2.
            int width_tmp = o.outWidth, height_tmp = o.outHeight;
            int scale = 1;
            while (true) {
                if (width_tmp < REQUIRED_SIZE && height_tmp < REQUIRED_SIZE) {
                    break;
                }
                width_tmp /= 2;
                height_tmp /= 2;
                scale *= 2;
            }

            // decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            bitmap = BitmapFactory.decodeFileDescriptor(imageSource, null, o2);

        } catch (FileNotFoundException e) {
            // handle errors
        } finally {
            if (parcelFD != null)
                try {
                    parcelFD.close();
                } catch (IOException e) {
                    // ignored
                }
        }
    }


    public Bitmap decodesmall(Uri uri) {
        ParcelFileDescriptor parcelFD = null;
        try {
            parcelFD = getContentResolver().openFileDescriptor(uri, "r");
            FileDescriptor imageSource = parcelFD.getFileDescriptor();

            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeFileDescriptor(imageSource, null, o);

            // the new size we want to scale to
            final int REQUIRED_SIZE = 500;

            // Find the correct scale value. It should be the power of 2.
            int width_tmp = o.outWidth, height_tmp = o.outHeight;
            int scale = 1;
            while (true) {
                if (width_tmp < REQUIRED_SIZE && height_tmp < REQUIRED_SIZE) {
                    break;
                }
                width_tmp /= 2;
                height_tmp /= 2;
                scale *= 2;
            }

            // decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeFileDescriptor(imageSource, null, o2);

        } catch (FileNotFoundException e) {
            // handle errors
        } finally {
            if (parcelFD != null)
                try {
                    parcelFD.close();
                } catch (IOException e) {
                    // ignored
                }
        }
        return null;
    }

    public String getApiEndpoint() {
        return "http://appsdev.sarthy.in/solmate";
    }

    private String getapikey()
    {
        return "dffa5c8fb41a6716b6268ce81e45fa61";
    }

}