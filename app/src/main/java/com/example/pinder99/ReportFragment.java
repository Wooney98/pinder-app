package com.example.pinder99;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.pinder99.data.ReportEntity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class ReportFragment extends Fragment {
    private RecyclerView recyclerView;
    private Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<ReportEntity> arrayList; /*= new ArrayList<>();*/
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private View rootView;
    private SwipeRefreshLayout swipe;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_report, container, false);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerview_report);
        recyclerView.addItemDecoration(new DividerItemDecoration(rootView.getContext(), 1));
        arrayList = new ArrayList<>();
        swipe = (SwipeRefreshLayout) rootView.findViewById(R.id.view_swipe);
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateLayoutView();
                swipe.setRefreshing(false);
            }
            private void updateLayoutView() {

            }
        });


        layoutManager=new LinearLayoutManager(getActivity());
        ((LinearLayoutManager) layoutManager).setReverseLayout(true);
        ((LinearLayoutManager) layoutManager).setStackFromEnd(true);

        recyclerView.setLayoutManager(layoutManager);


        adapter = new Adapter(new ReportItemClickListener() {
            @Override
            public void clickReportItem(ReportEntity report) {

            }
        });
        recyclerView.setAdapter(adapter);

        Button b = (Button) rootView.findViewById(R.id.report_btn);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ReportItemActivity.class);
                startActivity(intent);
            }
        });
        return rootView;

    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Log.e("onViewCreated", "onViewCreated");
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchReportData();
    }

    public void fetchReportData() {
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference().child("Main_Report");
        databaseReference.limitToLast(10).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //파이어베이스 데이터베이스의 DB를 받아오는 곳
                arrayList.clear(); //기존 배열 초기화

                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                /*  arrayList.add(0,snapshot.getValue(ReportEntity.class));
                    DataSnapshot snapshot 대충 포문에 시작 int i = 0과같음 빈 공간으로 스냅샷 선언
                    dataSnapshot.getChildren() 서버로부터 받은 레퍼런스 i < dataSnapshot.getChildren() 요런느낌
                    즉 snapshot에 데이터를 하나씩 넣어서 dataSnapshot.getChildren()의 개수까지 반복*/
                    ReportEntity r = snapshot.getValue(ReportEntity.class);
                    arrayList.add(r);
                }
                adapter.reportList = arrayList;
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("ReportFragment", String.valueOf(error.toException()));
            }
        });
    }
}

