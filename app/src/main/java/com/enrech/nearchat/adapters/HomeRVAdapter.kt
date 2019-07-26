package com.enrech.nearchat.adapters

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.enrech.nearchat.ViewHolders.EventListViewHolder
import com.enrech.nearchat.interfaces.EventCardInterface

class HomeRVAdapter(private val context: Context, private var list: List<String>, private var listener: EventCardInterface): RecyclerView.Adapter<EventListViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventListViewHolder {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getItemCount(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onBindViewHolder(holder: EventListViewHolder, position: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}