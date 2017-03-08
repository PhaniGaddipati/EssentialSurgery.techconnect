package org.techconnect.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.centum.techconnect.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by doranwalsten on 1/9/17.
 */

public class CategoryListItemView extends RelativeLayout {

    @Bind(R.id.titleTextView)
    TextView titleTextView;
    @Bind(R.id.countTextView)
    TextView countTextView;
    private String title;
    private String count;

    public CategoryListItemView(Context context) {
        super(context);
    }

    public CategoryListItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CategoryListItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this);
        updateViews();
    }

    public void updateViews() {
        if (this.title == null) {
            titleTextView.setText("Placeholder text");
        } else {
            titleTextView.setText(title);
        }

        if (this.count == null) {
            countTextView.setText("Placeholder text");
        } else {
            countTextView.setText(count);
        }
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }
}
