package me.zchang.onchart.ui;


import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

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

public class DonateFragment extends DialogFragment {

    public DonateFragment() {
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setPositiveButton(getString(R.string.button_donate), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = getActivity().getPackageManager().getLaunchIntentForPackage("com.eg.android.AlipayGphone");
                ClipboardManager clipboardManager = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData data = ClipData.newPlainText(ClipDescription.MIMETYPE_TEXT_PLAIN, getString(R.string.alipay_account));
                clipboardManager.setPrimaryClip(data);
                if (intent != null) {
                    Toast.makeText(getActivity(), getString(R.string.alert_clipboard_complete), Toast.LENGTH_LONG).show();
                    startActivity(intent);
                } else {
                    Toast.makeText(getActivity(), getString(R.string.alert_no_alipay), Toast.LENGTH_SHORT).show();
                }
            }
        })
                .setView(getActivity().getLayoutInflater().inflate(R.layout.fragment_donate, null));
        return builder.create();
    }
}
