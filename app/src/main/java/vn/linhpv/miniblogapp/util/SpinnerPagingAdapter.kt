package vn.linhpv.miniblogapp.util

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListUpdateCallback
import kotlinx.coroutines.flow.FlowCollector

data class SpinnerItem(
    val id: String,
    val name: String,
    val displayText: String,
) {
    override fun toString(): String = displayText
}

class SpinnerPagingAdapter(
    private val context: Context,
    private val onItemSelected: (SpinnerItem) -> Unit
) : BaseAdapter(), FlowCollector<PagingData<SpinnerItem>> {

    private val differ = AsyncPagingDataDiffer(
        diffCallback = object : DiffUtil.ItemCallback<SpinnerItem>() {
            override fun areItemsTheSame(oldItem: SpinnerItem, newItem: SpinnerItem): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: SpinnerItem, newItem: SpinnerItem): Boolean {
                return oldItem == newItem
            }
        },
        updateCallback = object : ListUpdateCallback {
            override fun onInserted(position: Int, count: Int) {
                notifyDataSetChanged()
            }
            override fun onRemoved(position: Int, count: Int) {
                notifyDataSetChanged()
            }
            override fun onMoved(fromPosition: Int, toPosition: Int) {
                notifyDataSetChanged()
            }
            override fun onChanged(position: Int, count: Int, payload: Any?) {
                notifyDataSetChanged()
            }
        }
    )

    override suspend fun emit(value: PagingData<SpinnerItem>) {
        differ.submitData(value)
    }

    override fun getCount(): Int = differ.itemCount

    override fun getItem(position: Int): SpinnerItem? = differ.peek(position)

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(android.R.layout.simple_spinner_item, parent, false)

        val item = getItem(position)
        view.findViewById<TextView>(android.R.id.text1).text = item?.name ?: ""

        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(android.R.layout.simple_spinner_dropdown_item, parent, false)

        val item = getItem(position)
        view.findViewById<TextView>(android.R.id.text1).text = item?.name ?: ""

        return view
    }
}