package com.romantymchyk.omguwreader.adapters;

import java.util.List;

import android.content.Context;
import android.text.Html;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.afollestad.silk.views.text.SilkTextView;
import com.google.api.services.blogger.model.Comment;
import com.romantymchyk.omguwreader.R;

public class PostCommentsAdapter extends ArrayAdapter<Comment> {

    private List<Comment> comments;

    public PostCommentsAdapter(Context context, int resource, List<Comment> comments) {
        super(context, resource, comments);
        this.comments = comments;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            LayoutInflater vi = LayoutInflater.from(getContext());
            view = vi.inflate(R.layout.comment_row, null);
        }

        Comment comment = comments.get(position);
        if (comment != null) {
            TextView title = (TextView) view.findViewById(android.R.id.title);
            TextView content = (SilkTextView) view.findViewById(android.R.id.content);
            TextView hint = (TextView) view.findViewById(android.R.id.hint);
            View commentReplyBlock = view.findViewById(R.id.comment_reply_block);

            if (title != null) title.setText(comment.getAuthor().getDisplayName());
            if (content != null) {
                content.setText(Html.fromHtml(comment.getContent()));
                Linkify.addLinks(content, Linkify.WEB_URLS);
            }
            if (comment.getInReplyTo() != null) commentReplyBlock.setVisibility(View.VISIBLE);
            else commentReplyBlock.setVisibility(View.GONE);
            if (hint != null) hint.setText(comment.getStatus());
        }

        return view;
    }

    public void setItems(List<Comment> comments) {
        super.clear();
        this.addAll(comments);
        this.comments = comments;
    }

}