package com.example.worksqlpractice_2

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class ListAdapter<T>(private val context: Context, productList: MutableList<Product>) :
    ArrayAdapter<Product>(context, R.layout.list_item, productList) {
    @SuppressLint("SetTextI18n")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val user = getItem(position)
        var view = convertView
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false)
        }
        val idText = view?.findViewById<TextView>(R.id.idTV)
        val nameText = view?.findViewById<TextView>(R.id.nameTV)
        val priceText = view?.findViewById<TextView>(R.id.priceTV)
        val weightText = view?.findViewById<TextView>(R.id.weightTV)

        idText?.text = "Id: ${user?.id}"
        nameText?.text = "Name: ${user?.name}"
        priceText?.text = "Price: ${user?.price}"
        weightText?.text = "Weight: ${user?.weight}"

        return view!!
    }
}