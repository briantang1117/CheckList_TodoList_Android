package com.brian.checklist.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.brian.checklist.ListContent;
import com.brian.checklist.ListViewAdapter;
import com.brian.checklist.MyDatabaseDAO;
import com.brian.checklist.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeFragment extends Fragment {
    private ListView listView;
    private MyDatabaseDAO db;
    private ListViewAdapter adapter;
    private List<Map<String, Object>> datalist;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        db = new MyDatabaseDAO(getActivity());
        listView = root.findViewById(R.id.list_view);
        View emptyView = root.findViewById(R.id.empty);
        listView.setEmptyView(emptyView);

        datalist = getData();
        adapter = new ListViewAdapter(getActivity(), datalist);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            HashMap<String, Object> listinfo = (HashMap<String, Object>) listView.getItemAtPosition(position);//SimpleAdapter返回Map
            String listid = listinfo.get("id").toString();

            Intent intent = new Intent();
            intent.setClass(getActivity(), ListContent.class);//this前面为当前activty名称，class前面为要跳转到得activity名称
            intent.putExtra("listid", listid);
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.trans_in, R.anim.no_anim);
        });

        listView.setOnItemLongClickListener((parent, view, position, id) -> {
            HashMap<String, Object> listinfo = (HashMap<String, Object>) listView.getItemAtPosition(position);//SimpleAdapter返回Map
            int listId = (int) listinfo.get("id");
            String name = (String) listinfo.get("title");

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("确认将 " + name + " 移至回收站？");
            builder.setNegativeButton("取消", (dialog, which) -> {
            });
            builder.setPositiveButton("确定", (dialog, which) -> {
                db.updateList(listId, 1);
                refresh();
                //Toast.makeText(ListContent.this, "已移动至回收站", Toast.LENGTH_SHORT).show();
            });
            builder.show();
            return true;
        });

        ImageView bt_search = root.findViewById(R.id.btn_search);

        bt_search.setOnClickListener(v -> Toast.makeText(getActivity(), "正在开发中", Toast.LENGTH_SHORT).show());

        return root;
    }

    //从数据库获取data
    public List<Map<String, Object>> getData() {
        return db.queryList(0);
    }


    @Override
    public void onStart() {
        super.onStart();
        refresh();
    }

    //刷新listview
    public void refresh() {
        //刷新list
        datalist.clear();
        datalist.addAll(getData());
        adapter.notifyDataSetChanged();
    }
}
