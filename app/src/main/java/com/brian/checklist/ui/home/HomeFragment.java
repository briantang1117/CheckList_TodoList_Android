package com.brian.checklist.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.brian.checklist.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeFragment extends Fragment {
    private ListView listView;
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_home , container, false);
        listView = (ListView)view.findViewById(R.id.list_view);
        List<Map<String, Object>> list=getData();
        listView.setAdapter(new ListViewAdapter(getActivity(), list));
        return view;
    }

    public List<Map<String, Object>> getData(){
        List<Map<String, Object>> list=new ArrayList<Map<String,Object>>();
        for (int i = 0; i < 20; i++) {
            Map<String, Object> map=new HashMap<String, Object>();
            map.put("image", R.drawable.ic_searchicon);
            map.put("title", "菜单标题 "+ i);
            map.put("info", "详细信息 " + i);
            list.add(map);
        }
        return list;
    }
}
