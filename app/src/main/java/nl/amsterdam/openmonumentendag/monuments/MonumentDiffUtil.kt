package nl.amsterdam.openmonumentendag.monuments

import android.support.v7.util.DiffUtil
import android.util.Log
import nl.amsterdam.openmonumentendag.data.Monument

class MonumentDiffCallback(val oldItems: List<Monument>, val newItems: List<Monument>) : DiffUtil.Callback() {
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldItems[oldItemPosition].id == newItems[newItemPosition].id
    }

    override fun getOldListSize(): Int {
        return oldItems.size
    }

    override fun getNewListSize(): Int {
        return newItems.size
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val theSame = oldItems[oldItemPosition].saved == newItems[newItemPosition].saved
        if (theSame) {
            Log.d("DiffUtil", String.format("%s is the same as %s", oldItems[oldItemPosition].id, newItems[newItemPosition].id))
        } else {
            Log.d("DiffUtil", String.format("%s is NOT the same as %s", oldItems[oldItemPosition].id, newItems[newItemPosition].id))
        }
        return theSame
    }

    override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
        if (oldItems[oldItemPosition].saved != newItems[newItemPosition].saved) {
            Log.d("DiffUtil", "saved type")
            return DiffType.Saved
        } else {
            Log.d("DiffUtil", "none type")
            return DiffType.None
        }
    }

    sealed class DiffType {
        object Saved: DiffType()
        object None: DiffType()
    }

}
