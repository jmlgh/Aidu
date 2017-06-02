package jjv.uem.com.aidu.UI;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import jjv.uem.com.aidu.Model.Community;
import jjv.uem.com.aidu.R;

public class CommunityInfo extends AppCompatActivity {
    private Community community;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community_info);
        initVew();
    }

    private void initVew() {
        community = getIntent().getParcelableExtra(Communities.KEY_COMMUNITY);

    }


}
