package com.brian.checklist.ui.notifications;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.brian.checklist.R;
import com.brian.checklist.about;

public class NotificationsFragment extends Fragment {


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_notifications, container, false);

        ConstraintLayout bt_about = root.findViewById(R.id.Btn_About);
        bt_about.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.setClass(getActivity(), about.class);//this前面为当前activty名称，class前面为要跳转到得activity名称
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.trans_in,R.anim.no_anim);
        });

        return root;
    }
}