package me.zchang.onchart.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import me.zchang.onchart.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddCourseFragment extends Fragment {

	public AddCourseFragment() {
		// Required empty public constructor
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_add_course, container, false);
	}

}
