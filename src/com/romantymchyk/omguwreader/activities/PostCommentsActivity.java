package com.romantymchyk.omguwreader.activities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.api.services.blogger.model.Comment;
import com.google.api.services.blogger.model.Comment.InReplyTo;
import com.google.api.services.blogger.model.CommentList;
import com.romantymchyk.omguwreader.R;
import com.romantymchyk.omguwreader.adapters.PostCommentsAdapter;
import com.romantymchyk.omguwreader.async.AsyncResponse;
import com.romantymchyk.omguwreader.fragments.PostsFragment;
import com.romantymchyk.omguwreader.resources.CommentsResource;
import com.romantymchyk.omguwreader.resources.CommentsResource.ListComments;

public class PostCommentsActivity extends Activity implements AsyncResponse {

    private String blogId;

    private String postId;

    private String postContent;

    private String postTitle;

    private List<Comment> commentList = new ArrayList<Comment>();

    ListView listView;

    PostCommentsAdapter listViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_post_comments);
        setupActionBar();

        Intent intent = getIntent();
        postId = intent.getStringExtra(PostsFragment.POST_ID_KEY);
        blogId = intent.getStringExtra(PostsFragment.BLOG_ID_KEY);
        postTitle = intent.getStringExtra(PostsFragment.POST_TITLE_KEY);
        postContent = intent.getStringExtra(PostsFragment.POST_CONTENT_KEY);

        setTitle(postTitle);

        listView = (ListView) findViewById(android.R.id.list);

        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View header = layoutInflater.inflate(R.layout.post_header, null);
        TextView headerTextView = (TextView) header.findViewById(android.R.id.content);
        headerTextView.setMovementMethod(LinkMovementMethod.getInstance());

        postContent = postContent.replaceAll("purple", "white");
        postContent = postContent.replaceAll("<span.*style=\"font-weight: bold;\">(.*)</span>(<br />)?", "");
        headerTextView.setText(Html.fromHtml(postContent));

        listView.addHeaderView(header);

        listViewAdapter = new PostCommentsAdapter(this, R.layout.comment_row, commentList);
        listView.setAdapter(listViewAdapter);
        listView.setVisibility(View.GONE);

        loadComments();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void setupActionBar() {
        ActionBar actionBar = getActionBar();

        actionBar.setDisplayHomeAsUpEnabled(true);

        int titleId = getResources().getIdentifier("action_bar_title", "id", "android");
        TextView actionBarTextView = (TextView) findViewById(titleId);
        Typeface typeFace = Typeface.createFromAsset(getAssets(), "Roboto-Bold.ttf");
        actionBarTextView.setTypeface(typeFace);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.post_comments, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_top:
                scrollToTop();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void scrollToTop() {
        listView.setSelection(0);
    }

    private void buildCommentsList() {
        listView.setVisibility(View.VISIBLE);
        listViewAdapter.setItems(commentList);

        listView.setEmptyView(findViewById(android.R.id.empty));
        listView.getEmptyView().setVisibility(View.GONE);
        listViewAdapter.notifyDataSetChanged();
    }

    @Override
    public void processFinish(Object result) {
        if (result != null) {
            CommentList commentListWrapper = (CommentList) result;
            List<Comment> commentList = commentListWrapper.getItems();

            if (commentList != null) {
                HashMap<String, List<Comment>> commentRepliesToId = new HashMap<String, List<Comment>>();
                List<Comment> baseComments = new ArrayList<Comment>();

                for (Comment comment : commentList) {
                    InReplyTo inReplyToComment = comment.getInReplyTo();

                    if (inReplyToComment != null) {
                        String inReplyToId = inReplyToComment.getId();

                        List<Comment> replies = commentRepliesToId.get(inReplyToId);
                        if (replies == null) {
                            replies = new ArrayList<Comment>();
                            commentRepliesToId.put(inReplyToId, replies);
                        }
                        replies.add(comment);
                    } else {
                        baseComments.add(comment);
                    }
                }

                List<Comment> nestedCommentList = new ArrayList<Comment>();
                int baseCommentIndex = 1;
                for (Comment comment : baseComments) {
                    comment.setStatus(String.valueOf(baseCommentIndex));
                    nestedCommentList.add(comment);

                    List<Comment> repliesToBaseComment = commentRepliesToId.get(comment.getId());
                    if (repliesToBaseComment != null) {
                        String baseReplyCommentIndex = String.valueOf(baseCommentIndex);
                        char replyCommentIndex = 'a';

                        for (Comment replyComment : repliesToBaseComment) {
                            replyComment.setStatus(baseReplyCommentIndex + replyCommentIndex);
                            nestedCommentList.add(replyComment);

                            if (replyCommentIndex == 'z') {
                                replyCommentIndex = 'a';
                                baseReplyCommentIndex += "a";
                            } else {
                                replyCommentIndex++;
                            }
                        }
                    }

                    baseCommentIndex++;
                }

                this.commentList = nestedCommentList;
            } else {
                Toast.makeText(this, "Oops! It looks like all the comments here have been deleted (you should probably go back).", Toast.LENGTH_LONG).show();
            }
        }

        buildCommentsList();
    }

    protected void loadComments() {
        CommentsResource commentsResource = CommentsResource.getInstance();
        ListComments listComments = commentsResource.new ListComments();
        listComments.setCallback(this);
        listComments.execute(blogId, postId);
    }

    @Override
    public void processFinishWithError() {
        Toast.makeText(this, R.string.network_error, Toast.LENGTH_LONG).show();
    }

}
