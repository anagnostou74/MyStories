package gr.mobap.mystories.viewholder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import gr.mobap.mystories.R;
import gr.mobap.mystories.utilities.GlideApp;

public class StoriesViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.iv_main_pic)
    ImageView iv_main_pic;
    @BindView(R.id.title)
    TextView titleTv;
    @BindView(R.id.star)
    ImageView star;
    @BindView(R.id.num_stars)
    TextView num_stars;
    @BindView(R.id.user_photo)
    ImageView userPhoto;
    @BindView(R.id.author)
    TextView author;

    public StoriesViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void setMainImageUrl(Context ctx, String imageUrl) {
        GlideApp.with(ctx)
                .load(imageUrl)
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

    public void setStar(String star) {
        num_stars.setText(star);
    }

    public void setUserPhoto(Context ctx, String imageUrl) {
        GlideApp.with(ctx)
                .load(imageUrl)
                .into(userPhoto);
    }

    public void setUserName(String userName) {
        author.setText(userName);
    }
}
