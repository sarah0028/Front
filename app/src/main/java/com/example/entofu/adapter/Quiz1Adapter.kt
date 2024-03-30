package com.example.entofu.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.entofu.R
import com.example.entofu.dataItem.Quiz1Model
import com.example.entofu.dataItem.Quiz2Model


class Quiz1Adapter(recyclerList: List<Quiz1Model>) : RecyclerView.Adapter<Quiz1Adapter.CustomViewHolder>() {

    private var quiz1ModelList: List<Quiz1Model>? = recyclerList

    inner class CustomViewHolder(val myView: View) : RecyclerView.ViewHolder(myView) {

        var enWord: TextView = itemView.findViewById<TextView>(R.id.enWord)
        var mean1: TextView = itemView.findViewById<TextView>(R.id.mean1)
        var mean2: TextView = itemView.findViewById<TextView>(R.id.mean2)
        var mean3: TextView = itemView.findViewById<TextView>(R.id.mean3)
        var mean4: TextView = itemView.findViewById<TextView>(R.id.mean4)

    }

    override
    fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val itemView = layoutInflater.inflate(R.layout.quiz1_cardview, parent, false)
        return CustomViewHolder(itemView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {

        holder.enWord.text = quiz1ModelList!![position].enWord
        holder.mean1.text = "1. ${quiz1ModelList!![position].mean1}"
        holder.mean2.text = "2. ${quiz1ModelList!![position].mean2}"
        holder.mean3.text = "3. ${quiz1ModelList!![position].mean3}"
        holder.mean4.text = "4. ${quiz1ModelList!![position].mean4}"

        holder.mean1.setOnClickListener {
            itemClickListener.onClick(it,position)
        }
        holder.mean2.setOnClickListener {
            itemClickListener.onClick(it,position)
        }
        holder.mean3.setOnClickListener {
            itemClickListener.onClick(it,position)
        }
        holder.mean4.setOnClickListener {
            itemClickListener.onClick(it,position)
        }
    }
    interface OnItemClickListener {
        fun onClick(v:View, position:Int) : Boolean
    }

    fun setItemClickListener(onItemClickListener : OnItemClickListener){
        this.itemClickListener = onItemClickListener
    }

    private lateinit var itemClickListener : OnItemClickListener

    override fun getItemCount(): Int {
        return quiz1ModelList!!.size
    }
}


class Quiz2Adapter(recyclerList: List<Quiz2Model>) : RecyclerView.Adapter<Quiz2Adapter.CustomViewHolder>() {

    private var quiz2ModelList: List<Quiz2Model>? = recyclerList

    inner class CustomViewHolder(val myView: View) : RecyclerView.ViewHolder(myView) {

        var koWord: TextView = itemView.findViewById(R.id.koWord)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val itemView = layoutInflater.inflate(R.layout.quiz2_cardview, parent, false)
        return CustomViewHolder(itemView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        holder.koWord.text = quiz2ModelList!![position].koWord
    }

    override fun getItemCount(): Int {
        return quiz2ModelList!!.size
    }

}