package com.brian.checklist.ui.notifications;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.brian.checklist.R;
import com.brian.checklist.about;
import com.brian.checklist.archive;
import com.brian.checklist.trash;

public class NotificationsFragment extends Fragment {


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_notifications, container, false);

        ConstraintLayout bt_about = root.findViewById(R.id.Btn_About);
        ConstraintLayout bt_trash = root.findViewById(R.id.btn_Trash);
        ConstraintLayout bt_archive = root.findViewById(R.id.btn_Archive);
        bt_about.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.setClass(getActivity(), about.class);//this前面为当前activty名称，class前面为要跳转到得activity名称
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.trans_in, R.anim.no_anim);
        });
        bt_trash.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.setClass(getActivity(), trash.class);
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.trans_in, R.anim.no_anim);
        });
        bt_archive.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.setClass(getActivity(), archive.class);
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.trans_in, R.anim.no_anim);
        });

        return root;
    }
}