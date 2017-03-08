package org.techconnect.views;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.techconnect.R;
import org.techconnect.misc.CircleTransform;
import org.techconnect.misc.ResourceHandler;
import org.techconnect.model.User;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by doranwalsten on 11/12/16.
 */
public class UserListItemView extends LinearLayout implements View.OnClickListener {
    @Bind(R.id.user_pic)
    ImageView userImageView;
    @Bind(R.id.user_name)
    TextView user_name;

    User user;

    public UserListItemView(Context context) {
        super(context);
    }

    public UserListItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public UserListItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this);
        updateViews();
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User u) {
        this.user = u;
    }

    public void updateViews() {
        if (this.user == null) {
            user_name.setText("Test Text");
        } else {
            user_name.setText(user.getName());
            if (user.getPic() != null && !TextUtils.isEmpty(user.getPic())) {
                if (ResourceHandler.get(getContext()).hasStringResource(user.getPic())) {
                    // Load offline image
                    Picasso.with(getContext())
                            .load(getContext().getFileStreamPath(
                                    ResourceHandler.get(getContext()).getStringResource(user.getPic())))
                            .fit()
                            .error(R.drawable.ic_account_circle_black_48dp)
                            .transform(new CircleTransform())
                            .into(userImageView);
                } else {
                    // Try to load from online
                    Picasso.with(getContext())
                            .load(user.getPic())
                            .fit()
                            .error(R.drawable.ic_account_circle_black_48dp)
                            .transform(new CircleTransform())
                            .into(userImageView);
                }
            } else {
                Picasso.with(getContext())
                        .load(R.drawable.ic_account_circle_black_48dp)
                        .into(userImageView);
            }
        }
    }


    @Override
    public void onClick(View view) {

    }
}
