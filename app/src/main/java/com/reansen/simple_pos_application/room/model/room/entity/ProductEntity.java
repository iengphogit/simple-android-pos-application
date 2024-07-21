package com.reansen.simple_pos_application.room.model.room.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class ProductEntity {
    @PrimaryKey(autoGenerate = true)
    public long id;
    public String name;
    public double price;
    public double quantity;
}