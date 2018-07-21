package gr.mobap.mystories.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class MyStory implements Serializable, Parcelable {

    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("main")
    @Expose
    private String main;
    @SerializedName("photo")
    @Expose
    private String photo;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("user")
    @Expose
    private String user;
    @SerializedName("video")
    @Expose
    private String video;
    @SerializedName("favorited")
    @Expose
    private String favorited;

    public MyStory(Parcel in) {
        this.date = ((String) in.readValue((String.class.getClassLoader())));
        this.main = ((String) in.readValue((String.class.getClassLoader())));
        this.photo = ((String) in.readValue((String.class.getClassLoader())));
        this.title = ((String) in.readValue((String.class.getClassLoader())));
        this.type = ((String) in.readValue((String.class.getClassLoader())));
        this.user = ((String) in.readValue((String.class.getClassLoader())));
        this.video = ((String) in.readValue((String.class.getClassLoader())));
        this.favorited = ((String) in.readValue((String.class.getClassLoader())));
    }

    /**
     * No args constructor for use in serialization
     */
    public MyStory() {
    }

    public MyStory(String date, String main, String photo, String title, String type, String user, String video, String favorited) {
        this.date = date;
        this.main = main;
        this.photo = photo;
        this.title = title;
        this.type = type;
        this.user = user;
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

    public String getMain() {
        return main;
    }

    public void setMain(String main) {
        this.main = main;
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

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public String getFavorited() {
        return favorited;
    }

    public void setFavorited(String favorited) {
        this.favorited = favorited;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(date);
        dest.writeString(main);
        dest.writeString(photo);
        dest.writeString(title);
        dest.writeString(type);
        dest.writeString(user);
        dest.writeString(video);
        dest.writeString(video);
    }

    public int describeContents() {
        return 0;
    }

}
