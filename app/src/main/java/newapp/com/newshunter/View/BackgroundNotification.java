package newapp.com.newshunter.View;


import android.content.Intent;

import android.util.Log;
import com.onesignal.NotificationExtenderService;
import com.onesignal.OSNotificationReceivedResult;



public class BackgroundNotification extends NotificationExtenderService {
    @Override
    protected boolean onNotificationProcessing(final OSNotificationReceivedResult receivedResult) {
        // Read properties from result.

        Log.d("back notification","in class");


        String url = receivedResult.payload.launchURL;
        Intent mIntent = new Intent(BackgroundNotification.this, NotificationWebview.class);
        mIntent.putExtra("url", url);
        mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(mIntent);


        return false;
    }
}