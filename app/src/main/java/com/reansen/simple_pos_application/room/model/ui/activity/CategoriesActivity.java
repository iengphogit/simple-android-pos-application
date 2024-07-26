package com.reansen.simple_pos_application.room.model.ui.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.reansen.simple_pos_application.R;
import com.reansen.simple_pos_application.room.model.Navigator;
import com.reansen.simple_pos_application.room.model.adapter.CategoriesAdapter;
import com.reansen.simple_pos_application.room.model.room.POSDatabase;
import com.reansen.simple_pos_application.room.model.room.dao.CategoryDao;
import com.reansen.simple_pos_application.room.model.room.entity.CategoryEntity;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CategoriesActivity extends AppCompatActivity {

    //Todo action..!
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_categories);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button btnNewCategory = findViewById(R.id.btnNewCategory);
        btnNewCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Todo open new category activity
                Intent intent = Navigator.getCreateCategoryActivity(CategoriesActivity.this);
                startActivity(intent);
            }
        });


        bindAdapterData();
    }

    private void bindAdapterData() {

        RecyclerView catRecyclerView = findViewById(R.id.catRecyclerView);

        //Todo get categories from database
        POSDatabase database = POSDatabase.getInstance(this);

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                List<CategoryEntity> categories = database.categoryDao().getAllCategories();

                CategoriesAdapter adapter = new CategoriesAdapter();
                adapter.setCategories(categories);
                adapter.setCategoryListener(new CategoriesAdapter.CategoryListener() {
                    @Override
                    public void onCategoryLongClick(CategoryEntity categoryEntity) {
                        Toast.makeText(CategoriesActivity.this, "Item long clicked: " + categoryEntity.name, Toast.LENGTH_SHORT).show();
                        alertDialog(categoryEntity);
                    }
                });
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        catRecyclerView.setAdapter(adapter);
                    }
                });
            }
        });
    }

    private ActivityResultLauncher<Intent> editLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    bindAdapterData();
                    Toast.makeText(this, "Item updated..!", Toast.LENGTH_SHORT).show();
                }
            });
    private final int EDIT_REQUEST_CODE = 1; // Any unique request code


    private void alertDialog(CategoryEntity categoryEntity) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Set the dialog title (optional)
        builder.setTitle(categoryEntity.name);

        // Set a click listener for the positive button ("Edit item")
        builder.setPositiveButton("Edit item", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent editIntent = new Intent(CategoriesActivity.this, CategoryActivity.class);
                editIntent.putExtra(CategoryActivity.CategoryId, categoryEntity.id);
                editLauncher.launch(editIntent);
            }
        });

        // Set a click listener for the negative button ("Delete item")
        builder.setNegativeButton("Delete item", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Confirmation dialog before deleting (recommended)
                confirmDeleteItem(categoryEntity);
            }
        });

        // Create the AlertDialog and show it
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void confirmDeleteItem(CategoryEntity categoryEntity) {

        POSDatabase database = POSDatabase.getInstance(this);
        CategoryDao categoryDao = database.categoryDao();


        // Create a confirmation dialog builder
        AlertDialog.Builder confirmBuilder = new AlertDialog.Builder(this);

        confirmBuilder.setTitle("Confirm Delete");
        confirmBuilder.setMessage("Are you sure you want to delete this item?");

        ExecutorService executorService = Executors.newSingleThreadExecutor();

        // Set positive button for confirmation
        confirmBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Perform delete operation here
                // For example:
                // deleteItem(itemId); // Replace with your actual delete logic
                executorService.execute(() -> {
                    categoryDao.delete(categoryEntity);
                    bindAdapterData();
                });
                Toast.makeText(CategoriesActivity.this, "Item deleted!", Toast.LENGTH_SHORT).show();
            }
        });

        // Set negative button to dismiss
        confirmBuilder.setNegativeButton("Cancel", null);

        // Create and show the confirmation dialog
        AlertDialog confirmDialog = confirmBuilder.create();
        confirmDialog.show();
    }

}