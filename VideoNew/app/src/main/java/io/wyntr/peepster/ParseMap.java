package io.wyntr.peepster;

/**
 * Created by Siddharth on 4/12/16.
 */
import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
/**
 * Created by sagar_000 on 4/7/2016.
 */
@ParseClassName("AroundMe")
public class ParseMap extends ParseObject {
    public String getText() {
        return getString("text");
    }
    public void setText(String value) {
        put("text", value);
    }
    public String getUser(){
        return getString(ParseConstants.KEY_SENDER_NAME);
    }
    public void setUser(ParseUser value) {
        put(ParseConstants.KEY_SENDER_NAME, value);
    }
    public ParseFile getVideoThumb(){
        return getParseFile("Thumbs");
    }
    public ParseGeoPoint getLocation() {
        return getParseGeoPoint("GeoArea");
    }
    public void setLocation(ParseGeoPoint value) {
        put("GeoArea", value);
    }
    public ParseFile getVideo(){
        return getParseFile("file");
    }
    public int getViews(){
        return getInt("views");
    }
    public String getUserId(){
        return getString("senderId");
    }
    public static ParseQuery<ParseMap> getQuery() {
        return ParseQuery.getQuery(ParseMap.class);
    }
}