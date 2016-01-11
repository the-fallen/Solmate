package in.sarthy.solmate;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Anil on 12/11/2015.
 */
public class Request_result implements Serializable{

    @SerializedName("success")
    private Boolean success;

    @SerializedName("expiresAt")
    private String expiresAt;

    @SerializedName("qsid")
    private String qsid;

    @SerializedName("qsUrl")
    private String qsUrl;

    @SerializedName("questions")
    private String[] questions;

    @SerializedName("allottedAt")
    private String allottedAt;

    public Boolean getSuccess(){
        return success;
    }
    public String getExpiresAt(){
        return expiresAt;
    }
    public String getAllottedAt(){
        return allottedAt;
    }
    public String getQsid(){
        return qsid;
    }
    public String getQsUrl(){
        return qsUrl;
    }
    public String[] getQuestions(){
        return questions;
    }

}
