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
import android.support.v7.graphics.Palette;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.util.zip.Inflater;

import me.zchang.onchart.R;

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
