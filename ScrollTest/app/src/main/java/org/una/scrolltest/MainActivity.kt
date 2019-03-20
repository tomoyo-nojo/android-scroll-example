package org.una.scrolltest

import android.os.Bundle
import android.view.*
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.view_content_box.*
import kotlinx.android.synthetic.main.view_content_list.*
import kotlinx.android.synthetic.main.view_list_cell.view.*

class MainActivity : AppCompatActivity() {

    private val contentListAdapter = ContentListAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        initView()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.list_items_1 -> {
                contentListAdapter.listItems = 1
                true
            }
            R.id.list_items_4 -> {
                contentListAdapter.listItems = 4
                true
            }
            R.id.list_items_100 -> {
                contentListAdapter.listItems = 100
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun initView() {
        recyclerView.adapter = contentListAdapter

        val sectionHeaderText = sectionHeader.findViewById<TextView>(R.id.sectionText)
        val sectionBoxText = sectionBox.findViewById<TextView>(R.id.sectionText)
        val sectionListText = sectionList.findViewById<TextView>(R.id.sectionText)

        sectionBoxText.text = "Content Box"
        sectionListText.text = "Content List"

        fun updateView(scrollY: Int) {
            recyclerView.isNestedScrollingEnabled = scrollY >= contentList.y
            when {
                scrollY >= contentList.y - sectionHeader.height && scrollY < contentList.y -> {
                    sectionHeader.isVisible = false
                    sectionBox.isVisible = true
                }
                scrollY >= contentList.y -> {
                    sectionHeader.isVisible = true
                    sectionHeaderText.text = sectionListText.text
                }
                else -> {
                    sectionHeader.isVisible = true
                    sectionBox.isInvisible = true
                    sectionHeaderText.text = sectionBoxText.text
                }
            }
        }
        nestedScrollView.setOnScrollChangeListener { _: NestedScrollView?, _: Int, scrollY: Int, _: Int, _: Int ->
            updateView(scrollY)
        }
        recyclerView.setOnGlobalLayout {
            val contentHeight = findViewById<View>(android.R.id.content).height
            val height = contentHeight - nestedScrollView.y - sectionHeader.height
            recyclerView.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                height.toInt()
            )
            updateView(0)
        }
    }

}

fun View.setOnGlobalLayout(block: () -> Unit) {
    viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
        override fun onGlobalLayout() {
            block()
            viewTreeObserver.removeOnGlobalLayoutListener(this)
        }
    })
}

fun ViewGroup.inflate(@LayoutRes layoutRes: Int, attachToRoot: Boolean = false): View {
    return LayoutInflater.from(context).inflate(layoutRes, this, attachToRoot)
}

class ContentListAdapter : RecyclerView.Adapter<ContentListAdapter.ViewHolder>() {

    var listItems: Int = 50
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ContentListAdapter.ViewHolder {
        return ContentListAdapter.ViewHolder(viewGroup.inflate(R.layout.view_list_cell))
    }

    override fun getItemCount(): Int {
        return listItems
    }

    override fun onBindViewHolder(holder: ContentListAdapter.ViewHolder, position: Int) {
        holder.setViewData(position + 1)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val iconImage = itemView.iconImage

        fun setViewData(number: Int) {
            iconImage.text = number.toString()
        }
    }
}
