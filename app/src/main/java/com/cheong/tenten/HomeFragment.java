package com.cheong.tenten;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.Profile;
import com.facebook.login.LoginManager;

/**
 * Created by Cheong on 30/6/2016.
 */
public class HomeFragment extends Fragment {

    private TextView tv;
    private Button logoutButton;
    private Profile profile;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.home_fragment, container, false);
        tv = (TextView) view.findViewById(R.id.tv_name);
        logoutButton = (Button) view.findViewById(R.id.logout_button);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Bundle bundle = getArguments();

        if (bundle!=null) {
            profile = (Profile) bundle.getParcelable(LoginFragment.PARCEL_KEY);
        }
        else {
            profile = Profile.getCurrentProfile();
        }

        tv.setText("Welcome, " + profile.getFirstName());

        logoutButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                logout();
            }
        });
    }

    private void logout() {
        LoginManager.getInstance().logOut();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.mainContainer, new LoginFragment());
        fragmentTransaction.commit();
    }
}
