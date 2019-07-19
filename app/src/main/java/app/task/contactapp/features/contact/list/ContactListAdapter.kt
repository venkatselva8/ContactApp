package app.task.contactapp.features.contact.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import app.task.contactapp.R
import app.task.contactapp.features.contact.ContactModel
import app.task.contactapp.features.contact.detail.ContactDetailActivity
import app.task.contactapp.helpers.CircleTransform
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_contact.view.*

/*
 * Created by venkatesh on 19-July-19.
 */


class ContactListAdapter(var contactList: List<ContactModel>) :
    RecyclerView.Adapter<ContactListAdapter.ContactListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactListViewHolder {
        return ContactListViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_contact, parent, false))
    }

    override fun getItemCount(): Int {
        return contactList.size
    }

    override fun onBindViewHolder(holder: ContactListViewHolder, position: Int) {
        holder.bind(contactList[position])
    }

    inner class ContactListViewHolder(var view: View) : RecyclerView.ViewHolder(view) {

        private val tvName = view.findViewById(R.id.tv_name) as TextView
        private val tvPhone = view.findViewById(R.id.tv_phone_number) as TextView
        private val ivPhoto = view.findViewById(R.id.iv_photo) as ImageView

        fun bind(contact: ContactModel) {
            tvName.text = contact.name
            tvPhone.text = contact.phone

            Picasso.get().load(contact.photo!!)
                .placeholder(R.drawable.ic_person).error(R.drawable.ic_person)
                .transform(CircleTransform())
                .into(ivPhoto)

            view.itemview.setOnClickListener {
                ContactDetailActivity.open(view.context, contact)
            }
        }
    }
}