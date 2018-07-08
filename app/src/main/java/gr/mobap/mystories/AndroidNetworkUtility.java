package gr.mobap.mystories;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;

public class AndroidNetworkUtility extends AppCompatActivity {

    public boolean isConnected(Context ctx) {
        boolean flag = false;
        ConnectivityManager connectivityManager =
                (ConnectivityManager) ctx.getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            flag = true;
        }
        return flag;
    }
}


//    AndroidNetworkUtility androidNetworkUtility = new AndroidNetworkUtility();
// if (androidNetworkUtility.isConnected(this)) {
//        new TwitterAsyncTask().execute(twitterScreenName,this);
//        } else {
//        Log.v(TAG, "Network not Available!");
//        }
