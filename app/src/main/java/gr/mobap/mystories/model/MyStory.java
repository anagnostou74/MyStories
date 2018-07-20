package gr.mobap.mystories.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;


public class MyStory implements Serializable, Parcelable {

    @SerializedName("date")
    @Expose
    private String Date;
    @SerializedName("main")
    @Expose
    private String Main;
    @SerializedName("photo")
    @Expose
    private String Photo;
    @SerializedName("title")
    @Expose
    private String Title;
    @SerializedName("type")
    @Expose
    private String Type;
    @SerializedName("user")
    @Expose
    private String User;
    @SerializedName("video")
    @Expose
    private String Video;

    public MyStory(Parcel in) {
        this.Date = ((String) in.readValue((String.class.getClassLoader())));
        this.Main = ((String) in.readValue((String.class.getClassLoader())));
        this.Photo = ((String) in.readValue((String.class.getClassLoader())));
        this.Title = ((String) in.readValue((String.class.getClassLoader())));
        this.Type = ((String) in.readValue((String.class.getClassLoader())));
        this.User = ((String) in.readValue((String.class.getClassLoader())));
        this.Video = ((String) in.readValue((String.class.getClassLoader())));
    }

    /**
     * No args constructor for use in serialization
     */
    public MyStory() {
    }

    public MyStory(String date, String main, String photo, String title, String type, String user, String video) {
        this.Date = date;
        this.Main = main;
        this.Photo = photo;
        this.Title = title;
        this.Type = type;
        this.User = user;
        this.Video = video;
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
        return Date;
    }

    public void setDate(String date) {
        this.Date = date;
    }

    public String getMain() {
        return Main;
    }

    public void setMain(String main) {
        this.Main = main;
    }

    public String getPhoto() {
        return Photo;
    }

    public void setPhoto(String photo) {
        this.Photo = photo;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        this.Title = title;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        this.Type = type;
    }

    public String getUser() {
        return User;
    }

    public void setUser(String user) {
        this.User = user;
    }

    public String getVideo() {
        return Video;
    }

    public void setVideo(String video) {
        this.Video = video;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(Date);
        dest.writeString(Main);
        dest.writeString(Photo);
        dest.writeString(Title);
        dest.writeString(Type);
        dest.writeString(User);
        dest.writeString(Video);
    }

    public int describeContents() {
        return 0;
    }

}
