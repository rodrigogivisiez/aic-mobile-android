package edu.artic.events

import android.view.View
import com.bumptech.glide.Glide
import com.fuzz.rx.bindToMain
import com.fuzz.rx.defaultThrottle
import com.fuzz.rx.disposedBy
import com.jakewharton.rxbinding2.view.clicks
import com.jakewharton.rxbinding2.widget.text
import edu.artic.adapter.AutoHolderRecyclerViewAdapter
import edu.artic.adapter.BaseViewHolder
import kotlinx.android.synthetic.main.cell_all_events_layout.view.*

/**
 * @author Sameer Dhakal (Fuzz)
 */

class AllEventsAdapter : AutoHolderRecyclerViewAdapter<AllEventsCellViewModel>() {

    override fun View.onBindView(item: AllEventsCellViewModel, position: Int) {

        item.eventImageUrl.subscribe {
            Glide.with(context)
                    .load(it)
                    .into(image)
        }.disposedBy(item.viewDisposeBag)

        item.eventTitle
                .bindToMain(title.text())
                .disposedBy(item.viewDisposeBag)

        item.eventDescription
                .bindToMain(description.text())
                .disposedBy(item.viewDisposeBag)

        item.eventDateTime
                .bindToMain(dateTime.text())
                .disposedBy(item.viewDisposeBag)

    }

    override fun onItemViewDetachedFromWindow(holder: BaseViewHolder, position: Int) {
        super.onItemViewDetachedFromWindow(holder, position)
        getItem(position).apply {
            cleanup()
            onCleared()
        }
    }

    override fun getLayoutResId(position: Int): Int {
        return R.layout.cell_all_events_layout
    }

}