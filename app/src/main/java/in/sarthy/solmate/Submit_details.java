package in.sarthy.solmate;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Submit_details implements Serializable {

    @SerializedName("success")
    private Boolean success;

    @SerializedName("token")
    private String token;

    @SerializedName("type")
    private String type;

    @SerializedName("expiresAt")
    private int expiresAt;

    @SerializedName("uid")
    private String uid;

    @SerializedName("qsid")
    private String qsid;

    @SerializedName("qsUrl")
    private String qsUrl;

    @SerializedName("questions")
    private String[] questions;

    @SerializedName("qsAllottedAt")
    private String qsAllottedAt;

    @SerializedName("qsExpiresAt")
    private String qsExpiresAt;

    public Boolean getSuccess(){
        return success;
    }
    public String getToken(){
        return token;
    }
    public String getType(){
        return type;
    }
    public String getUid(){
        return uid;
    }
    public int getExpiresAt(){
        return expiresAt;
    }
    public String getQsAllottedAt(){
        return qsAllottedAt;
    }
    public String getQsid(){
        return qsid;
    }
    public String getQsUrl(){
        return qsUrl;
    }
    public String getQsExpiresAt(){
        return qsExpiresAt;
    }
    public String[] getQuestions(){
        return questions;
    }
}