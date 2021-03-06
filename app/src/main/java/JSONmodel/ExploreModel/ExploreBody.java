package JSONmodel.ExploreModel;


import com.google.gson.annotations.SerializedName;

/**
 * Created by ASHL7 on 2/16/2017.
 * Class representing a call to Foursquare API
 */
public class ExploreBody {

    @SerializedName("response")
    private Response response;

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

}
