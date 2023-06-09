package com.example.projectmanager.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projectmanager.R
import com.example.projectmanager.activities.TaskListActivity
import com.example.projectmanager.modals.Task
import java.util.*
import kotlin.collections.ArrayList


open class TaskListItemsAdapter(private val context: Context, private var list: ArrayList<Task>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mPositionDraggedFrom = -1
    private var mPositionDraggedTo = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val view = LayoutInflater.from(context).inflate(R.layout.item_task, parent, false)
        val layoutParams = LinearLayout.LayoutParams(
            (parent.width * 0.7).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        // margins order starts left top right bottom
        layoutParams.setMargins((15.toDp()).toPx(), 0, (40.toDp()).toPx(), 0)
        view.layoutParams = layoutParams
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }


    @SuppressLint("CutPasteId")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val modal = list[position]

        if (holder is MyViewHolder) {
            if (position == list.size - 1) {
                holder.itemView.findViewById<TextView>(R.id.tv_add_task_list).visibility =
                    View.VISIBLE
                holder.itemView.findViewById<LinearLayout>(R.id.ll_task_item).visibility = View.GONE
            } else {
                holder.itemView.findViewById<TextView>(R.id.tv_add_task_list).visibility = View.GONE
                holder.itemView.findViewById<LinearLayout>(R.id.ll_task_item).visibility =
                    View.VISIBLE
            }
            holder.itemView.findViewById<TextView>(R.id.tv_task_list_title).text = modal.title

            holder.itemView.findViewById<TextView>(R.id.tv_add_task_list).setOnClickListener {
                holder.itemView.findViewById<TextView>(R.id.tv_add_task_list).visibility = View.GONE
                holder.itemView.findViewById<CardView>(R.id.cv_add_task_list_name).visibility =
                    View.VISIBLE
            }
            holder.itemView.findViewById<ImageButton>(R.id.ib_close_list_name).setOnClickListener {
                holder.itemView.findViewById<TextView>(R.id.tv_add_task_list).visibility =
                    View.VISIBLE
                holder.itemView.findViewById<CardView>(R.id.cv_add_task_list_name).visibility =
                    View.GONE
            }
            holder.itemView.findViewById<ImageButton>(R.id.ib_done_list_name).setOnClickListener {
                val taskListName =
                    holder.itemView.findViewById<TextView>(R.id.et_task_list_name).text.toString()
                if (taskListName.isNotEmpty()) {
                    if (context is TaskListActivity) {
                        context.createTaskList(taskListName)
                    }
                } else {
                    Toast.makeText(context, "Please enter a list name.", Toast.LENGTH_SHORT).show()
                }
            }
            holder.itemView.findViewById<ImageButton>(R.id.ib_edit_list_name).setOnClickListener {
                holder.itemView.findViewById<EditText>(R.id.et_edit_task_list_name)
                    .setText(modal.title)
                holder.itemView.findViewById<LinearLayout>(R.id.ll_title_view).visibility =
                    View.GONE
                holder.itemView.findViewById<CardView>(R.id.cv_edit_task_list_name).visibility =
                    View.VISIBLE
            }
            holder.itemView.findViewById<ImageButton>(R.id.ib_close_editable_view)
                .setOnClickListener {
                    holder.itemView.findViewById<LinearLayout>(R.id.ll_title_view).visibility =
                        View.VISIBLE
                    holder.itemView.findViewById<CardView>(R.id.cv_edit_task_list_name).visibility =
                        View.GONE
                }
            holder.itemView.findViewById<ImageButton>(R.id.ib_done_edit_list_name)
                .setOnClickListener {
                    val taskListName =
                        holder.itemView.findViewById<TextView>(R.id.et_edit_task_list_name).text.toString()
                    if (taskListName.isNotEmpty()) {
                        if (context is TaskListActivity) {
                            context.updateTaskList(position, taskListName, modal)
                        }
                    } else {
                        Toast.makeText(context, "Please enter a list name.", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            holder.itemView.findViewById<ImageButton>(R.id.ib_delete_list).setOnClickListener {
                alertDialogForDeleteList(position, modal.title)
            }
            holder.itemView.findViewById<TextView>(R.id.tv_add_card).setOnClickListener {
                holder.itemView.findViewById<TextView>(R.id.tv_add_card).visibility = View.GONE
                holder.itemView.findViewById<CardView>(R.id.cv_add_card).visibility = View.VISIBLE
            }
            holder.itemView.findViewById<ImageButton>(R.id.ib_close_card_name).setOnClickListener {
                holder.itemView.findViewById<TextView>(R.id.tv_add_card).visibility = View.VISIBLE
                holder.itemView.findViewById<CardView>(R.id.cv_add_card).visibility = View.GONE
            }
            holder.itemView.findViewById<ImageButton>(R.id.ib_done_card_name).setOnClickListener {
                val cardName =
                    holder.itemView.findViewById<TextView>(R.id.et_card_name).text.toString()
                if (cardName.isNotEmpty()) {
                    if (context is TaskListActivity) {
                        context.addCardToTaskList(position, cardName)
                    }
                } else {
                    Toast.makeText(context, "Please enter a card name.", Toast.LENGTH_SHORT).show()
                }
            }

            holder.itemView.findViewById<RecyclerView>(R.id.rv_card_list).layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            holder.itemView.findViewById<RecyclerView>(R.id.rv_card_list).setHasFixedSize(true)
            val adapter = CardListItemsAdapter(context, modal.cards)
            holder.itemView.findViewById<RecyclerView>(R.id.rv_card_list).adapter = adapter

            adapter.setOnClickListener(object : CardListItemsAdapter.OnClickListener {
                override fun onClick(cardPosition: Int) {
                    if (context is TaskListActivity) {
                        context.cardDetails(position, cardPosition)
                    }
                }
            }
            )
            val dividerItemDecoration = DividerItemDecoration(
                context,
                DividerItemDecoration.VERTICAL)
            holder.itemView.findViewById<RecyclerView>(R.id.rv_card_list).addItemDecoration(
                dividerItemDecoration)
            val helper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.UP or ItemTouchHelper.DOWN,
               0
            ) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    from: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    val fromPosition = from.adapterPosition
                    val targetPosition = target.adapterPosition

                    if(mPositionDraggedFrom == -1) {
                        mPositionDraggedFrom = fromPosition
                    }
                    mPositionDraggedTo = targetPosition

                    Collections.swap(modal.cards, fromPosition, targetPosition)
                    adapter.notifyItemMoved(fromPosition, targetPosition)
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    // Not required as we are not swiping in this case
                }

                override fun clearView(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder
                ) {
                    super.clearView(recyclerView, viewHolder)
                    if(mPositionDraggedFrom != -1 && mPositionDraggedTo != -1 && mPositionDraggedFrom != mPositionDraggedTo) {
                        (context as TaskListActivity).updateCardsInTaskList(
                            position,
                            modal.cards
                        )
                    }
                    mPositionDraggedFrom = -1
                    mPositionDraggedTo = -1
                }
            })
            helper.attachToRecyclerView(holder.itemView.findViewById(R.id.rv_card_list))
        }
    }

    private class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)

    // it is for adjusting the size of the view px to dp
    private fun Int.toDp(): Int = (this / Resources.getSystem().displayMetrics.density).toInt()

    // it is for adjusting the size of the view dp to px
    private fun Int.toPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()

    private fun alertDialogForDeleteList(position: Int, title: String) {
        val builder = AlertDialog.Builder(context)
        //set title for alert dialog
        builder.setTitle("Alert")
        //set message for alert dialog
        builder.setMessage("Are you sure you want to delete $title.")
        builder.setIcon(android.R.drawable.ic_dialog_alert)
        //performing positive action
        builder.setPositiveButton("Yes") { dialogInterface, which ->
            dialogInterface.dismiss() // Dialog will be dismissed

            if (context is TaskListActivity) {
                context.deleteTaskList(position)
            }
        }

        //performing negative action
        builder.setNegativeButton("No") { dialogInterface, which ->
            dialogInterface.dismiss() // Dialog will be dismissed
        }
        // Create the AlertDialog
        val alertDialog: AlertDialog = builder.create()
        // Set other dialog properties
        alertDialog.setCancelable(false) // Will not allow user to cancel after clicking on remaining screen area.
        alertDialog.show()  // show the dialog to UI
    }
}