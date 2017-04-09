package org.techconnect.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.techconnect.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by doranwalsten on 4/6/17.
 */

public class InfoView extends RelativeLayout {


    @Bind(R.id.specTextView)
    TextView specTextView;
    @Bind(R.id.valueTextView)
    TextView valueTextView;


    public InfoView(Context context) {
        super(context);
    }

    public InfoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public InfoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this);
    }
}
