package com.test.input.Activity;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.journeyapps.barcodescanner.ScanOptions;
import com.test.input.Adapter.DraftAdapter;
import com.test.input.Adapter.EquipmentAdapter;
import com.test.input.Adapter.HistoryAdapter;
import com.test.input.Class.DataClass;
import com.test.input.Class.DraftClass;
import com.test.input.Class.HistoryClass;
import com.test.input.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class History extends AppCompatActivity {

    DatabaseReference databaseReferences;
    ValueEventListener eventListeners;
    RecyclerView recyclerViews;
    List<HistoryClass> dataLists;
    HistoryAdapter adapter;
    TextView tvTitle, tvIsEmpty;
    Button btnBack;
    SearchView searchView;

    private boolean isAscendingByName = true;
    private boolean isDescendingByName = false;
    private boolean isAscendingByDate = true;
    private boolean isDescendingByDate = false;
    private SharedPreferences sharedPreferencess;

    @SuppressLint({"WrongViewCast", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        recyclerViews = findViewById(R.id.recyHistorya);
        tvTitle = findViewById(R.id.t_history_title);
        btnBack = findViewById(R.id.btn_backs);
        tvIsEmpty = findViewById(R.id.emptyState);
        searchView = findViewById(R.id.search);
        searchView.clearFocus();

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        fillDataFromIntent();
        dataLists = new ArrayList<>();
        adapter = new HistoryAdapter(History.this, dataLists);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerViews.setLayoutManager(layoutManager);
        recyclerViews.setAdapter(adapter);

        NavigationView navigationView = findViewById(R.id.nav_view);
        sharedPreferencess = getSharedPreferences("sorting_prefss", Context.MODE_PRIVATE);

        String childKey = tvTitle.getText().toString();
        databaseReferences = FirebaseDatabase.getInstance().getReference("History").child(childKey);

        Toolbar toolbar = findViewById(R.id.tool_bar);
        Menu item = toolbar.getMenu();
        MenuItem sortByRecent = item.findItem(R.id.sort_date_ascending);
        MenuItem sortByDate = item.findItem(R.id.sort_date_descending);

        sortByRecent.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem menuItem) {
                isAscendingByDate = true;
                isDescendingByDate = false;
                sortDataByDate();
                return true;
            }
        });

        sortByDate.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem menuItem) {
                isAscendingByDate = false;
                isDescendingByDate = true;
                sortDataByDate();
                return true;
            }
        });

        // Tambahkan ValueEventListener untuk membaca data dari Firebase
        eventListeners = databaseReferences.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dataLists.clear();

                for (DataSnapshot itemSnapshot: snapshot.getChildren()){
                    HistoryClass dataClass = itemSnapshot.getValue(HistoryClass.class);
                    dataClass.setKey(itemSnapshot.getKey());
                    dataLists.add(dataClass);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Penanganan kesalahan jika baca data gagal
                Toast.makeText(History.this, "Failed to read data from Firebase", Toast.LENGTH_SHORT).show();
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                searchList(newText);
                return false;
            }
        });
    }

    private void fillDataFromIntent(){
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            tvTitle.setText(bundle.getString("KodeQR"));
        }
    }

    private void restoreSortingStatus() {
        isAscendingByDate = sharedPreferencess.getBoolean("ascending_by_date", true);
        isDescendingByDate = sharedPreferencess.getBoolean("descending_by_date", false);

        if (isAscendingByDate || isDescendingByDate) {
            sortDataByDate();
        } else {
        }
    }

    private void saveSortingStatus() {
        SharedPreferences.Editor editor = sharedPreferencess.edit();
        editor.putBoolean("ascending_by_date", isAscendingByDate);
        editor.putBoolean("descending_by_date", isDescendingByDate);
        editor.apply();
    }

    private void sortDataByName() {
        Collections.sort(dataLists, new Comparator<HistoryClass>() {
            @Override
            public int compare(HistoryClass data1, HistoryClass data2) {
                if (isAscendingByName) {
                    return data1.getKodeQR().compareTo(data2.getKodeQR());
                } else if (isDescendingByName) {
                    return data2.getKodeQR().compareTo(data1.getKodeQR());
                }
                return 0;
            }
        });
        adapter.notifyDataSetChanged();
        saveSortingStatus();
    }

    private void sortDataByDate() {
        Collections.sort(dataLists, new Comparator<HistoryClass>() {
            DateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy HH.mm.ss", Locale.US);
            @Override
            public int compare(HistoryClass data1, HistoryClass data2) {
                try {
                    Date date1 = dateFormat.parse(data1.getDataDate());
                    Date date2 = dateFormat.parse(data2.getDataDate());
                    if (isAscendingByDate) {
                        return date1.compareTo(date2);
                    } else if (isDescendingByDate) {
                        return date2.compareTo(date1);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return 0;
            }
        });
        adapter.notifyDataSetChanged();
        saveSortingStatus();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.riwayat, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sort_date_ascending:
                isAscendingByDate = true;
                isDescendingByDate = false;
                sortDataByDate();
                return true;
            case R.id.sort_date_descending:
                isAscendingByDate = false;
                isDescendingByDate = true;
                sortDataByDate();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void searchList(String text) {
        ArrayList<HistoryClass> searchList = new ArrayList<>();
        for (HistoryClass dataClass : dataLists) {
            if (dataClass.getUser().toLowerCase().contains(text.toLowerCase())
                    || dataClass.getLokasiTabung().toLowerCase().contains(text.toLowerCase())
                    || dataClass.getJenisAPAR().toLowerCase().contains(text.toLowerCase())
                    || dataClass.getDataDate().toLowerCase().contains(text.toLowerCase())) {
                searchList.add(dataClass);
            }
        }
        adapter.searchDataList(searchList);
        restoreSortingStatus();
    }
}
