package in.sarthy.solmate.httputils;


import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.IOException;

public class MyCustomHttpHelper {
    private static final int TIME_OUT = 30000;
    private static final DefaultHttpClient mClient;

    static {
        final HttpParams params = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(params, TIME_OUT);
        SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        schemeRegistry.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));

        ClientConnectionManager manager = new ThreadSafeClientConnManager(params, schemeRegistry);
        mClient = new DefaultHttpClient(manager, params);
        //mClient = new DefaultHttpClient();
    }

    public static HttpResponse execute(HttpPost post) throws IOException, ClientProtocolException {
        return mClient.execute(post);
    }

    public static HttpResponse execute(HttpPut put) throws IOException, ClientProtocolException {
        return mClient.execute(put);
    }

    public static HttpResponse execute(HttpGet get) throws IOException, ClientProtocolException {
        return mClient.execute(get);
    }

    public static void clearCookies(boolean isToClearCookie) {
        if (isToClearCookie)
            mClient.getCookieStore().clear();
    }
}