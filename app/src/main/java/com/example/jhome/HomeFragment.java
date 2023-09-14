package com.example.jhome;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
    private ListView list;
    ArrayList<Item> applist = new ArrayList<>();
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        list = rootView.findViewById(R.id.list_items_app);
        // Récupérez les arguments du Bundle
        Bundle arguments = getArguments();
        if (arguments != null) {
            // Récupérez la liste d'objets Item du Bundle
            ArrayList<Item> myItems = arguments.getParcelableArrayList("key");
            if (myItems != null) {
                // Utilisez la liste d'objets Item comme vous le souhaitez
                applist = myItems;
                // Mettez à jour l'adaptateur de la liste
                ArrayAdapter<Item> adapter = new ArrayAdapter<Item>(requireContext(), R.layout.item, applist) {
                    @NonNull
                    @Override
                    public View getView(int position, @NonNull View convertView, @NonNull ViewGroup parent) {
                        String names = applist.get(position).name.toString();
                        if (convertView == null) {
                            convertView = getLayoutInflater().inflate(R.layout.item, null);
                        }
                        ImageView appicon = (ImageView) convertView.findViewById(R.id.icon);
                        appicon.setImageDrawable(applist.get(position).icon);
                        TextView appName = (TextView) convertView.findViewById(R.id.name);
                        appName.setText(applist.get(position).name);

                        return convertView;
                    }
                };
                list.setAdapter(adapter);
                addClickListener();
            }

        }

        Button button = rootView.findViewById(R.id.button2);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentListApps destinataireFragment = new FragmentListApps();

                // Attachez le Bundle en tant qu'arguments au fragment destinataire
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("keye", (ArrayList<Item>) applist); // Utilisez putParcelableArrayList pour passer une liste d'objets Item
                destinataireFragment.setArguments(bundle);

                // Remplacez le fragment actuel par destinataireFragment
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, destinataireFragment)
                        .addToBackStack(null) // Permet de revenir en arrière avec le bouton Back
                        .commit();
            }

        });

        return rootView;
    }


    private void addClickListener(){
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = applist.get(position).manager.getLaunchIntentForPackage(applist.get(position).label.toString());
                startActivity(i);
            }
        });
    }
}