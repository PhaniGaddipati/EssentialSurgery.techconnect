package org.techconnect.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.techconnect.R;

import java.io.File;
import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;
import uk.co.senab.photoview.PhotoViewAttacher;

public class ImageViewActivity extends Activity {

    public static final String EXTRA_URL = "url";
    public static final String EXTRA_PATH = "path";
    @Bind(R.id.container)
    FrameLayout container;
    @Bind(R.id.close_btn)
    ImageView closeBtn;

    ImageView imageView;
    GifImageView gifImageView;

    private PhotoViewAttacher attacher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);
        ButterKnife.bind(this);

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageViewActivity.this.finish();
            }
        });
        if (getIntent() != null && getIntent().getStringExtra(EXTRA_PATH) != null) {
            String path = getIntent().getStringExtra(EXTRA_PATH);
            updateImage(path);
        } else if (getIntent() != null && getIntent().getStringExtra(EXTRA_URL) != null) {
            String path = getIntent().getStringExtra(EXTRA_URL);
            updateImageURL(path);
        }
    }

    private void updateImageURL(String url) {
        // Load it as a regular image via picasso
        imageView = new ImageView(this);
        container.addView(imageView);
        attacher = new PhotoViewAttacher(imageView);
        Picasso.with(this)
                .load(url)
                .into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        attacher.update();
                    }

                    @Override
                    public void onError() {
                        imageView.setImageResource(android.R.drawable.stat_notify_error);
                        attacher.update();
                    }
                });
    }

    private void updateImage(String path) {
        boolean isGif;
        GifDrawable gifDrawable = null;
        try {
            gifDrawable = new GifDrawable(path);
            isGif = true;
        } catch (IOException e) {
            // Fails if it's not a gif
            isGif = false;
        }

        if (isGif) {
            gifImageView = new GifImageView(this);
            attacher = new PhotoViewAttacher(gifImageView);
            gifImageView.setImageDrawable(gifDrawable);
            container.addView(gifImageView);
        } else {
            // Load it as a regular image via picasso
            imageView = new ImageView(this);
            container.addView(imageView);
            attacher = new PhotoViewAttacher(imageView);
            Picasso.with(this)
                    .load(new File(path))
                    .into(imageView, new Callback() {
                        @Override
                        public void onSuccess() {
                            attacher.update();
                        }

                        @Override
                        public void onError() {
                            imageView.setImageResource(android.R.drawable.stat_notify_error);
                            attacher.update();
                        }
                    });
        }
    }
}
