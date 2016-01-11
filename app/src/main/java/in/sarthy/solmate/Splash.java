package in.sarthy.solmate;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Environment;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import java.io.File;
import java.io.FileOutputStream;

public class Splash extends Activity {

    private Button login;
    private Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mContext=this;
        clearall();
        filereader read=new filereader();

        String key=read.readfile("token", getApplicationContext());
        if(key!="") {
            Intent intent = new Intent(mContext, Main.class);
            startActivity(intent);
            finish();
        }
        else {
            key = read.readfile("sol_token", getApplicationContext());
            if (key != "") {
                Intent intent = new Intent(mContext, OTP.class);
                startActivity(intent);
                finish();
            }
        }
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        float[] arr=new float[4];
        int[][] arr2={{320,480},{640,960},{640,1136},{1080,1920}};
        int min=0;

        for(int i=0;i<4;i++){
            arr[i]=(float)arr2[i][1]/arr2[i][0]-(float)height/width;
            if(arr[i]>0 && arr[min]>0)if(arr[i]-arr[min]<0)min=i;
            else if(arr[i]>0 && arr[min]<0)if(arr[i]+arr[min]<0.05)min=i;
            else if(arr[i]<0 && arr[min]>0)if(arr[i]+arr[min]>0.05)min=i;
            else if(arr[i]<0 && arr[min]<0)if(-arr[i]+arr[min]<0)min=i;
        }

        if(min==0 || min==1){
            float diff1=Math.abs(width-arr2[0][0])+Math.abs(height-arr2[0][1]);
            float diff2=Math.abs(width-arr2[1][0])+Math.abs(height-arr2[1][1]);
            if(diff2<diff1)min=1;
            else min=0;
        }
        if(min==3 || min==2){
            float diff1=Math.abs(width-arr2[2][0])+Math.abs(height-arr2[2][1]);
            float diff2=Math.abs(width-arr2[3][0])+Math.abs(height-arr2[3][1]);
            if(diff2<diff1)min=3;
            else min=2;

        }

        RelativeLayout rl = (RelativeLayout)findViewById(R.id.rl);
        if(min==0)
            rl.setBackgroundResource(R.drawable.splash1);
        if(min==1)
            rl.setBackgroundResource(R.drawable.splash2);
        if(min==2)
            rl.setBackgroundResource(R.drawable.splash3);
        if(min==3)
            rl.setBackgroundResource(R.drawable.splash4);

        login = (Button)findViewById(R.id.button1);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addtoken();
                Intent intent = new Intent(mContext, OTP.class);
                startActivity(intent);
                overridePendingTransition(R.anim.enter_from_right, R.anim.exit_out_left);
                finish();
            }
        });
    }
    public void clearall() {
        try {
            File dir = new File(Environment.getExternalStorageDirectory() + "/Solmate/questions");
            if (dir.isDirectory()) {
                String[] children = dir.list();
                for (int i = 0; i < children.length; i++) {
                    new File(dir, children[i]).delete();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void addtoken()
    {
        FileOutputStream outputStream1;
        try {
            outputStream1 = openFileOutput("sol_token", Context.MODE_PRIVATE);
            outputStream1.write("meh".getBytes());
            outputStream1.close();
        }catch (Exception e){}
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_splash, menu);
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
