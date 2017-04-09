package org.techconnect.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import org.techconnect.R;
import org.techconnect.model.Comment;
import org.techconnect.model.Commentable;
import org.techconnect.views.CommentView;

/**
 * Created by doranwalsten on 4/8/17.
 */

public class CommentableAdapter extends BaseAdapter {

    private Commentable commentList;
    private Context context;
    LayoutInflater inflater;

    public CommentableAdapter(Commentable commentable, Context context) {
        this.commentList = commentable;
        this.context =  context;
        inflater = LayoutInflater.from(context);
    }


    @Override
    public int getCount() {
        return commentList.getComments().size();
    }

    @Override
    public Comment getItem(int i) {
        return commentList.getComments().get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final ViewHolder holder;
        //Convert the comment to a comment view

        if (view == null) {
            holder = new ViewHolder();
            CommentView temp_view = (CommentView) inflater.inflate(R.layout.comment_view,null);
            temp_view.setComment(getItem(i));
            temp_view.setTag(holder);
            view = temp_view;
        } else {
            holder = (ViewHolder) view.getTag();
            ((CommentView) view).setComment(getItem(i));
        }

        return view;
    }

    private static class ViewHolder {
        private View view;
    }

}
