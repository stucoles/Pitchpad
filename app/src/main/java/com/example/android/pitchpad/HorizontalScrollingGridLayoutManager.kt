package com.example.android.pitchpad

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView


class HorizontalScrollingGridLayoutManager(context: Context?, columns: Int) :
    GridLayoutManager(context, columns) {
    private var isScrollEnabled = true
    fun setScrollEnabled(flag: Boolean) {
        isScrollEnabled = flag
    }

    override fun canScrollVertically(): Boolean {
        return false;
    }

    override fun setOrientation(orientation: Int) {
        super.setOrientation(GridLayoutManager.HORIZONTAL)
    }

}

class GridLayoutDecoration : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        outRect.left = 10
        outRect.right = 100
        outRect.bottom = 20
        //super.getItemOffsets(outRect, view, parent, state)
    }
}

object displayUtilities {
    fun calculateNoOfColumns(
        context: Context,
        columnWidthDp: Int
    ): Int { // For example columnWidthdp=180
        val displayMetrics = context.resources.displayMetrics
        val screenWidthDp = displayMetrics.widthPixels / displayMetrics.density
        return (screenWidthDp / columnWidthDp + 0.5).toInt()
    }
}



