package org.techconnect.dialogs;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

import org.techconnect.R;

/**
 * Created by Phani on 11/13/2016.
 */

public class SendFeedbackDialogFragment extends DialogFragment {

    private FeedbackListener listener;

    public void setListener(FeedbackListener listener) {
        this.listener = listener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final EditText editText = new EditText(getActivity());
        editText.setInputType(InputType.TYPE_CLASS_TEXT);
        editText.setSingleLine(false);
        editText.setImeOptions(EditorInfo.IME_FLAG_NO_ENTER_ACTION);
        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.action_post_app_feedback)
                .setView(editText)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (listener != null) {
                            listener.onYes(editText.getText().toString());
                        }
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (listener != null) {
                            listener.onNo();
                        }
                    }
                })
                .create();
    }

    public interface FeedbackListener {
        void onYes(String feedback);

        void onNo();
    }
}
