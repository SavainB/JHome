package com.example.jhome;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
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

public class AppListActivity extends AppCompatActivity {
    private PackageManager manager;
    private List<Item> apps;
    private ListView list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_list);
        loadApps();
        loadListView();
        addClickListener();
        EditText editText = (EditText) findViewById(R.id.edit);
        Button button = (Button) findViewById(R.id.bouton);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                String EditTextText = removeAccents(editText.getText().toString().toLowerCase(Locale.FRENCH));
                for (Item item: apps){
                    String nameApplication = removeAccents(item.name.toString().toLowerCase(Locale.FRENCH));
                    if (EditTextText.contentEquals(nameApplication) ){
                        ImageView singleItemImage = findViewById(R.id.singleItemImage);
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
    }
    private void loadApps(){
        manager = getPackageManager();
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
    private void loadListView(){
        list = (ListView) findViewById(R.id.list_item);
        ArrayAdapter<Item> adapter = new ArrayAdapter<Item>(this,R.layout.item,apps){
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