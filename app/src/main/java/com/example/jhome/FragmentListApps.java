package com.example.jhome;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

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
 * Use the {@link FragmentListApps#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentListApps extends Fragment {
    private PackageManager manager;
    private List<Item> apps;
    private ListView list;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FragmentListApps() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentListApps.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentListApps newInstance(String param1, String param2) {
        FragmentListApps fragment = new FragmentListApps();
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
        View rootView = inflater.inflate(R.layout.fragment_list_apps, container, false);

        Bundle args = getArguments();
        if (args != null) {
            String sharedData = args.getString("key");
            Button button = rootView.findViewById(R.id.bouton);
            button.setText(sharedData);
        }

        loadApps();
        loadListView(rootView);
        addClickListener();
        EditText editText = (EditText) rootView.findViewById(R.id.edit);
        Button button = (Button) rootView.findViewById(R.id.bouton);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                String EditTextText = removeAccents(editText.getText().toString().toLowerCase(Locale.FRENCH));
                for (Item item: apps){
                    String nameApplication = removeAccents(item.name.toString().toLowerCase(Locale.FRENCH));
                    if (EditTextText.contentEquals(nameApplication) ){
                        ImageView singleItemImage = rootView.findViewById(R.id.singleItemImage);
                        singleItemImage.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent i = manager.getLaunchIntentForPackage(item.label.toString());
                                startActivity(i);
                            }
                        });
                        // Mettez à jour les éléments visuels avec les informations de l'élément
                        singleItemImage.setImageDrawable(item.icon);
                        break;
                    }
                }
            }
        });

        return rootView;
    }

    private void loadApps(){
        manager = requireContext().getPackageManager();
        apps = new ArrayList<>();
        Intent i = new Intent(Intent.ACTION_MAIN, null);
        i.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> availableActivities = manager.queryIntentActivities(i,0);
        for (ResolveInfo ri : availableActivities){
            Item app = new Item();
            app.label = ri.activityInfo.packageName;
            app.name = ri.loadLabel(manager);
            app.icon = ri.loadIcon(manager);
            apps.add(app);
        }

    }
    private void loadListView(View rootView){
        list = (ListView) rootView.findViewById(R.id.list_item);
        ArrayAdapter<Item> adapter = new ArrayAdapter<Item>(requireContext(),R.layout.item,apps){
            @NonNull
            @Override
            public View getView(int position,@NonNull View convertView,@NonNull ViewGroup parent){
                if(convertView == null){
                    convertView = getLayoutInflater().inflate(R.layout.item,null);
                }
                ImageView appicon = (ImageView) convertView.findViewById(R.id.icon);
                appicon.setImageDrawable(apps.get(position).icon);
                TextView appName = (TextView) convertView.findViewById(R.id.name);
                appName.setText(apps.get(position).name);
                return convertView;
            }
        };
        System.out.println(adapter);
        list.setAdapter(adapter);
    }

    private void addClickListener(){
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
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
