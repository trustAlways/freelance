package info.android.technologies.indoreconnect.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import info.android.technologies.indoreconnect.R;

/**
 * Created by kamlesh on 12/7/2017.
 */
public class ContactUsFragment extends Fragment {
    View view;
    TextView contact_tv;

    public ContactUsFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_contactus, null);
        contact_tv = (TextView) view.findViewById(R.id.tv_contact_calling);

        contact_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String contact = contact_tv.getText().toString();
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + contact));
                startActivity(intent);
            }
        });
        return view;
    }
}
