package com.romantymchyk.omguwreader.resources;

import android.os.AsyncTask;

import com.google.api.services.blogger.Blogger.Posts.List;
import com.google.api.services.blogger.model.Post;
import com.google.api.services.blogger.model.PostList;
import com.romantymchyk.omguwreader.async.AsyncResponse;

public class PostsResource {

    private static PostsResource postsResource;

    public static PostsResource getInstance() {
        if (postsResource == null) postsResource = new PostsResource();

        return postsResource;
    }

    public class GetMostRecentPost extends AsyncTask<String, Void, Post> {

        private AsyncResponse callback;

        private Exception exception;

        public void setCallback(AsyncResponse callback) {
            this.callback = callback;
        }

        @Override
        protected void onPostExecute(Post result) {
            if (exception != null) callback.processFinishWithError();
            else if (callback != null) callback.processFinish(result);
        }

        @Override
        protected Post doInBackground(String... args) {
            BloggerHelper bloggerHelper = BloggerHelper.getInstance();

            String blogId = args[0];

            List postsListAction;
            try {
                postsListAction = bloggerHelper.getBlogger().posts().list(blogId);

                postsListAction.setFields("items(blog,published)");
                postsListAction.setKey(BloggerHelper.API_KEY);
                postsListAction.setMaxResults((long) 1);
                postsListAction.setOrderBy("published");

                return postsListAction.execute().getItems().get(0);
            }
            catch (Exception e) {
                exception = e;
            }

            return null;
        }
    }

    public class ListPosts extends AsyncTask<String, Void, PostList> {

        private AsyncResponse callback;

        private Exception exception;

        public void setCallback(AsyncResponse callback) {
            this.callback = callback;
        }

        @Override
        protected void onPostExecute(PostList result) {
            if (exception != null) callback.processFinishWithError();
            else if (callback != null) callback.processFinish(result);
        }

        @Override
        protected PostList doInBackground(String... args) {
            BloggerHelper bloggerHelper = BloggerHelper.getInstance();

            String blogId = args[0];
            String pageToken = null;
            if (args.length != 1) pageToken = (String) args[1];

            List postsListAction;
            try {
                postsListAction = bloggerHelper.getBlogger().posts().list(blogId);

                postsListAction.setFields("items(id,content,title,published,replies/totalItems),nextPageToken");
                postsListAction.setKey(BloggerHelper.API_KEY);
                postsListAction.setMaxResults(BloggerHelper.BLOG_PAGE_SIZE);
                if (pageToken != null) postsListAction.setPageToken(pageToken);

                return postsListAction.execute();
            }
            catch (Exception e) {
                exception = e;
            }

            return null;

        }

    }

}
