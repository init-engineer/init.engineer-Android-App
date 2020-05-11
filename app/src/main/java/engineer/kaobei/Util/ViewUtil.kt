package engineer.kaobei.Util

import androidx.recyclerview.widget.RecyclerView
import engineer.kaobei.View.AnimatedGap
import kotlin.jvm.internal.Ref

object ViewUtil {
    fun addGapController(recyclerView: RecyclerView, gap: AnimatedGap, reInTop: Ref.BooleanRef){
        recyclerView.addOnScrollListener(object: RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!recyclerView.canScrollVertically(-1)) {
                    reInTop.element = true
                    gap.hide()
                } else {
                    if (reInTop.element) {
                        reInTop.element = false
                        gap.show()
                    }
                }
            }
        })
    }

}

