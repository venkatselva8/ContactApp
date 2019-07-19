package app.task.contactapp.features.contact.list

import app.task.contactapp.features.contact.ContactModel

/*
 * Created by venkatesh on 19-Jul-19.
 */

interface ContactListView {

    fun onPreExecuteToLoadContactList()
    fun onPostExecuteToLoadContactList(contacts: MutableList<ContactModel>?)
    fun onPreExecuteToUpdateContactList()
    fun onPostExecuteToUpdateContactList(contacts: MutableList<ContactModel>?)
}