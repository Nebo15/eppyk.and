package nebo15.eppyk;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by anton on 18/04/16.
 */
public class L10nFragment extends Fragment {
    Button doneButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.frg_l10n, container, false);

        this.doneButton = (Button)v.findViewById(R.id.DoneButton);
        this.doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);

                ft.remove(L10nFragment.this);
                ft.commit();
            }
        });



//        View tv = v.findViewById(R.id.msg);
//        ((TextView)tv).setText("The fragment saves and restores this text.");
//
//        // Retrieve the text editor, and restore the last saved state if needed.
//        mTextView = (TextView)v.findViewById(R.id.saved);
//        if (savedInstanceState != null) {
//            mTextView.setText(savedInstanceState.getCharSequence("text"));
//        }

        return v;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Remember the current text, to restore if we later restart.
    }
}