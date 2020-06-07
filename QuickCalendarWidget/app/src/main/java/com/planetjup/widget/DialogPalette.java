package com.planetjup.widget;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;

public class DialogPalette extends DialogFragment {
    private static final String TAG = DialogPalette.class.getSimpleName();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setPositiveButton(R.string.dialog_yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.setNegativeButton(R.string.dialog_no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_palette, null);
        builder.setView(view);

        ColorPalette palette = (ColorPalette) view.findViewById(R.id.palette);
//        palette.setColors(mColors);
//        palette.setSelectedColor(mSelectedColor);
//        palette.setOnColorSelectedListener(this);
//        if (mOutlineWidth != 0) {
//            palette.setOutlineWidth(mOutlineWidth);
//        }
//        if (mFixedColumnCount > 0) {
//            palette.setFixedColumnCount(mFixedColumnCount);
//        }



        return builder.create();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    
}
