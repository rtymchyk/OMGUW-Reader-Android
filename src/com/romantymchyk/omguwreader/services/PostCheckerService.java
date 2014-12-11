package com.romantymchyk.omguwreader.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import com.google.api.services.blogger.model.Post;
import com.romantymchyk.omguwreader.R;
import com.romantymchyk.omguwreader.activities.HomeActivity;
import com.romantymchyk.omguwreader.async.AsyncResponse;
import com.romantymchyk.omguwreader.resources.BloggerHelper;
import com.romantymchyk.omguwreader.resources.PostsResource;
import com.romantymchyk.omguwreader.resources.PostsResource.GetMostRecentPost;
import com.romantymchyk.omguwreader.utilities.PostCheckerUtilities;

public class PostCheckerService extends Service implements AsyncResponse {

    private int blogsChecked;

    private String notificationMessage;

    @Override
    public void onCreate() {
        super.onCreate();

        blogsChecked = 0;
        notificationMessage = "";

        PostsResource postsResource = PostsResource.getInstance();

        GetMostRecentPost getMostRecentPost = postsResource.new GetMostRecentPost();
        getMostRecentPost.setCallback(this);
        getMostRecentPost.execute(BloggerHelper.BLOG_OMG_ID);

        getMostRecentPost = postsResource.new GetMostRecentPost();
        getMostRecentPost.setCallback(this);
        getMostRecentPost.execute(BloggerHelper.BLOG_MC_ID);

        getMostRecentPost = postsResource.new GetMostRecentPost();
        getMostRecentPost.setCallback(this);
        getMostRecentPost.execute(BloggerHelper.BLOG_OH_ID);

        getMostRecentPost = postsResource.new GetMostRecentPost();
        getMostRecentPost.setCallback(this);
        getMostRecentPost.execute(BloggerHelper.BLOG_ASK_ID);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void processFinish(Object result) {
        blogsChecked++;

        if (result != null) {
            Post newestPost = (Post) result;

            SharedPreferences mostRecentlyReadDates = PostCheckerUtilities.getMostRecentlyReadDates(this);

            long newestPostDate = newestPost.getPublished().getValue();
            long mostRecentlyReadPostDate = mostRecentlyReadDates.getLong(newestPost.getBlog().getId(), 0);
            if (mostRecentlyReadPostDate != 0 && mostRecentlyReadPostDate < newestPostDate) {
                SharedPreferences.Editor editor = mostRecentlyReadDates.edit();
                editor.putLong(newestPost.getBlog().getId(), newestPostDate);
                editor.commit();

                String blogType = BloggerHelper.getShortBlogTypeFromId(newestPost.getBlog().getId());

                if (notificationMessage.equals("")) {
                    notificationMessage += "Updated: " + blogType;
                } else {
                    notificationMessage += ", " + blogType;
                }
            }
        }

        notifyUserIfFullCheckComplete();
        stopSelf();
    }

    private void notifyUserIfFullCheckComplete() {
        if (blogsChecked == 4 && !notificationMessage.equals("")) {
            notifyUserViaNotification();
        }
    }

    private void notifyUserViaNotification() {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this).setSmallIcon(R.drawable.ic_launcher_text).setContentTitle(notificationMessage)
                .setContentText(PostCheckerUtilities.notificationContent).setSubText(PostCheckerUtilities.notificationSubContent);

        Intent resultIntent = new Intent(this, HomeActivity.class);
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        mBuilder.setContentIntent(resultPendingIntent);

        int mNotificationId = 001;
        NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        Notification notification = mBuilder.build();
        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        mNotifyMgr.notify(mNotificationId, notification);
    }

    @Override
    public void processFinishWithError() {
        blogsChecked++;
        notifyUserIfFullCheckComplete();
        stopSelf();
    }

}
