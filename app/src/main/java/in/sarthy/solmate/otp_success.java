package in.sarthy.solmate;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class otp_success implements Serializable {

    @SerializedName("success")
    private Boolean success;

    @SerializedName("registered")
    private Boolean registered;

    @SerializedName("name")
    private String name;

    @SerializedName("type")
    private String type;

    public Boolean getSuccess(){
        return success;
    }
    public Boolean getRegistered(){
        return registered;
    }
    public String getName() {return name;}
    public String getType() {return type;}
}