package gr.mobap.mystories.widget;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

import gr.mobap.mystories.R;
import gr.mobap.mystories.activities.StoriesActivity;
import gr.mobap.mystories.model.MyStory;
import gr.mobap.mystories.utilities.GlideApp;

public class ListViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    private ArrayList<MyStory> myStoryArrayList = new ArrayList<>();
    private Context context;
    private Intent intent;

    public ListViewsFactory(Context applicationContext, Intent intent) {
        this.context = applicationContext;
        this.intent = intent;
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {
        myStoryArrayList.clear();
        // Get data from StoriesActivity
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String json = sharedPreferences.getString(StoriesActivity.WIDGET_MESSAGES_SHAREDPREF, "");
        if (!json.equals("")) {
            Gson gson = new Gson();
            myStoryArrayList = gson.fromJson(json, new TypeToken<ArrayList<MyStory>>() {
            }.getType());
        }
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return myStoryArrayList.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        // loading data on the widget
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.my_stories_widget_item);
        views.setTextViewText(R.id.story_title, myStoryArrayList.get(position).getTitle());
        try {
            Bitmap bitmap = GlideApp.with(context)
                    .asBitmap()
                    .load(myStoryArrayList.get(position).getPhoto())
                    .submit(512, 512)
                    .get();

            views.setImageViewBitmap(R.id.story_img, bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putString("message", myStoryArrayList.get(position).getTitle());
        intent.putExtras(bundle);
        views.setOnClickFillInIntent(R.id.story_title, intent);

        return views;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }
}