package gr.mobap.mystories.model;

import android.os.Parcel;
import android.os.Parcelable;


public class MyStory implements Parcelable {

    private String type;
    private String title;
    private String main;
    private String photo;
    private String video;
    private String user;
    private String date;

    public final static Parcelable.Creator<MyStory> CREATOR = new Creator<MyStory>() {


        @SuppressWarnings({
                "unchecked"
        })
        public MyStory createFromParcel(Parcel in) {
            return new MyStory(in);
        }

        public MyStory[] newArray(int size) {
            return (new MyStory[size]);
        }

    };

    protected MyStory(Parcel in) {
        this.type = ((String) in.readValue((String.class.getClassLoader())));
        this.title = ((String) in.readValue((String.class.getClassLoader())));
        this.main = ((String) in.readValue((String.class.getClassLoader())));
        this.photo = ((String) in.readValue((String.class.getClassLoader())));
        this.video = ((String) in.readValue((String.class.getClassLoader())));
        this.user = ((String) in.readValue((String.class.getClassLoader())));
        this.date = ((String) in.readValue((String.class.getClassLoader())));
    }

    /**
     * No args constructor for use in serialization
     */
    public MyStory() {
    }


    public MyStory(String type, String title, String main, String photo, String video, String user, String date) {
        super();
        this.type = type;
        this.title = title;
        this.main = main;
        this.photo = photo;
        this.video = video;
        this.user = user;
        this.date = date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(type);
        dest.writeValue(title);
        dest.writeValue(main);
        dest.writeValue(photo);
        dest.writeValue(video);
        dest.writeValue(user);
        dest.writeValue(date);
    }

    public int describeContents() {
        return 0;
    }

}
