package com.romantymchyk.omguwreader.resources;

import android.os.AsyncTask;

import com.google.api.services.blogger.Blogger.Comments.List;
import com.google.api.services.blogger.model.CommentList;
import com.romantymchyk.omguwreader.async.AsyncResponse;

public class CommentsResource {

    private static CommentsResource commentsResource;

    public static CommentsResource getInstance() {
        if (commentsResource == null) commentsResource = new CommentsResource();

        return commentsResource;
    }

    public class ListComments extends AsyncTask<String, Void, CommentList> {

        private AsyncResponse callback;

        private Exception exception;

        public void setCallback(AsyncResponse callback) {
            this.callback = callback;
        }

        @Override
        protected void onPostExecute(CommentList result) {
            if (exception != null) callback.processFinishWithError();
            else if (callback != null) callback.processFinish(result);
        }

        @Override
        protected CommentList doInBackground(String... args) {
            BloggerHelper bloggerHelper = BloggerHelper.getInstance();

            String blogId = args[0];
            String postId = args[1];

            try {
                List commentsListAction = bloggerHelper.getBlogger().comments().list(blogId, postId);

                commentsListAction.setKey(BloggerHelper.API_KEY);
                commentsListAction.setFields("items(id,author/displayName,content,inReplyTo)");
                commentsListAction.setMaxResults((long) 50);

                return commentsListAction.execute();
            }
            catch (Exception e) {
                exception = e;
            }

            return null;
        }
        
    }
    
}
