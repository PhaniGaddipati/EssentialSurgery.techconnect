package org.techconnect.activities;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.ScrollBar;
import com.github.barteksc.pdfviewer.exception.FileNotFoundException;
import com.github.barteksc.pdfviewer.listener.OnErrorListener;

import org.techconnect.R;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import butterknife.Bind;
import butterknife.ButterKnife;

public class PDFActivity extends AppCompatActivity {

    public static final String EXTRA_FILE = "file";
    public static final String EXTRA_URI = "uri";
    public static final String EXTRA_IS_FILE = "isFile";

    @Bind(R.id.pdfView)
    PDFView pdfView;
    @Bind(R.id.scrollBar)
    ScrollBar scrollBar;
    @Bind(R.id.close_btn)
    ImageView closeBtn;
    @Bind(R.id.errorImageView)
    ImageView errorImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf);
        ButterKnife.bind(this);

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        pdfView.setScrollBar(scrollBar);
        errorImageView.setVisibility(View.GONE);
        OnErrorListener onErrorListener = new OnErrorListener() {
            @Override
            public void onError(Throwable t) {
                onLoadError();
            }
        };
        try {
            if (getIntent() != null && getIntent().hasExtra(EXTRA_IS_FILE)) {
                if (getIntent().getBooleanExtra(EXTRA_IS_FILE, false)) {
                    String file = getIntent().getStringExtra(EXTRA_FILE);
                    pdfView.fromFile(new File(file)).onError(onErrorListener).load();
                } else {
                    String url = getIntent().getStringExtra(EXTRA_URI);
                    try {
                        pdfView.fromUri(Uri.parse(new URL(url).toURI().toString())).onError(onErrorListener).load();
                    } catch (URISyntaxException | MalformedURLException e) {
                        e.printStackTrace();
                        onLoadError();
                    }
                }
            }
        } catch (FileNotFoundException ex) {
            onLoadError();
        }
    }

    private void onLoadError() {
        pdfView.setVisibility(View.GONE);
        scrollBar.setVisibility(View.GONE);
        errorImageView.setVisibility(View.VISIBLE);
    }
}
