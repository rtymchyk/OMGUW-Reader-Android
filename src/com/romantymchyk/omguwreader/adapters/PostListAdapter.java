package com.romantymchyk.omguwreader.adapters;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.cardsui.CardAdapter;
import com.afollestad.cardsui.CardBase;

@SuppressWarnings("rawtypes")
public class PostListAdapter extends CardAdapter {

    public PostListAdapter(Activity context, int layoutRes) {
        super(context, layoutRes);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected boolean onProcessThumbnail(ImageView icon, CardBase card) {
        return super.onProcessThumbnail(icon, card);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected boolean onProcessTitle(TextView title, CardBase card, int accentColor) {
        return super.onProcessTitle(title, card, accentColor);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected boolean onProcessContent(TextView content, CardBase card) {
        return super.onProcessContent(content, card);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected boolean onProcessMenu(View view, CardBase card) {
        return super.onProcessMenu(view, card);
    }

    @SuppressWarnings("unchecked")
    @Override
    public View onViewCreated(int index, View recycled, CardBase item) {
        View view = super.onViewCreated(index, recycled, item);

        TextView hint = (TextView) recycled.findViewById(android.R.id.hint);
        if (hint != null) hint.setText(item.getHint());

        return view;
    }
    
}
