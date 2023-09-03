package com.example.jhome;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentSearchBar#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentSearchBar extends Fragment {
    private PackageManager manager;
    private List<Item> apps,oldApps;
    private ListView list;

    private ListView suggestionsListView;
    private List<String> suggestions;
    private ArrayAdapter<String> adapter;
    private String searchText;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FragmentSearchBar() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentSearchBar.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentSearchBar newInstance(String param1, String param2) {
        FragmentSearchBar fragment = new FragmentSearchBar();
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
        View rootView = inflater.inflate(R.layout.fragment_search_bar, container, false);
        EditText editText = (EditText) rootView.findViewById(R.id.edit);
        suggestionsListView = rootView.findViewById(R.id.suggestionsListView);
        suggestions = new ArrayList<>();
        oldApps = new ArrayList<>();
        adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, suggestions);
        suggestionsListView.setAdapter(adapter);

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                searchText = charSequence.toString();
                if(!searchText.isEmpty()) {
                    updateSuggestions(searchText);
                    loadApps(removeAccents(searchText));
                    list = (ListView) rootView.findViewById(R.id.list_items);
                    list.setVisibility(View.VISIBLE);
                    ArrayAdapter<Item> adapter = new ArrayAdapter<Item>(requireContext(), R.layout.item, apps) {
                        @NonNull
                        @Override
                        public View getView(int position, @NonNull View convertView, @NonNull ViewGroup parent) {
                            String names = removeAccents((String) apps.get(position).name);
                            System.out.println(names);
                            if (convertView == null) {
                                convertView = getLayoutInflater().inflate(R.layout.item, null);
                            }

                            ImageView appicon = (ImageView) convertView.findViewById(R.id.icon);
                            appicon.setImageDrawable(apps.get(position).icon);
                            TextView appName = (TextView) convertView.findViewById(R.id.name);
                            appName.setText(apps.get(position).name);


                            return convertView;
                        }
                    };
                    list.setAdapter(adapter);

                    addClickListener();
                }
                else{
                    apps = oldApps;
                    list = (ListView) rootView.findViewById(R.id.list_items);
                    ArrayAdapter<Item> adapter = new ArrayAdapter<Item>(requireContext(), R.layout.item, apps) {
                        @NonNull
                        @Override
                        public View getView(int position, @NonNull View convertView, @NonNull ViewGroup parent) {
                            String names = removeAccents((String) apps.get(position).name);
                            System.out.println(names);
                            if (convertView == null) {
                                convertView = getLayoutInflater().inflate(R.layout.item, null);
                            }

                            ImageView appicon = (ImageView) convertView.findViewById(R.id.icon);
                            appicon.setImageDrawable(apps.get(position).icon);
                            TextView appName = (TextView) convertView.findViewById(R.id.name);
                            appName.setText(apps.get(position).name);


                            return convertView;
                        }
                    };
                    list.setAdapter(adapter);
                    addClickListener();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        return rootView;
    }
    private void updateSuggestions(String searchText) {
        if (!searchText.isEmpty()){
            suggestionsListView.setVisibility(View.VISIBLE);
            // Ici, vous pouvez implémenter la logique pour obtenir des suggestions
            // en fonction du texte de recherche. Pour cet exemple, nous utilisons des données
            // statiques.
            suggestions.clear();
            suggestions.add("Internet : "+searchText);
            suggestions.add("Youtube : "+searchText);
            adapter.notifyDataSetChanged();
            suggestionsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    if (position == 0) {
                        intent.setData(Uri.parse("https://www.google.com/search?q=" + Uri.encode(searchText)));
                    }
                    else{
                        intent.setData(Uri.parse("https://www.youtube.com/results?search_query=" + Uri.encode(searchText)));
                    }


                    // Vérifiez si une application de navigateur est disponible pour gérer cette intention
                    if (intent.resolveActivity(requireContext().getPackageManager()) != null) {
                        // Lancez l'intention
                        startActivity(intent);
                    } else {
                        // Aucune application de navigateur n'est disponible, vous pouvez gérer cette situation ici
                    }
                }
            });
        }else {
            suggestionsListView.setVisibility(View.GONE);
        }

    }
    private void loadApps(String search){
        if (!search.trim().isEmpty())
        {
            manager = requireContext().getPackageManager();
            apps = new ArrayList<>();
            Intent i = new Intent(Intent.ACTION_MAIN, null);
            i.addCategory(Intent.CATEGORY_LAUNCHER);
            List<ResolveInfo> availableActivities = manager.queryIntentActivities(i, 0);
            for (ResolveInfo ri : availableActivities) {
                Item app = new Item();
                String name = removeAccents(String.valueOf(ri.loadLabel(manager)));
                if (name.toLowerCase().contains(search.toLowerCase())) {
                    app.label = ri.activityInfo.packageName;
                    app.name = ri.loadLabel(manager);
                    app.icon = ri.loadIcon(manager);
                    apps.add(app);
                }
            }
        }
    }

    private void addClickListener(){
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (oldApps.size()>3){
                    oldApps = new ArrayList<>();
                }
                oldApps.add(apps.get(position));
                Intent i = manager.getLaunchIntentForPackage(apps.get(position).label.toString());
                startActivity(i);
            }
        });
    }
    public static String removeAccents(String input) {
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(normalized).replaceAll("");
    }

}