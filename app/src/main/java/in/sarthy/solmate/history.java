package in.sarthy.solmate;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;


public class history extends AppCompatActivity {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private Button his;
    private Boolean hisexists=true;
    private EditText num;
    private EditText start;
    String para;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        Bundle b=this.getIntent().getExtras();
        para=b.getString("para");
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setOffscreenPageLimit(4);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);


    }
    private String getTitle(String timestamp) {
        String res = "";
        try {
            String[] months = new String[]{"Jan", "Feb", "Mar", "Apr", "May", "June", "July", "Aug", "Sep", "Oct", "Nov", "Dec"};
            timestamp = timestamp.substring(0, timestamp.length() - 1);
            timestamp = timestamp.replace("T", " ");
            Timestamp time = Timestamp.valueOf(timestamp);
            res += time.getDate() + " " + months[time.getMonth()];
        }
        catch (Exception e){}
        return res;
    }
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        apiresult ApiResult = makeResult();
        if(!ApiResult.equals(null) && ApiResult.res.length!=0) {
            for (int i = 0; i < ApiResult.res.length; i++) {
                OneFragment fragment = new OneFragment();
                fragment.res = ApiResult.res[i];
                fragment.mContext = getApplicationContext();
                adapter.addFragment(fragment, getTitle(ApiResult.res[i].allotedAt));
            }
        }
        else {
            OneFragment fragment = new OneFragment();
            hisexists=false;
            adapter.addFragment(fragment, "           ");
        }
        viewPager.setAdapter(adapter);
    }

    public String getApiEndpoint() {
        return "http://appsdev.sarthy.in";
    }


    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
    public class apiresult
    {
        result[] res;
    }
    public class result{
        String qsurl;       //added
        String para;
        String allotedAt;   //added
        String submittedAt; //added
        String expiresAt;   //added
        String usercomment; //added
        String admincomment;//added
        String[] qname;
        String[] qstatus;
        public void initialize(int len)
        {
            qname=new String[len];
            qstatus=new String[len];
        }
    }
    public String search(String json,String key) {
        String tmp = "";
        int flag=0;
        for(int i=0;i<json.length();i++)
        {
            if(json.charAt(i)=='"') {
                if(flag==2) return tmp;
                if(flag==1) flag=2;
                if(tmp.equals(key)) flag=1;
                System.out.println(tmp+"meh");
                tmp="";

            }
            else
                tmp+=json.substring(i,i+1);
        }
        return "";
    }
    public apiresult makeResult()
    {
        try {
            final JSONObject jObject = new JSONObject(para);
            apiresult ApiResult=new apiresult();
            JSONArray res = jObject.getJSONArray("result");
            ApiResult.res=new result[res.length()];
            for (int i = 0; i < res.length(); i++) {  // **line 2**
                JSONObject childJSONObject = res.getJSONObject(i);
                ApiResult.res[i]=new result();
                ApiResult.res[i].para=childJSONObject.toString();
                ApiResult.res[i].qsurl = childJSONObject.getString("qsUrl");
                ApiResult.res[i].qsurl =getApiEndpoint() + ApiResult.res[i].qsurl;
                ApiResult.res[i].expiresAt = search(childJSONObject.toString(), "expiresAt");
                ApiResult.res[i].allotedAt = search(childJSONObject.toString(),"allottedAt");
                ApiResult.res[i].submittedAt = "";
                ApiResult.res[i].submittedAt = search(childJSONObject.toString(),"submittedAt");
                ApiResult.res[i].usercomment = childJSONObject.getString("userComment");
                ApiResult.res[i].admincomment = childJSONObject.getString("adminComment");
                JSONArray qs = childJSONObject.getJSONArray("questions");
                ApiResult.res[i].initialize(qs.length());
                for (int j = 0; j < qs.length(); j++) {
                    JSONObject child = qs.getJSONObject(j);
                    ApiResult.res[i].qname[j] = child.getString("name");
                    ApiResult.res[i].qstatus[j] = child.getString("status");
                }
            }
            return ApiResult;
        } catch (Exception e) {
            Toast.makeText(history.this, e.toString()+"Something went wrong", Toast.LENGTH_LONG).show();
            return null;
        }
    }

}