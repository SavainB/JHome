package com.example.jhome;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Parcelable;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
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
    ArrayList<Item> applist = new ArrayList<>();
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private int selectedPosition = -1; // Variable pour stocker la position de l'élément sélectionné

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
        loadApps();
        loadListView(rootView);
        registerForContextMenu(list);
        addClickListener();
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
            app.manager = manager;
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
        list.setAdapter(adapter);
    }

    private void addClickListener() {
        list.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) contextMenuInfo;
                selectedPosition = info.position; // Stockez la position de l'élément sélectionné
                getActivity().getMenuInflater().inflate(R.menu.menu_popup, contextMenu);
            }
        });

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
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        String itemId = getResources().getResourceEntryName(item.getItemId());
        switch (itemId) {
            case "menu_item_1":
                favoriteapp();
                boolean isAppInList = false;
                for (Item iteme : applist) {
                    if (iteme.name.equals(apps.get(selectedPosition).name)) {
                        isAppInList = true;
                        break; // Sortir de la boucle dès que l'élément est trouvé
                    }
                }
                if (!isAppInList&& selectedPosition != -1 && selectedPosition < apps.size()) {

                    if (applist.size() >= 3){
                        applist.remove(0);
                    }
                    applist.add(apps.get(selectedPosition));
                    // Créez le fragment destinataire
                    HomeFragment destinataireFragment = new HomeFragment();

                    // Attachez le Bundle en tant qu'arguments au fragment destinataire
                    Bundle bundle = new Bundle();
                    bundle.putParcelableArrayList("key", (ArrayList<Item>) applist); // Utilisez putParcelableArrayList pour passer une liste d'objets Item

                    destinataireFragment.setArguments(bundle);
                    // Remplacez le fragment actuel par le fragment destinataire
                    requireActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, destinataireFragment)
                            .addToBackStack(null) // Permet de revenir en arrière avec le bouton Back
                            .commit();
                }


                return true;
            case "menu_item_2":
                // Action à effectuer pour l'option 2
                Toast.makeText(getContext(), "Option 2 sélectionnée", Toast.LENGTH_SHORT).show();
                return true;
            // Ajoutez d'autres cas pour les autres éléments de menu si nécessaire
            default:
                return super.onContextItemSelected(item);
        }
    }




    @Override
    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v, @Nullable ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        requireActivity().getMenuInflater().inflate(R.menu.menu_popup, menu);

    }
    public void favoriteapp(){
        Bundle arguments = getArguments();
        if (arguments != null) {
            // Récupérez la liste d'objets Item du Bundle
            ArrayList<Item> myItems = arguments.getParcelableArrayList("keye");
            if (myItems != null) {
                // Utilisez la liste d'objets Item comme vous le souhaitez
                applist = myItems;
            }
        }

    }
}
