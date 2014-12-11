package com.romantymchyk.omguwreader.resources;

import android.os.AsyncTask;

import com.google.api.services.blogger.Blogger.Blogs.Get;
import com.google.api.services.blogger.model.Blog;
import com.romantymchyk.omguwreader.async.AsyncResponse;

public class BlogsResource {

    private static BlogsResource blogsResource;

    private Exception exception;

    public static BlogsResource getInstance() {
        if (blogsResource == null) blogsResource = new BlogsResource();

        return blogsResource;
    }

    public class GetById extends AsyncTask<String, Void, Blog> {

        private AsyncResponse callback = null;

        public void setCallBack(AsyncResponse callback) {
            this.callback = callback;
        }

        @Override
        protected void onPostExecute(Blog result) {
            if (exception != null) callback.processFinishWithError();
            else if (callback != null) callback.processFinish(result);
        }

        @Override
        protected Blog doInBackground(String... blogIds) {
            BloggerHelper bloggerHelper = BloggerHelper.getInstance();

            try {
                Get blogGetAction = bloggerHelper.getBlogger().blogs().get(blogIds[0]);

                blogGetAction.setFields("description");
                blogGetAction.setKey(BloggerHelper.API_KEY);

                return blogGetAction.execute();
            }
            catch (Exception e) {
                exception = e;
            }

            return null;
        }
        
    }
    
}
