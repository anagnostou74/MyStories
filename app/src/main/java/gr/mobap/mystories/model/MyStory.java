package gr.mobap.mystories.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class MyStory implements Parcelable {

    public String date;
    public String prologue;
    public String body;
    public String epilogue;
    public String photo;
    public String title;
    public String type;
    public String user;
    public String image;
    public String email;
    public Integer favorited = 0;
    public Map<String, Boolean> fav = new HashMap<>();

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
        this.favorited = ((Integer) in.readValue((Integer.class.getClassLoader())));
    }

    public MyStory() {
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
        result.put("favorited", favorited);
        result.put("fav", fav);

        return result;
    }

}
