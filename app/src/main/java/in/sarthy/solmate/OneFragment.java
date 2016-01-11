package in.sarthy.solmate;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Timestamp;

import in.sarthy.solmate.lazylist.ImageLoader;


public class OneFragment extends Fragment{
    public Context mContext;
    private ImageLoader imageLoader;
    public history.result res;
    public OneFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View meh;
        try{

            meh = inflater.inflate(R.layout.fragment_one, container, false);

            String[] mainText=new String[5];
            for(int i=0;i<5;i++) mainText[i]="";

            try{mainText[0]=format(res.submittedAt);
            }catch (Exception e){
            }
            try {mainText[1]=format(res.expiresAt);
            }catch (Exception e){}
            try{mainText[2]=format(res.allotedAt);
            }catch (Exception e){}

            if(mainText[0].equals("")||mainText[0].equals(null))mainText[0]="Not submitted";
            mainText[3]=res.usercomment;
            if(mainText[3].equals("")) {
                mainText[3]="-- empty --";
            }
            mainText[4]=res.admincomment;
            if(mainText[4].equals("")) {
                mainText[4]="-- empty --";
            }

            TableLayout tbLayout = (TableLayout) meh.findViewById(R.id.table);
            int color=0;
            Typeface typeface=null;
            for(int i=0;i<5;i++)
            {
                TableRow row = (TableRow) tbLayout.getChildAt(i);
                TextView button = (TextView) row.getChildAt(1); // get child index on particular row
                button.setText(mainText[i]);
                if(i==0){
                    typeface = button.getTypeface();
                    color=button.getCurrentTextColor();
                }
            }
            int padding_in_dp = 100;  // 6 dps
            final float scale = getResources().getDisplayMetrics().density;
            int padding_in_px = (int) (padding_in_dp * scale + 0.5f);

            TableLayout tl = (TableLayout) meh.findViewById(R.id.main_table);

            TableRow tr_head = new TableRow(mContext);
            tr_head.setId(R.id.titleId);
            tr_head.setLayoutParams(new TableRow.LayoutParams(
                    TableRow.LayoutParams.FILL_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT));
            tr_head.setPadding(0, 8, 0, 8);

            TextView label_date = new TextView(mContext);
            label_date.setText("Question");
            label_date.setTextColor(color);
            label_date.setTypeface(typeface, Typeface.BOLD);
            label_date.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            label_date.setPadding(0, 0, padding_in_px, 0);
            tr_head.addView(label_date);// add the column to the table row here


            TextView text2= new TextView(mContext);
            text2.setText("Status");
            text2.setTextColor(color);
            text2.setTypeface(typeface, Typeface.BOLD);
            text2.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            tr_head.addView(text2);// add the column to the table row here

            tl.addView(tr_head, new TableLayout.LayoutParams(
                    TableLayout.LayoutParams.FILL_PARENT,
                    TableLayout.LayoutParams.WRAP_CONTENT));
            for (int i = 0; i < res.qname.length; i++) {

                TableRow temp = new TableRow(mContext);
                temp.setId(R.id.titleId + i);
                temp.setLayoutParams(new TableRow.LayoutParams(
                        TableRow.LayoutParams.FILL_PARENT,
                        TableRow.LayoutParams.WRAP_CONTENT));
                temp.setPadding(0, 8, 0, 8);

                TextView text1= new TextView(mContext);
                text1.setText("     "+res.qname[i]);
                text1.setTextColor(color);
                text1.setTypeface(typeface, Typeface.BOLD);
                text1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                text1.setPadding(0, 0, padding_in_px, 0);
                temp.addView(text1);// add the column to the table row here


                TextView question = new TextView(mContext);
                String str="";
                if(res.qstatus[i].equals("N")) {
                    str="Not Attempted";
                }
                else if(res.qstatus[i].equals("P")) {
                    str="Pending";
                }
                else if(res.qstatus[i].equals("R")) {
                    str="Rejected";
                }
                else if(res.qstatus[i].equals("A")) {
                    str="Approved";
                }
                question.setText(str);
                question.setTextColor(color);
                question.setTypeface(typeface);
                question.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                temp.addView(question);// add the column to the table row here

                tl.addView(temp, new TableLayout.LayoutParams(
                        TableLayout.LayoutParams.FILL_PARENT,
                        TableLayout.LayoutParams.WRAP_CONTENT));

            }

            ImageView imageView = (ImageView) meh.findViewById(R.id.im);
            imageLoader=new ImageLoader(mContext);
            imageLoader.DisplayImage(res.qsurl, imageView);
            imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(imageLoader.getLoaded()){
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.parse("file://" + imageLoader.getLocation()), "image/*");
                    startActivity(intent);
                }
            }
        });
        }catch (Exception e){
            meh = inflater.inflate(R.layout.no_history, container, false);
        }
        return meh;
    }
    private String format(String timestamp) {
        String res = "";
        try {
            String[] months = new String[]{"Jan", "Feb", "Mar", "Apr", "May", "June", "July", "Aug", "Sep", "Oct", "Nov", "Dec"};
            timestamp = timestamp.substring(0, timestamp.length() - 1);
            timestamp = timestamp.replace("T", " ");
            Timestamp time = Timestamp.valueOf(timestamp);
            time.setTime(time.getTime()+55*6*60*1000);
            res +=time.getDate() + " " + months[time.getMonth()]+", "+time.getHours()+":"+time.getMinutes()+":"+time.getSeconds();

        }
        catch (Exception e){}
        return res;
    }
    @Override
    public void onDestroyView(){
        super.onDestroy();
        try{imageLoader.imageclear();}
        catch (Exception e){}
    }

}
