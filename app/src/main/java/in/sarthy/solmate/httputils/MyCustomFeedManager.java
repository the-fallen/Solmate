package in.sarthy.solmate.httputils;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Modifier;
import java.net.UnknownHostException;
import java.util.List;
import java.util.zip.GZIPInputStream;


public class MyCustomFeedManager {


    public static String executePostRequest(String url, List<NameValuePair> postParams){
        String responseStrData = null;
        url = url.trim().replace(" ", "%20");
        HttpPost postRequest = new HttpPost(url);
        HttpEntity entity = null;
        postRequest.addHeader("Accept-Encoding", "gzip");

        try {
            postRequest.setEntity(new UrlEncodedFormEntity(postParams));
            HttpResponse httpResponse = MyCustomHttpHelper.execute(postRequest);
            int statusCode = httpResponse.getStatusLine().getStatusCode();
            //Success
            if (statusCode == HttpStatus.SC_OK || statusCode == HttpStatus.SC_CREATED) {
                entity = httpResponse.getEntity();
                if (entity != null) {
                    //String responseStrData = generateString(entity.getContent());
                    responseStrData = generateStringFromHttpResponse(httpResponse);
                }
            } else {//Failure but got httpResponse
//                wrapHttpError(httpResponse, feedResponse);
            }
        } catch (UnknownHostException ex) {
        } catch (ClientProtocolException ex) {
        } catch (ConnectTimeoutException ex) {
        } catch (IOException ex) {
        } finally {
            if (entity != null) {
                try {
                    entity.consumeContent();
                } catch (IOException e) {
                }
            }
        }
        return responseStrData;
    }
    public static String executeGetRequest(String url) {
        String responseStrData = null;
        url = url.trim().replace(" ", "%20");
        HttpGet getRequest = new HttpGet(url);
        HttpEntity entity = null;
        getRequest.addHeader("Accept-Encoding", "gzip");

        try {
            HttpResponse httpResponse = MyCustomHttpHelper.execute(getRequest);
            int statusCode = httpResponse.getStatusLine().getStatusCode();
            //Success
            if (statusCode == HttpStatus.SC_OK || statusCode == HttpStatus.SC_CREATED) {
                entity = httpResponse.getEntity();
                if (entity != null) {
                    //String responseStrData = generateString(entity.getContent());
                    responseStrData = generateStringFromHttpResponse(httpResponse);
                }
            } else {//Failure but got httpResponse
//                wrapHttpError(httpResponse, feedResponse);
            }
        } catch (UnknownHostException ex) {
        } catch (ClientProtocolException ex) {
        } catch (ConnectTimeoutException ex) {
        } catch (IOException ex) {
        } finally {
            if (entity != null) {
                try {
                    entity.consumeContent();
                } catch (IOException e) {
                }
            }
        }
        return responseStrData;
    }

    public static String generateStringFromHttpResponse(HttpResponse httpResponse) {

        InputStream instream = null;
        StringBuilder sb = new StringBuilder();
        try {
            instream = httpResponse.getEntity().getContent();
            Header contentEncoding = httpResponse.getFirstHeader("Content-Encoding");
            if (contentEncoding != null && contentEncoding.getValue().equalsIgnoreCase("gzip")) {
                instream = new GZIPInputStream(instream);
//                Log.i("HttpManager", url + " Length is " + httpResponse.getEntity().getContentLength() + "");
            } else {
//                Log.i("HttpManager","Without gZIP Url is "+url+" Length is "+ httpResponse.getEntity().getContentLength()+"");
            }
            InputStreamReader reader = new InputStreamReader(instream);
            BufferedReader buffer = new BufferedReader(reader);
            String cur;
            while ((cur = buffer.readLine()) != null) {
                sb.append(cur + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        try {
            instream.close();

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return sb.toString();
    }

    public String generateString(InputStream stream) {
        InputStreamReader reader = new InputStreamReader(stream);
        BufferedReader buffer = new BufferedReader(reader);
        StringBuilder sb = new StringBuilder();
        try {
            String cur;
            while ((cur = buffer.readLine()) != null) {
                sb.append(cur + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        try {
            stream.close();

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return sb.toString();
    }

    public static Object getMappedModel(String value, Class<?> className) {
        Object businessObject = null;
        Gson gson = new GsonBuilder().excludeFieldsWithModifiers(Modifier.STATIC, Modifier.PROTECTED)
                .registerTypeAdapterFactory(new MyCustomArrayAdapterFactory())
                        //.registerTypeAdapter(businessObjectListType, new ArrayObjectChanger())
                .create();
        try{
            businessObject =  gson.fromJson(value, className);
        }catch (Exception e){
            String de = e.toString();
            String fds= "";
        }
        return businessObject;
    }
}
