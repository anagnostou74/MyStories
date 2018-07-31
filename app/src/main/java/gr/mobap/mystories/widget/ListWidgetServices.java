package gr.mobap.mystories.widget;

import android.content.Intent;
import android.widget.RemoteViewsService;


public class ListWidgetServices extends RemoteViewsService {
    private static final String TAG = ListWidgetServices.class.getSimpleName();

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new ListViewsFactory(this.getApplicationContext(), intent);
    }
}