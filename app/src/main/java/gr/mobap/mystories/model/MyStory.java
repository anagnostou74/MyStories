package gr.mobap.mystories.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class MyStory implements Serializable, Parcelable {

    @SerializedName("date")
    @Expose
    public String date;
    @SerializedName("prologue")
    @Expose
    public String prologue;
    @SerializedName("body")
    @Expose
    public String body;
    @SerializedName("epilogue")
    @Expose
    public String epilogue;
    @SerializedName("photo")
    @Expose
    public String photo;
    @SerializedName("title")
    @Expose
    public String title;
    @SerializedName("type")
    @Expose
    public String type;
    @SerializedName("user")
    @Expose
    public String user;
    @SerializedName("image")
    @Expose
    public String image;
    @SerializedName("email")
    @Expose
    public String email;
    @SerializedName("video")
    @Expose
    public String video;
    @SerializedName("favorited")
    @Expose
    public Integer favorited;
    public Map<String, Boolean> stars = new HashMap<>();

    public MyStory(Parcel in) {
        this.date = ((String) in.readValue((String.class.getClassLoader())));
        this.prologue = ((String) in.readValue((String.class.getClassLoader())));
        this.body = ((String) in.readValue((String.class.getClassLoader())));
        this.epilogue = ((String) in.readValue((String.class.getClassLoader())));
        this.photo = ((String) in.readValue((String.class.getClassLoader())));
        this.title = ((String) in.readValue((String.class.getClassLoader())));
        this.type = ((String) in.readValue((String.class.getClassLoader())));
        this.user = ((String) in.readValue((String.class.getClassLoader())));
        this.image = ((String) in.readValue((String.class.getClassLoader())));
        this.email = ((String) in.readValue((String.class.getClassLoader())));
        this.video = ((String) in.readValue((String.class.getClassLoader())));
        this.favorited = ((Integer) in.readValue((Integer.class.getClassLoader())));
    }

    /**
     * No args constructor for use in serialization
     *
     * @param date
     * @param prologue
     * @param body
     * @param epilogue
     * @param photo
     * @param title
     * @param user
     * @param email
     * @param i
     * @param image
     * @param type
     */
    public MyStory(String date, String prologue, String body, String epilogue, String photo, String title, String user, String email, int i, String image, String type) {
    }

    public MyStory() {
    }

    public MyStory(String date, String prologue, String body, String epilogue, String photo, String title, String type, String user, String image, String email, String video, Integer favorited) {
        this.date = date;
        this.prologue = prologue;
        this.body = body;
        this.epilogue = epilogue;
        this.photo = photo;
        this.title = title;
        this.type = type;
        this.user = user;
        this.image = image;
        this.email = email;
        this.video = video;
        this.favorited = favorited;
    }


    public static final Creator<MyStory> CREATOR = new Creator<MyStory>() {
        @Override
        public MyStory createFromParcel(Parcel in) {
            return new MyStory(in);
        }

        @Override
        public MyStory[] newArray(int size) {
            return new MyStory[size];
        }
    };

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPrologue() {
        return prologue;
    }

    public void setPrologue(String prologue) {
        this.prologue = prologue;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getEpilogue() {
        return epilogue;
    }

    public void setEpilogue(String epilogue) {
        this.epilogue = epilogue;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public Integer getFavorited() {
        return favorited;
    }

    public void setFavorited(Integer favorited) {
        this.favorited = favorited;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(date);
        dest.writeString(prologue);
        dest.writeString(body);
        dest.writeString(epilogue);
        dest.writeString(photo);
        dest.writeString(title);
        dest.writeString(type);
        dest.writeString(user);
        dest.writeString(image);
        dest.writeString(email);
        dest.writeString(video);
        dest.writeInt(favorited);
    }

    public int describeContents() {
        return 0;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("date", date);
        result.put("prologue", prologue);
        result.put("body", body);
        result.put("epilogue", epilogue);
        result.put("photo", photo);
        result.put("title", title);
        result.put("type", type);
        result.put("user", user);
        result.put("image", image);
        result.put("email", email);
        result.put("video", video);
        result.put("favorited", favorited);

        return result;
    }

}
