package gr.mobap.mystories.viewholder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import gr.mobap.mystories.R;

public class StoriesViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.title)
    TextView titleTv;
    @BindView(R.id.num_stars)
    TextView num_stars;
    @BindView(R.id.iv_main_pic)
    ImageView iv_main_pic;
    @BindView(R.id.author)
    TextView author;

    public StoriesViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void setTitle(String title) {
        titleTv.setText(title);
    }

    public void setDesc(String desc) {
        num_stars.setText(desc);
    }

    public void setImageUrl(Context ctx, String imageUrl) {
        Picasso.with(ctx).load(imageUrl).into(iv_main_pic);
    }

    public void setUserName(String userName) {
        author.setText(userName);
    }
}
