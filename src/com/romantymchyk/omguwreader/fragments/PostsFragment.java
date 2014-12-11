package com.romantymchyk.omguwreader.fragments;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Toast;

import com.afollestad.cardsui.Card;
import com.afollestad.cardsui.CardAdapter;
import com.afollestad.cardsui.CardBase;
import com.afollestad.cardsui.CardListView;
import com.afollestad.cardsui.CardListView.CardLongClickListener;
import com.google.api.services.blogger.model.Post;
import com.google.api.services.blogger.model.Post.Replies;
import com.google.api.services.blogger.model.PostList;
import com.romantymchyk.omguwreader.R;
import com.romantymchyk.omguwreader.activities.PostCommentsActivity;
import com.romantymchyk.omguwreader.adapters.PostListAdapter;
import com.romantymchyk.omguwreader.async.AsyncResponse;
import com.romantymchyk.omguwreader.resources.BloggerHelper;
import com.romantymchyk.omguwreader.resources.PostsResource;
import com.romantymchyk.omguwreader.resources.PostsResource.ListPosts;
import com.romantymchyk.omguwreader.utilities.PostCheckerUtilities;

@SuppressWarnings("rawtypes")
public class PostsFragment extends Fragment implements AsyncResponse {

    public final static String BLOG_TYPE_KEY = "blog_type";

    public final static String POST_ID_KEY = "post_id";

    public final static String POST_TITLE_KEY = "post_title";

    public final static String POST_CONTENT_KEY = "post_content";

    public static final String BLOG_ID_KEY = "blog_id";

    private static final String LIST_STATE_KEY = "list_state";

    private String blogType;

    private List<Post> postList = new ArrayList<Post>();

    private boolean appending = false;

    private boolean loading = true;

    private CardListView listView;

    private CardAdapter listViewAdapter;

    private String pageToken = null;

    private Parcelable listState = null;

    private Card selectedCard = null;

    public PostsFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        listViewAdapter = new PostListAdapter(getActivity(), R.layout.post_card);
        listViewAdapter.setAccentColorRes(R.color.holo_purple);
        listViewAdapter.setCardsClickable(true);

        if (getArguments() != null) {
            blogType = getArguments().getString(BLOG_TYPE_KEY);
            loadPosts(null);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        if (listView != null) {
            listState = listView.onSaveInstanceState();
            bundle.putParcelable(LIST_STATE_KEY, listState);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (postList != null) buildPostsList();
        if (listState != null) listView.onRestoreInstanceState(listState);
        listState = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_posts, container, false);

        listView = (CardListView) view.findViewById(R.id.posts_list);
        listView.setEmptyView(view.findViewById(android.R.id.empty));
        listView.addFooterView(inflater.inflate(R.layout.footer, null, true), null, false);
        listView.setOnScrollListener(new PostCardListViewOnScrollListener());

        PostCardClickListener clickListener = new PostCardClickListener();
        listView.setOnCardLongClickListener(clickListener);
        listView.setOnCardClickListener(clickListener);
        registerForContextMenu(listView);

        listView.setAdapter(listViewAdapter);

        return view;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getActivity().getMenuInflater().inflate(R.menu.entry_popup, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        String itemTitle = (String) item.getTitle();

        if (itemTitle.equals("Share")) sharePost();

        return true;
    }

    private void sharePost() {
        if (selectedCard != null) {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);

            String shareMessage = "" + Html.fromHtml(selectedCard.getContent());

            sendIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
            sendIntent.setType("text/plain");
            startActivity(Intent.createChooser(sendIntent, "Share"));
        }
    }

    @SuppressWarnings({ "unchecked" })
    private void buildPostsList() {
        loading = true;
        listViewAdapter.clear();
        for (Post post : postList) {
            listViewAdapter.add(createPostCard(post));
        }
        loading = false;
    }

    @SuppressWarnings("unchecked")
    private void appendToPostsList(List<Post> items) {
        for (Post post : items) {
            listViewAdapter.add(createPostCard(post));
        }
    }

    private Card createPostCard(Post post) {
        String title = post.getTitle();
        String content = post.getContent();
        Replies comments = post.getReplies();

        Card newCard = new Card(BloggerHelper.getLongBlogTypeFromShortType(blogType) + " " + title, content);
        newCard.setHint(String.valueOf(comments.getTotalItems()));
        newCard.setTag(post.getId());

        return newCard;
    }

    @Override
    public void processFinish(Object result) {
        if (result != null) {
            PostList postListWrapper = (PostList) result;

            if (!appending) {
                postList = postListWrapper.getItems();
                pageToken = postListWrapper.getNextPageToken();
                buildPostsList();
                updateMostRecentlyReadDate();
            } else {
                postList.addAll(postListWrapper.getItems());
                pageToken = postListWrapper.getNextPageToken();
                appendToPostsList(postListWrapper.getItems());
                appending = false;
            }
        } else {
            this.postList = new ArrayList<Post>();
            buildPostsList();
        }
    }

    private void updateMostRecentlyReadDate() {
        if (getActivity() != null && postList != null && postList.size() != 0) {
            SharedPreferences mostRecentlyReadDates = PostCheckerUtilities.getMostRecentlyReadDates(getActivity());

            long mostRecentlyReadPostDate = mostRecentlyReadDates.getLong(BloggerHelper.getBlogIdFromType(blogType), 0);
            long newestPostDate = postList.get(0).getPublished().getValue();

            if (newestPostDate > mostRecentlyReadPostDate) {
                SharedPreferences.Editor editor = mostRecentlyReadDates.edit();
                editor.putLong(BloggerHelper.getBlogIdFromType(blogType), newestPostDate);
                editor.commit();
            }
        }
    }

    @Override
    public void processFinishWithError() {
        Toast.makeText(getActivity(), R.string.network_error, Toast.LENGTH_LONG).show();
    }

    protected void loadPosts(String pageToken) {
        PostsResource postsResource = PostsResource.getInstance();
        ListPosts listPosts = postsResource.new ListPosts();
        listPosts.setCallback(this);
        if (pageToken != null) listPosts.execute(BloggerHelper.getBlogIdFromType(blogType), pageToken);
        else listPosts.execute(BloggerHelper.getBlogIdFromType(blogType));
    }

    class PostCardClickListener implements CardLongClickListener, CardListView.CardClickListener {

        @Override
        public void onCardClick(int index, CardBase selectedEntryCard, View view) {
            Post selectedPost = postList.get(index);
            Replies replies = selectedPost.getReplies();
            if (replies.getTotalItems() != 0) {
                openPostCommentsActivity(String.valueOf(selectedEntryCard.getTag()), selectedEntryCard.getTitle(), selectedEntryCard.getContent());
            } else {
                Toast.makeText(getActivity(), "No comments to view in this post!", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public boolean onCardLongClick(int index, CardBase card, View view) {
            selectedCard = (Card) card;

            return false;
        }
    }

    class PostCardListViewOnScrollListener implements OnScrollListener {

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            final int lastItem = firstVisibleItem + visibleItemCount;
            if (!appending && !loading && visibleItemCount != 0 && lastItem == totalItemCount) {
                appending = true;
                loadPosts(pageToken);
            }
        }

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {}
    }

    public void openPostCommentsActivity(String postId, String postTitle, String postContent) {
        Intent openPostCommentsIntent = new Intent(getActivity(), PostCommentsActivity.class);
        openPostCommentsIntent.putExtra(POST_ID_KEY, postId);
        openPostCommentsIntent.putExtra(BLOG_ID_KEY, BloggerHelper.getBlogIdFromType(blogType));
        openPostCommentsIntent.putExtra(POST_TITLE_KEY, postTitle);
        openPostCommentsIntent.putExtra(POST_CONTENT_KEY, postContent);

        startActivity(openPostCommentsIntent);
    }

    public void refreshPosts() {
        loading = true;
        listViewAdapter.clear();
        loadPosts(null);
        loading = false;
    }

    public void scrollToTop() {
        loading = true;
        listView.setSelectionAfterHeaderView();
        loading = false;
    }

}
