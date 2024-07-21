package com.reansen.simple_pos_application.room.model;

import android.content.Context;
import android.content.Intent;

import com.reansen.simple_pos_application.room.model.ui.activity.CategoriesActivity;
import com.reansen.simple_pos_application.room.model.ui.activity.CategoryActivity;

public class Navigator {

    public static Intent getCategoriesActivity(Context context){
        return new Intent(context, CategoriesActivity.class);
    }

    public static Intent getCreateCategoryActivity(Context context){
        return new Intent(context, CategoryActivity.class);
    }
}
