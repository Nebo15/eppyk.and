package nebo15.eppyk;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import nebo15.eppyk.data_api.L10N;
import nebo15.eppyk.managers.UpdateManager;

/**
 * Created by anton on 18/04/16.
 */
public class L10nFragment extends Fragment {
    public L10N[] data;

    Button doneButton;
    ListView l10nList;
    L10nAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.frg_l10n, container, false);

        this.doneButton = (Button)v.findViewById(R.id.DoneButton);
        this.doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateManager.getInstance().loadAnswers(adapter.selectedCode);

                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);

                ft.remove(L10nFragment.this);
                ft.commit();



            }
        });

        this.l10nList = (ListView)v.findViewById(R.id.l10nList);
        String currentL10n = UpdateManager.getInstance().getCurrentL10N();
        adapter = new L10nAdapter(this.getActivity(), this.data, currentL10n);
        this.l10nList.setAdapter(adapter);

        return v;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Remember the current text, to restore if we later restart.
    }
}