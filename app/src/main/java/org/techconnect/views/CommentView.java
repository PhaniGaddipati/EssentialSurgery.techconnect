package org.techconnect.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.techconnect.R;
import org.techconnect.misc.Utils;
import org.techconnect.model.Comment;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Phani on 10/24/2016.
 */

public class CommentView extends LinearLayout {

    @Bind(R.id.profilePic)
    ImageView profilePicImageView;
    @Bind(R.id.author_textView)
    TextView authorTextView;
    @Bind(R.id.post_date_textView)
    TextView postDateTextView;
    @Bind(R.id.comment_textView)
    TextView commentTextView;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d, yyyy");
    private Comment comment = null;

    public CommentView(Context context) {
        super(context);
    }

    public CommentView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CommentView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public Comment getComment() {
        return comment;
    }

    public void setComment(Comment comment) {
        this.comment = comment;
        updateViews();
    }

    private void updateViews() {
        if (comment == null) {
            authorTextView.setText("");
            postDateTextView.setText("");
            commentTextView.setText("");
        } else {
            authorTextView.setText(comment.getOwnerName());
            commentTextView.setText(comment.getText());
            try {
                postDateTextView.setText(dateFormat.format(Utils.parseISO8601Date(comment.getCreatedDate())));
            } catch (ParseException e) {
                postDateTextView.setText("");
                e.printStackTrace();
            }
        }

    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this);
    }
}
