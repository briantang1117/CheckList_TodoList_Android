package com.brian.checklist.ui.notifications;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
        ConstraintLayout bt_feedback = root.findViewById(R.id.constraintLayout3_1);
        ConstraintLayout bt_share = root.findViewById(R.id.btn_share);

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
        bt_feedback.setOnClickListener(view -> {
            Uri uri = Uri.parse("mailto:");
            String[] email = {"briantffff@icloud.com"};
            Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
            intent.putExtra(Intent.EXTRA_EMAIL, email);
            intent.putExtra(Intent.EXTRA_SUBJECT, "Feedback:CheckList 问题反馈"); // 主题
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent.createChooser(intent, "请选择邮件应用"));
        });
        bt_share.setOnClickListener(view -> {
            shareText(getActivity(), "“CheckList 清单”下载地址：https://gitee.com/briantang/CheckList_Android/releases");
        });
        return root;
    }

    public static void shareText(Context context, String extraText) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "应用分享");
        intent.putExtra(Intent.EXTRA_TEXT, extraText);//extraText为文本的内容
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//为Activity新建一个任务栈
        context.startActivity(
                Intent.createChooser(intent, "请选择要分享到的应用"));//R.string.action_share同样是标题
    }
}