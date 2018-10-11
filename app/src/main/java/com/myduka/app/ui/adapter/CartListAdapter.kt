/*
 *
 *  * Copyright (C) 2017 Safaricom, Ltd.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package com.myduka.app.ui.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.myduka.app.R
import com.myduka.app.ui.callback.PriceTransfer
import java.util.*

/**
 * Created  on 8/1/2017.
 */

class CartListAdapter(private val context: Context, private val items: List<String>,
                      private val item_prices: List<String>,
                      private val priceTransfer: PriceTransfer
) : RecyclerView.Adapter<CartListAdapter.ViewHolder>() {

    private val prices = ArrayList<Int>()

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): CartListAdapter.ViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.category_list_item, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: CartListAdapter.ViewHolder, i: Int) {
        viewHolder.item_name.text = items[i]
        viewHolder.btn_add_to_cart.text = "Add Kshs " + item_prices[i]
        viewHolder.item_description.text = items[i] + " fresh and healthy now available."

        when {
            items[i] == "Tomatoes" -> Glide.with(context)
                    .load(R.drawable.tomatoes)
                    .into(viewHolder.item_image)
            items[i] == "Apples" -> Glide.with(context)
                    .load(R.drawable.apples)
                    .into(viewHolder.item_image)
            items[i] == "Bananas" -> Glide.with(context)
                    .load(R.drawable.bananas)
                    .into(viewHolder.item_image)
        }
    }

    /**
     * tells the Adapter that how many rows are there to display
     */
    override fun getItemCount(): Int = items.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
        val item_image: ImageView = view.findViewById(R.id.item_image)
        val item_name: TextView = view.findViewById(R.id.item_name)
        val item_description: TextView = view.findViewById(R.id.item_description)
        val btn_add_to_cart: Button = view.findViewById(R.id.btn_add_to_cart)

        init {
            btn_add_to_cart.setOnClickListener(this)
        }

        override fun onClick(view: View) {
            val position = adapterPosition
            if (view.id == R.id.btn_add_to_cart) {
                prices.add(Integer.valueOf(btn_add_to_cart.text.toString().replace("Add Kshs ", "")))
                Toast.makeText(context, "Added: " + items[position], Toast.LENGTH_SHORT).show()

                priceTransfer.setPrices(prices)
            }
        }
    }
}
