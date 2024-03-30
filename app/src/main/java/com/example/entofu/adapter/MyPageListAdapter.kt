package com.example.entofu.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.entofu.dataItem.MyPageListItem
import com.example.entofu.R


class MyPageListAdapter  : BaseAdapter() {

    private var listViewItemList = ArrayList<MyPageListItem>()

    override fun getCount(): Int {
        return listViewItemList.size
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        val context = parent.context

        // "listview_item" Layout을 inflate하여 convertView 참조 획득.
        if (view == null) {
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = inflater.inflate(R.layout.my_page_list_item, parent, false)
        }

        // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
        val iconImageView = view!!.findViewById(R.id.list_image) as ImageView
        val titleTextView = view.findViewById(R.id.list_text) as TextView
        val btnTextView = view.findViewById<TextView>(R.id.pwd_change)

        // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
        val listViewItem = listViewItemList[position]

        // 아이템 내 각 위젯에 데이터 반영
        iconImageView.setImageDrawable(listViewItem.getImg())
//        titleTextView.text = listViewItem.getText()
        if (position == count - 1) {
            titleTextView.text = listViewItem.getText()?.let { "*".repeat(it.length) } // textPassword
            btnTextView.text = "수정"
        }
        else titleTextView.text = listViewItem.getText()

        return view
    }

    // 지정한 위치(position)에 있는 데이터와 관계된 아이템(row)의 ID를 리턴. : 필수 구현
    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    // 지정한 위치(position)에 있는 데이터 리턴 : 필수 구현
    override fun getItem(position: Int): Any {
        return listViewItemList[position]
    }

    // 아이템 데이터 추가를 위한 함수. 개발자가 원하는대로 작성 가능.
    fun addItem(icon: Drawable, title: String) {
        val item = MyPageListItem()
        item.setImg(icon)
        item.setText(title)
        listViewItemList.add(item)
    }
}