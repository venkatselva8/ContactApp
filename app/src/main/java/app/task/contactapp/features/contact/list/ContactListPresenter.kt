package app.task.contactapp.features.contact.list

import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.AsyncTask
import android.provider.ContactsContract
import app.task.contactapp.features.contact.ContactModel

/*
 * Created by venkatesh on 19-Jul-19.
 */

class ContactListPresenter(val mContext: Context, val contactListView: ContactListView) {

    fun loadContactList() {
        AsyncTaskToLoadContact().execute()
    }

    fun updatedContactList() {
        AsyncTaskToUpdateContact().execute()
    }

    @SuppressLint("StaticFieldLeak")
    private inner class AsyncTaskToLoadContact : AsyncTask<String, String, MutableList<ContactModel>>() {

        val contacts: MutableList<ContactModel> = mutableListOf()
        var name: String? = null
        var phonenumber: String? = null
        var photo: String? = null
        var email: String? = null

        override fun onPreExecute() {
          contactListView.onPreExecuteToLoadContactList()
        }

        override fun doInBackground(vararg params: String): MutableList<ContactModel>? {

            val contentResolver = mContext.contentResolver
            val cursor = contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,
                null,
                null,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
            )
            if (cursor!!.count > 0) {
                while (cursor.moveToNext()) {

                    email = ""
                    photo = ""

                    name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                    phonenumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                    photo =
                        openDisplayPhoto(cursor.getLong(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID))).toString()
                    val id = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID))

                    val curEmail = contentResolver.query(
                        ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
                        ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                        arrayOf(id), null
                    )
                    if (curEmail!!.count > 0) {
                        if (curEmail.moveToNext()) {
                            email =
                                curEmail.getString(curEmail.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA))
                        }
                    }
                    curEmail.close()

                    contacts.add(
                        ContactModel(
                            name.toString(),
                            phonenumber.toString(),
                            photo.toString(),
                            email.toString()
                        )
                    )
                }
            }
            cursor.close()

            return contacts
        }


        override fun onPostExecute(contacts: MutableList<ContactModel>?) {
            super.onPostExecute(contacts)
            contactListView.onPostExecuteToLoadContactList(contacts)
        }

    }

    @SuppressLint("StaticFieldLeak")
    private inner class AsyncTaskToUpdateContact : AsyncTask<String, String, MutableList<ContactModel>>() {

        val contacts: MutableList<ContactModel> = mutableListOf()
        var name: String? = null
        var phonenumber: String? = null
        var photo: String? = null
        var email: String? = null

        override fun onPreExecute() {
            contactListView.onPreExecuteToUpdateContactList()
        }

        override fun doInBackground(vararg params: String): MutableList<ContactModel>? {

            val contentResolver = mContext.contentResolver
            val cursor = contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,
                null,
                null,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
            )
            if (cursor!!.count > 0) {
                while (cursor.moveToNext()) {
                    name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                    phonenumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                    photo =
                        openDisplayPhoto(cursor.getLong(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID))).toString()
                    val id = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID))

                    val curEmail = contentResolver.query(
                        ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
                        ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                        arrayOf(id), null
                    )
                    email = ""
                    if (curEmail!!.count > 0) {
                        if (curEmail.moveToNext()) {
                            email =
                                curEmail.getString(curEmail.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA))
                        }
                    }
                    curEmail.close()

                    contacts.add(
                        ContactModel(
                            name.toString(),
                            phonenumber.toString(),
                            photo.toString(),
                            email.toString()
                        )
                    )
                }
            }
            cursor.close()

            return contacts
        }


        override fun onPostExecute(contacts: MutableList<ContactModel>?) {
            super.onPostExecute(contacts)
            contactListView.onPostExecuteToUpdateContactList(contacts)
        }
    }

    fun openDisplayPhoto(contactId: Long): Uri? {
        val contactUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactId)
        var displayPhotoUri: Uri? = null
        if (contactUri != null) {
            displayPhotoUri = Uri.withAppendedPath(contactUri, ContactsContract.Contacts.Photo.DISPLAY_PHOTO)
        }
        return displayPhotoUri
    }

}