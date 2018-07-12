package com.mall.flickrdownloadtags.ViewFragments;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.mall.flickrdownloadtags.R;


public class LoadingFragment extends Dialog implements View.OnClickListener {

    private String loadingText;
    private ProgressBar spinner;
    private TextView dialogText;

    public LoadingFragment(@NonNull Context context, String loadingText) {
        super(context);
        this.loadingText = loadingText;
        this.setCanceledOnTouchOutside(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_fragment_loading);

        spinner = (ProgressBar) findViewById(R.id.progress_spinner);
        dialogText = (TextView) findViewById(R.id.loading_text);

        dialogText.setText(loadingText);


    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }

    @Override
    public void onClick(View view) {

    }

    public void dismissWithDelay(long delay) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                LoadingFragment.this.dismiss();
            }
        }, delay);
    }
}
