package jjv.uem.com.aidu.util;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import jjv.uem.com.aidu.R;
import jjv.uem.com.aidu.UI.MainActivity;

/**
 * Created by javi_ on 11/06/2017.
 */

public class TutorialFragment extends Fragment {
    private static final String BACKGROUND_COLOR = "backgroundColor";
    private static final String PAGE = "page";

    private int mBackgroundColor, mPage;

    public static TutorialFragment newInstance(int backgroundColor, int page) {
        TutorialFragment frag = new TutorialFragment();
        Bundle b = new Bundle();
        b.putInt(BACKGROUND_COLOR, backgroundColor);
        b.putInt(PAGE, page);
        frag.setArguments(b);
        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!getArguments().containsKey(BACKGROUND_COLOR))
            throw new RuntimeException("Fragment must contain a \"" + BACKGROUND_COLOR + "\" argument!");
        mBackgroundColor = getArguments().getInt(BACKGROUND_COLOR);

        if (!getArguments().containsKey(PAGE))
            throw new RuntimeException("Fragment must contain a \"" + PAGE + "\" argument!");
        mPage = getArguments().getInt(PAGE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Select a layout based on the current page
        int layoutResId;
        switch (mPage) {
            case 0:
                layoutResId = R.layout.tutorial_screen_one;
                Log.i("FRAGMENT","CASE 0");
                break;
            case 1:
                layoutResId = R.layout.tutorial_screen_two;
                Log.i("FRAGMENT","CASE 1");
                break;
            case 2:
                layoutResId = R.layout.tutorial_screen_three;
                Log.i("FRAGMENT","CASE 2");
                break;
            case 3:
                layoutResId = R.layout.tutorial_screen_four;
                Log.i("FRAGMENT","CASE 3");
                break;
            case 4:
                layoutResId = R.layout.tutorial_screen_exit;
                Log.i("FRAGMENT","CASE 4");
                break;
            case 5:
                Log.i("FRAGMENT","CASE 5");
                layoutResId = R.layout.tutorial_screen_four;
                Intent i = new Intent(getContext(), MainActivity.class);
                startActivity(i);
                getActivity().finish();
                break;
            default:
                layoutResId = R.layout.tutorial_screen_four;
                getActivity().finish();
        }

        // Inflate the layout resource file
        View view = getActivity().getLayoutInflater().inflate(layoutResId, container, false);

        // Set the current page index as the View's tag (useful in the PageTransformer)
        view.setTag(mPage);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Set the background color of the root view to the color specified in newInstance()
        View background = view.findViewById(R.id.intro_background);
        background.setBackgroundColor(mBackgroundColor);
    }
}
