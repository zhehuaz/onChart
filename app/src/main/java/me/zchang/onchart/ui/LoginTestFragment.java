package me.zchang.onchart.ui;


import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import me.zchang.onchart.R;

/*
 *    Copyright 2015 Zhehua Chang
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

/**
 * Login Fragment, for test only.
 * TODO replace the fragment
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
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        View rootView = inflater.inflate(R.layout.fragment_login_test, container, false);
//        usrNumInput = (EditText) rootView.findViewById(R.id.et_num);
//        pswInput = (EditText) rootView.findViewById(R.id.et_pwd);
//        fetchBtn = (Button) rootView.findViewById(R.id.bt_fetch);
//
//        fetchBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if(listener != null) {
//                    listener.onLoginInputFinish(usrNumInput.getText().toString(), pswInput.getText().toString());
//                    dismiss();
//                }
//            }
//        });
//        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
//
//        return rootView;
//    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstance) {
        View rootView = getActivity().getLayoutInflater().inflate(R.layout.fragment_login_test, null);
        usrNumInput = (EditText) rootView.findViewById(R.id.et_num);
        pswInput = (EditText) rootView.findViewById(R.id.et_pwd);

        return new AlertDialog.Builder(getActivity())
                .setPositiveButton(getString(R.string.title_login), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (listener != null) {
                            listener.onLoginInputFinish(usrNumInput.getText().toString(), pswInput.getText().toString());
                        }
                    }
                })
                .setView(rootView)
                .create();
        //return super.getDialog();
    }

    public interface LoginListener {
        void onLoginInputFinish(String usrNum, String psw);
    }
}
