package org.oo.onchart.ui;


import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import org.oo.onchart.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginTestFragment extends DialogFragment {

    private LoginListener listener = null;
    private EditText usrNumInput;
    private EditText pswInput;
    private Button fetchBtn;

    public LoginTestFragment() {

    }

    public void setListener(LoginListener listener) {
        this.listener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_login_test, container, false);
        usrNumInput = (EditText) rootView.findViewById(R.id.et_num);
        pswInput = (EditText) rootView.findViewById(R.id.et_pwd);
        fetchBtn = (Button) rootView.findViewById(R.id.bt_fetch);

        fetchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(listener != null) {
                    listener.onFinish(usrNumInput.getText().toString(), pswInput.getText().toString());
                    dismiss();
                }
            }
        });
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

        return rootView;
    }


    public interface LoginListener {
        void onFinish(String usrNum, String psw);
    }
}
