package org.techconnect.dialogs;

import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.centum.techconnect.R;
import org.techconnect.analytics.FirebaseEvents;
import org.techconnect.model.session.Session;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by phani on 1/9/17.
 */

public class GuideFeedbackDialogFragment extends DialogFragment {

    @Bind(R.id.experience_opt_1)
    View expOpt1;
    @Bind(R.id.experience_opt_2)
    View expOpt2;
    @Bind(R.id.experience_opt_3)
    View expOpt3;
    @Bind(R.id.contact_opt_1)
    View contactOpt1;
    @Bind(R.id.contact_opt_2)
    View contactOpt2;
    @Bind(R.id.contact_opt_3)
    View contactOpt3;
    @Bind(R.id.comments_editText)
    EditText commentsEditText;
    private Session session;
    private String selectedExpFeedback = null;
    private String selectedContactFeedback = null;

    private DialogInterface.OnDismissListener onDismissListener = null;


    public static GuideFeedbackDialogFragment newInstance(Session session) {
        GuideFeedbackDialogFragment frag = new GuideFeedbackDialogFragment();
        frag.setSession(session);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_fragment_guide_feedback, container, false);
        ButterKnife.bind(this, view);

        View.OnClickListener expListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onExperienceOptionClick(v);
            }
        };
        View.OnClickListener contactListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onContactOptionClick(v);
            }
        };
        expOpt1.setOnClickListener(expListener);
        expOpt2.setOnClickListener(expListener);
        expOpt3.setOnClickListener(expListener);
        contactOpt1.setOnClickListener(contactListener);
        contactOpt2.setOnClickListener(contactListener);
        contactOpt3.setOnClickListener(contactListener);

        return view;
    }

    private void onContactOptionClick(View v) {
        contactOpt1.setBackgroundResource(android.R.color.background_light);
        contactOpt2.setBackgroundResource(android.R.color.background_light);
        contactOpt3.setBackgroundResource(android.R.color.background_light);
        v.setBackgroundResource(R.color.colorAccent);
        selectedContactFeedback = ((TextView) v).getText().toString();
    }

    private void onExperienceOptionClick(View v) {
        expOpt1.setBackgroundResource(android.R.color.background_light);
        expOpt2.setBackgroundResource(android.R.color.background_light);
        expOpt3.setBackgroundResource(android.R.color.background_light);
        v.setBackgroundResource(R.color.colorAccent);
        selectedExpFeedback = ((TextView) v).getText().toString();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @OnClick(R.id.submit_button)
    void onSubmit() {
        if (selectedContactFeedback == null || selectedExpFeedback == null) {
            Toast.makeText(getActivity(), R.string.feedback_no_selections, Toast.LENGTH_SHORT).show();
        } else {
            FirebaseEvents.logGuideFeedback(getActivity(), session, selectedExpFeedback, selectedContactFeedback, commentsEditText.getText().toString());
            dismiss();
            getActivity().finish(); //Close the activity that it was a part of
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (onDismissListener != null) {
            onDismissListener.onDismiss(dialog);
        }
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public void setOnDismissListener(DialogInterface.OnDismissListener onDismissListener) {
        this.onDismissListener = onDismissListener;
    }
}
