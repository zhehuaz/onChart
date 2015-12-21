package me.zchang.onchart.ui;


import android.app.DialogFragment;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import me.zchang.onchart.BuildConfig;
import me.zchang.onchart.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class AboutFragment extends DialogFragment {


    public AboutFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_about, container, false);
        TextView versionText = (TextView) rootView.findViewById(R.id.tv_version);
        versionText.setText(BuildConfig.VERSION_NAME);
        //getDialog().getWindow().requestFeature(STYLE_NO_TITLE);
        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
