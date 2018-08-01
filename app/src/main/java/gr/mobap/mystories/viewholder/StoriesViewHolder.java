package gr.mobap.mystories.viewholder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import gr.mobap.mystories.R;
import gr.mobap.mystories.model.MyStory;
import gr.mobap.mystories.utilities.GlideApp;

public class StoriesViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.iv_main_pic)
    public ImageView iv_main_pic;
    @BindView(R.id.title)
    public TextView titleTv;
    @BindView(R.id.star)
    public ImageView star;
    @BindView(R.id.num_stars)
    public TextView num_stars;
    @BindView(R.id.user_photo)
    public CircleImageView user_photo;
    @BindView(R.id.author)
    public TextView author;

    public StoriesViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void setMainImageUrl(Context ctx, String imageUrl) {

        GlideApp.with(ctx)
                .load(imageUrl)
                .fitCenter()
                .into(iv_main_pic);
    }

    public void setTitle(String title) {
        titleTv.setText(title);
    }

    public void setStarImage(Context ctx, String imageUrl) {
        GlideApp.with(ctx)
                .load(imageUrl)
                .into(star);
    }

    public void setStar(Integer star) {
        num_stars.setText(String.valueOf(star));
    }

    public void setUserPhoto(Context ctx, String imageUrl) {
        GlideApp.with(ctx)
                .load(imageUrl)
                .into(user_photo);
    }

    public void setUserName(String userName) {
        author.setText(userName);
    }

    public void bindToPost(MyStory myStory, View.OnClickListener starClickListener) {
        num_stars.setText(String.valueOf(myStory.favorited));
        star.setOnClickListener(starClickListener);
    }
}
