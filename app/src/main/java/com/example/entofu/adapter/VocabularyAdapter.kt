package com.example.entofu.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.entofu.R
import com.example.entofu.dataItem.Word

class VocabularyAdapter(var mList: ArrayList<Word>?) :
    RecyclerView.Adapter<VocabularyAdapter.ViewHolder>() , Filterable {
    var itemList= ArrayList<Word>()
    val originalItemList= ArrayList<Word>(mList!!)

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var word: TextView
        var meaning: TextView


        init {
            word = itemView.findViewById<View>(R.id.word) as TextView
            meaning = itemView.findViewById<View>(R.id.meaning) as TextView
        }
    }

    init {
        this.itemList = mList!!
    }

    // 아이템 뷰를 위한 뷰홀더 객체를 생성하여 리턴
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context: Context = parent.context
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(com.example.entofu.R.layout.layout_vocabularyitem, parent, false)

        return ViewHolder(view)
    }

    // position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item: Word = itemList[position]
        holder.word.text = item.getWord()
        holder.meaning.text = item.getMeaning()

        //animation
        holder.itemView.startAnimation(AnimationUtils.loadAnimation(holder.itemView.context, R.anim.script_one))

        holder.itemView.setOnLongClickListener{
            itemLongClickListener.onLongClick(it,position)
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }
    interface OnItemLongClickListener {
        fun onLongClick(v:View, position:Int) : Boolean
    }

    fun setItemLongClickListener(onItemLongClickListener : OnItemLongClickListener){
        this.itemLongClickListener = onItemLongClickListener
    }

    private lateinit var itemLongClickListener : OnItemLongClickListener


    // filter
    var itemFilter = ItemFilter()

    override fun getFilter(): Filter {
        return itemFilter
    }


    inner class ItemFilter : Filter() {
        override fun performFiltering(charSequence: CharSequence): FilterResults {
            val filterString = charSequence.toString()
            val results = FilterResults()

            //검색이 필요없을 경우를 위해 원본 배열을 복제
            val filteredList:ArrayList<Word> = ArrayList()
            //공백제외 아무런 값이 없을 경우 -> 원본 배열
            if (filterString.trim().isEmpty()) {
                filteredList.addAll(originalItemList)

                //공백제외 2글자 이인 경우 -> 이름으로만 검색
            } else {
                for (item in originalItemList) {
                    if (item.getWord()?.contains(filterString) == true ||item.getMeaning()?.contains(filterString) == true) {
                        filteredList.add(item)
                    }
                }
            }
            results.values = filteredList
            results.count = filteredList.size
            return results
        }
        @SuppressLint("NotifyDataSetChanged")
        override fun publishResults(charSequence: CharSequence?, filterResults: FilterResults) {
            itemList.clear()
            itemList.addAll(filterResults.values as ArrayList<Word>)
            notifyDataSetChanged()
        }
    }

    fun deleteOne(position: Int){
        originalItemList.removeAt(position)
        itemList.removeAt(position)
        notifyItemRemoved(position)
    }

}