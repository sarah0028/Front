package com.example.entofu.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils.loadAnimation
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.entofu.R
import com.example.entofu.dataItem.ScriptItem


class RecyclerViewAdapter(var mList: ArrayList<ScriptItem>?) :
    RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>() , Filterable {

    var itemList= ArrayList<ScriptItem>()
    val originalItemList= ArrayList<ScriptItem>(mList!!)

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var titleImage: ImageView
        var title: TextView
        var status : TextView

        init {
            titleImage = itemView.findViewById<View>(com.example.entofu.R.id.titleimage) as ImageView
            title = itemView.findViewById<View>(com.example.entofu.R.id.title) as TextView
            status = itemView.findViewById<View>(com.example.entofu.R.id.status) as TextView
        }
    }

    init {
        this.itemList = mList!!
    }

    // 아이템 뷰를 위한 뷰홀더 객체를 생성하여 리턴
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context: Context = parent.context
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(com.example.entofu.R.layout.layout_scriptitem, parent, false)

        return ViewHolder(view)
    }

    // position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item: ScriptItem = itemList[position]

        Glide.with(holder.itemView).load(item.getImgUrl()).into(holder.titleImage)
//        item.setDrawable(holder.titleImage.drawable) //= holder.titleImage.drawable
//        holder.titleImage.setImageDrawable(item.getImg()) // 사진 없어서 기본 파일로 이미지 띄움
        holder.title.text = item.getTitle()
        holder.status.text = item.getStatus()

        //animation
        holder.itemView.startAnimation(loadAnimation(holder.itemView.context,R.anim.script_one))

        holder.itemView.setOnClickListener{
            itemClickListener.onClick(it,position)
        }

        holder.itemView.setOnLongClickListener{
            itemLongClickListener.onLongClick(it,position)
        }
    }

    interface OnItemClickListener {
        fun onClick(v:View, position:Int)
    }
    interface OnItemLongClickListener {
        fun onLongClick(v:View, position:Int) : Boolean
    }
    fun setItemClickListener(onItemClickListener : OnItemClickListener){
        this.itemClickListener = onItemClickListener
    }

    fun setItemLongClickListener(onItemLongClickListener : OnItemLongClickListener){
        this.itemLongClickListener = onItemLongClickListener
    }

    private lateinit var itemClickListener : OnItemClickListener
    private lateinit var itemLongClickListener : OnItemLongClickListener


    override fun getItemCount(): Int {
        return itemList.size
    }

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
            val filteredList:ArrayList<ScriptItem> = ArrayList()
            //공백제외 아무런 값이 없을 경우 -> 원본 배열
            if (filterString.trim().isEmpty()) {
                filteredList.addAll(originalItemList)

                //공백제외 2글자 이인 경우 -> 이름과 카테고리로 검색, 대소문자 구분x
            } else {
                for (item in originalItemList) {
                    if (item.getTitle()?.lowercase()?.contains(filterString.lowercase()) == true || item.getStatus()?.lowercase()?.contains(filterString.lowercase())==true ) {
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
            itemList.addAll(filterResults.values as ArrayList<ScriptItem>)
            notifyDataSetChanged()
        }
    }
    @SuppressLint("NotifyDataSetChanged")
    fun addMemberScriptOne(idx:Long,status:String,title:String,content:String,imgUrl:String?){
        val scriptItem = ScriptItem()
        scriptItem.setStatus(status)
        scriptItem.setImgUrl(imgUrl)  //기본 이미지
        scriptItem.setTitle(title)
        scriptItem.setContent(content)
        scriptItem.setScriptIdx(idx)

        var scrapNum = 0
        for (i in originalItemList)
            if(i.getStatus()=="SCRAP")
                scrapNum += 1
            else if (i.getStatus()=="MEMBER") break

        originalItemList.add(scrapNum,scriptItem)
        itemList.add(scrapNum,scriptItem)
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun deleteOne(position: Int){
        val scriptItem = itemList[position]
        scriptItem.setStatus("COMMON")
        originalItemList.removeAt(position)
        itemList.removeAt(position)
        notifyDataSetChanged()
    }
}