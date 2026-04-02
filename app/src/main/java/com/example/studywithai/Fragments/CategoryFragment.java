package com.example.studywithai.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.studywithai.Activities.Categories.AddActivity;
import com.example.studywithai.Adapters.CategoryListAdapter;
import com.example.studywithai.Models.CategoryModel;
import com.example.studywithai.R;
import com.example.studywithai.Repositories.CategoryRepository;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CategoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CategoryFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ArrayList<CategoryModel> categoryModelArrayList;
    private CategoryListAdapter categoryListAdapter;
    private CategoryModel categoryModel;
    private CategoryRepository categoryRepository;
    private RecyclerView recyclerView;

    public CategoryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CategoryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CategoryFragment newInstance(String param1, String param2) {
        CategoryFragment fragment = new CategoryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_category, container, false);
        Button btnCreate = view.findViewById(R.id.btnCreateCategory);
        recyclerView     = view.findViewById(R.id.rvCategory);
        categoryModelArrayList = new ArrayList<>(); // mang rong
        categoryRepository     = new CategoryRepository(getActivity());
        categoryModelArrayList = categoryRepository.getListCategories(); // lay DL DB do vao mang
        categoryListAdapter    = new CategoryListAdapter(categoryModelArrayList, getContext());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(categoryListAdapter);

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AddActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }
}