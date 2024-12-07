package com.example.worksqlpractice_2

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context, factory: SQLiteDatabase.CursorFactory? )
    : SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION) {
    companion object{
        private val DATABASE_NAME = "PRODUCT_DATABASE"
        private val DATABASE_VERSION = 1
        val TABLE_NAME = "product_table"
        val KEY_ID = "id"
        val KEY_NAME = "name"
        val KEY_PRICE = "price"
        val KEY_WEIGHT = "weight"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val USER_TABLE = ("CREATE TABLE " + TABLE_NAME + " (" +
                KEY_ID + " INTEGER PRIMARY KEY, " +
                KEY_NAME + " TEXT, " +
                KEY_PRICE + " REAL, " +
                KEY_WEIGHT + " REAL)")

        db?.execSQL(USER_TABLE)

    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun addProduct(product: Product){
        val db = this.writableDatabase
        val contentValues = ContentValues()
        //contentValues.put(KEY_ID, product.id)
        contentValues.put(KEY_NAME, product.name)
        contentValues.put(KEY_PRICE, product.price)
        contentValues.put(KEY_WEIGHT, product.weight)
        db.insert(TABLE_NAME, null, contentValues)
        db.close()
    }

    @SuppressLint("Range")
    fun readProduct() : MutableList<Product> {
        val productList: MutableList<Product> = mutableListOf()
        val selectQuery = "SELECT * FROM $TABLE_NAME"
        val db = this.readableDatabase
        var cursor: Cursor? = null
        try {
            cursor = db.rawQuery(selectQuery, null)
        } catch (e: SQLiteException) {
            db.execSQL(selectQuery)
            return productList
        }
        var id: Int
        var name: String
        var price: Double
        var weight: Double
        if(cursor.moveToFirst()){
            do {
                id = cursor.getInt(cursor.getColumnIndex("id"))
                name = cursor.getString(cursor.getColumnIndex("name"))
                price = cursor.getDouble(cursor.getColumnIndex("price"))
                weight = cursor.getDouble(cursor.getColumnIndex("weight"))
                val product = Product(id=id, name=name, price=price, weight= weight )
                productList.add(product)
            }while (cursor.moveToNext())
        }
        return productList
    }

    fun getInfo(): Cursor?{
        val db = this.readableDatabase
        return db.rawQuery("SELECT * FROM $TABLE_NAME", null)
    }

    fun removeAll(){
        val db = this.writableDatabase
        db.delete(TABLE_NAME, null,null)
    }

    fun updateProduct(product: Product){
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(KEY_ID, product.id)
        contentValues.put(KEY_NAME, product.name)
        contentValues.put(KEY_PRICE, product.price)
        contentValues.put(KEY_WEIGHT, product.weight)
        db.update(TABLE_NAME, contentValues, "id=" + product.id, null)
        db.close()
    }

    fun deleteProduct(product: Product){
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(KEY_ID, product.id)
        db.delete(TABLE_NAME,  "id=" + product.id, null)
        db.close()
    }
}