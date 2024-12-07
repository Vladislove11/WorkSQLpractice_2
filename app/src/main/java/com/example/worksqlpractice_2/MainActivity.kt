package com.example.worksqlpractice_2

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.AdapterView
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.collection.emptyLongSet
import androidx.core.text.isDigitsOnly
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    private val db = DBHelper(this, null)
    private var listProductAdapter: ListAdapter<Product>? = null

    private lateinit var nameProductET: EditText
    private lateinit var priceProductET: EditText
    private lateinit var weightProductET: EditText
    private lateinit var saveBTN: Button
    private lateinit var updateBTN: Button
    private lateinit var deleteBTN: Button
    private lateinit var listViewLW: ListView
    private lateinit var toolbar: Toolbar

    private var productList: MutableList<Product> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        title = ""

        init()

        saveBTN.setOnClickListener {
            val name = nameProductET.text.toString()
            val price = priceProductET.text.toString().replace(",",".")
            val weight = weightProductET.text.toString().replace(",",".")

            if (price.toDoubleOrNull() == null || weight.toDoubleOrNull() == null ){
                Toast.makeText(applicationContext, "Не корректно задан вес или цена продукта! (0.0). Попробуйте ещё раз!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val product = Product(1,name, price.toDouble(), weight.toDouble())
            db.addProduct(product)
            Toast.makeText(this, "$name добавлен в чек", Toast.LENGTH_SHORT).show()
            nameProductET.text.clear()
            priceProductET.text.clear()
            weightProductET.text.clear()

            viewDataAdapter()
        }

        updateBTN.setOnClickListener{
            updateRecord()
        }

        deleteBTN.setOnClickListener{
            deleteRecord()
        }

        listViewLW.onItemClickListener =
            AdapterView.OnItemClickListener { parent, v, position, id ->
                val dialogBuilder = AlertDialog.Builder(this)

                val product = listProductAdapter?.getItem(position)
                dialogBuilder.setTitle("Работа с записью")
                dialogBuilder.setMessage("Выберите действие:")
                dialogBuilder.setPositiveButton("Удалить") {_, _ ->
                    deleteRecord(product?.id.toString())
                    listProductAdapter?.remove(product)
                }
                dialogBuilder.setNegativeButton("Изменить"){_, _ ->
                    updateRecord(product?.id.toString())
                }
                dialogBuilder.setNeutralButton("Отмена") {_, _ ->
                    Toast.makeText(applicationContext, "Нажата кнопка 3", Toast.LENGTH_SHORT).show()
                }

                dialogBuilder.create().show()
            }

    }

    private fun init(){
        nameProductET = findViewById(R.id.nameProductET)
        priceProductET = findViewById(R.id.priceProductET)
        weightProductET = findViewById(R.id.weightProductET)
        saveBTN = findViewById(R.id.saveBTN)
        updateBTN = findViewById(R.id.updateBTN)
        deleteBTN = findViewById(R.id.deleteBTN)
        listViewLW = findViewById(R.id.listViewLW)
        viewDataAdapter()
    }

    @SuppressLint("MissingInflatedId")
    private fun updateRecord(id : String = "0") {
        val dialogBilder = AlertDialog.Builder(this)
        val inflater = this.layoutInflater
        var dialogView = inflater.inflate(R.layout.update_dialog, null)
        if (id != "0") {
            dialogView = inflater.inflate(R.layout.update_dialog_from_list, null)
        }
        dialogBilder.setView(dialogView)

        val editId= dialogView.findViewById<EditText>(R.id.updateIdET)
        val editName = dialogView.findViewById<EditText>(R.id.updateNameET)
        val editWeight = dialogView.findViewById<EditText>(R.id.updateWeightET)
        val editPrice = dialogView.findViewById<EditText>(R.id.updatePriceET)

        dialogBilder.setTitle("Обновить запись")
        dialogBilder.setMessage("Введите данные ниже:")
        dialogBilder.setPositiveButton("Обновить"){_,_ ->
            var updateId = ""
            try {
                updateId = editId.text.toString()
            } catch (e : Exception){
                e.stackTrace
            }
            if (id != "0") {
                updateId = id
            }
            val updateName = editName.text.toString()
            val updateWeight = editWeight.text.toString().replace(",",".")
            val updatePrice = editPrice.text.toString().replace(",",".")

            if (updateWeight.toDoubleOrNull() == null || updatePrice.toDoubleOrNull() == null ){
                Toast.makeText(applicationContext, "Не корректно задан вес или цена продукта! (0.0). Попробуйте ещё раз!", Toast.LENGTH_LONG).show()
                return@setPositiveButton
            }

            if (updateId.trim() != "" && updateName.trim() != "" && updatePrice.trim() != "" && updateWeight.trim() != ""){
                val product = Product(Integer.parseInt(updateId), updateName, updateWeight.toDouble(), updatePrice.toDouble())
                db.updateProduct(product)
                viewDataAdapter()
                Toast.makeText(applicationContext, "Данные обновлены", Toast.LENGTH_SHORT).show()
            }
        }

        dialogBilder.setNegativeButton("Отмена"){dialog, which ->
        }

        dialogBilder.create().show()
    }

    private fun deleteRecord(id : String = "0") {
        val dialogBilder = AlertDialog.Builder(this)
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.delete_dialog, null)
        if (id == "0") {
            dialogBilder.setView(dialogView)
        }

        val chooseDeleteId = dialogView.findViewById<EditText>(R.id.deleteIdET)

        dialogBilder.setTitle("Удалить запись")
        var message = ""
        if (id == "0"){
            message = "Введите идентификатор:"
        } else {
            message = "Будет удалена запись: $id"
        }
        dialogBilder.setMessage("$message:")
        dialogBilder.setPositiveButton("Удалить") {_, _ ->
            var deleteId = ""
            if (id == "0") {
                deleteId = chooseDeleteId.text.toString()
            } else {
                deleteId = id
            }
            if (deleteId.trim() != ""){
                val product = Product(Integer.parseInt(deleteId), "", 0.0, 0.0)
                db.deleteProduct(product)
                viewDataAdapter()
                Toast.makeText(applicationContext, "Запись удалена", Toast.LENGTH_SHORT).show()
            }
        }
        if (id == "0"){
            dialogBilder.setNegativeButton("Отмена") { _, _ ->
            }
        }

        dialogBilder.create().show()
    }

    private fun viewDataAdapter() {
        productList = db.readProduct()
        listProductAdapter = ListAdapter(this@MainActivity, productList)
        listViewLW.adapter = listProductAdapter
        listProductAdapter?.notifyDataSetChanged()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.context_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.exitMenuMain ->{
                finishAffinity()
                Toast.makeText(this,"Приложение завершено", Toast.LENGTH_LONG).show()
            }
        }
        return super.onOptionsItemSelected(item)
    }

}