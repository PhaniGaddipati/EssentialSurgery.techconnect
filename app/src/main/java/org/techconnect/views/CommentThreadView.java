package org.techconnect.views;

import android.app.AlertDialog;
import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.techconnect.R;
import org.techconnect.adapters.CommentableAdapter;
import org.techconnect.asynctasks.PostCommentAsyncTask;
import org.techconnect.misc.auth.AuthManager;
import org.techconnect.model.Comment;
import org.techconnect.model.Commentable;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Phani on 10/24/2016.
 */

public class CommentThreadView extends LinearLayout {


    @Bind(R.id.comments_headerTextView)
    TextView headerTextView;
    @Bind(R.id.postcomment_linearLayout)
    LinearLayout postcommentLinearLayout;
    @Bind(R.id.comment_editText)
    EditText commentEditText;
    @Bind(R.id.post_button)
    ImageButton imageButton;
    @Bind(R.id.signin_to_comment_textView)
    TextView signinToCommentTextView;
    @Bind(R.id.commentListView)
    ListView commentListView;

    private Commentable commentable;
    private String chartId;
    private CommentableAdapter adapter;

    public CommentThreadView(Context context) {
        super(context);
        //init();
    }

    public CommentThreadView(Context context, AttributeSet attrs) {
        super(context, attrs);
        //init();
    }

    public CommentThreadView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //init();
    }

    private void init() {
        inflate(getContext(), R.layout.comment_thread_view, this);
        ButterKnife.bind(this);
        /*
        this.header = (TextView)findViewById(R.id.header);
        this.description = (TextView)findViewById(R.id.description);
        this.thumbnail = (ImageView)findViewById(R.id.thumbnail);
        this.icon = (ImageView)findViewById(R.id.icon);
        */
    }

    public void setComments(String chartId, Commentable commentable) {
        this.commentable = commentable;
        this.chartId = chartId;
        adapter = new CommentableAdapter(commentable,getContext());
        commentListView.setAdapter(adapter);
        updateViews();

    }

    private void updateViews() {
        removeAllViews();
        if (AuthManager.get(getContext()).hasAuth()) {
            addView(postcommentLinearLayout);
        } else {
            addView(signinToCommentTextView);
        }
        if (commentable.getComments().size() == 0) {
            addView(headerTextView);
        } else {
            addView(commentListView);
        }
    }

    @OnClick(R.id.post_button)
    public void onPostComment() {
        if (!TextUtils.isEmpty(commentEditText.getText().toString().trim())) {
            final Comment comment = new Comment();
            comment.setOwnerId(AuthManager.get(getContext()).getAuth().getUserId());
            comment.setText(commentEditText.getText().toString().trim());
            if (commentable.getParentType().equals(Comment.PARENT_TYPE_VERTEX)) {
                comment.setNodeId(commentable.getId());
            }
            imageButton.setEnabled(false);
            commentEditText.setEnabled(false);
            commentEditText.setText(R.string.posting);
            new PostCommentAsyncTask(getContext(), chartId, comment) {
                @Override
                protected void onPostExecute(Comment postedComment) {
                    if (postedComment != null) {
                        commentable.getComments().add(0, postedComment);
                        commentEditText.setText("");
                        updateViews();
                    } else {
                        new AlertDialog.Builder(getContext())
                                .setTitle(R.string.error)
                                .setMessage(R.string.failed_post_comment)
                                .show();
                        commentEditText.setText(comment.getText());
                    }
                    imageButton.setEnabled(true);
                    commentEditText.setEnabled(true);
                }
            }.execute();
        }
    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this);
    }

}
