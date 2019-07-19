package app.task.contactapp.features.contact.list

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import app.task.contactapp.R
import app.task.contactapp.features.contact.ContactModel
import app.task.contactapp.helpers.AppUtils

/*
 * Created by venkatesh on 19-July-19.
 */


class ContactListActivity : AppCompatActivity(), ContactListView {

    private lateinit var swipeRefreshContact: SwipeRefreshLayout
    private lateinit var constraintContactList: ConstraintLayout
    private lateinit var rvContact: RecyclerView
    private lateinit var mContext: Context
    private lateinit var listOfContact: MutableList<ContactModel>
    private lateinit var contactListPresenter: ContactListPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_list)
        init()
        getContactList()

    }

    private fun init() {
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.title = "Contact App"
        }
        mContext = this

        ProcessLifecycleOwner.get().lifecycle.addObserver(AppLifecycleListener())
        contactListPresenter = ContactListPresenter(mContext, this)

        swipeRefreshContact = findViewById(R.id.swipe_refresh_contacts)
        rvContact = findViewById(R.id.rv_contact)
        constraintContactList = findViewById(R.id.constraint_contact_list)

        listOfContact = mutableListOf()
        rvContact.layoutManager = LinearLayoutManager(this)

        swipeRefreshContact.setOnRefreshListener {
            progressRefresh(true)
            getContactList()
        }

    }

    private fun getContactList() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_CONTACTS
            )
            != PackageManager.PERMISSION_GRANTED
        ) {

            // Permission is not granted
            // request the permission.
            rvContact.visibility = View.GONE
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_CONTACTS),
                PERMISSIONS_REQUEST_READ_CONTACTS
            )

        } else {
            contactListPresenter.loadContactList()
        }

    }

    override fun onPreExecuteToLoadContactList() {
        progressRefresh(true)
        rvContact.visibility = View.GONE
    }

    override fun onPostExecuteToLoadContactList(contacts: MutableList<ContactModel>?) {
        listOfContact = contacts!!
        progressRefresh(false)
        if (listOfContact.size > 0) {
            rvContact.visibility = View.VISIBLE
            rvContact.adapter = ContactListAdapter(listOfContact)
        } else {
            rvContact.visibility = View.GONE
            AppUtils.showSnackBarWithActionOK(constraintContactList, "No contact available")
        }
    }

    override fun onPreExecuteToUpdateContactList() {
        progressRefresh(true)
    }

    override fun onPostExecuteToUpdateContactList(contacts: MutableList<ContactModel>?) {
        progressRefresh(false)
        //Update UI
        if (listOfContact.size != contacts!!.size) {
            listOfContact = contacts
            if (contacts.size > 0) {
                AppUtils.showSnackBarWithActionOK(constraintContactList, "Contacts updated")
                rvContact.visibility = View.VISIBLE
                rvContact.adapter = ContactListAdapter(contacts)
            } else {
                rvContact.visibility = View.GONE
                AppUtils.showSnackBarWithActionOK(constraintContactList, "No contact available")
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSIONS_REQUEST_READ_CONTACTS -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // permission granted
                    getContactList()
                } else {
                    // permission denied
                    AppUtils.showSnackBarWithActionOK(constraintContactList, "Permission Denied please try again")
                    progressRefresh(false)
                    rvContact.visibility = View.VISIBLE
                }
                return
            }

            // Add other 'when' lines to check for other
            // permissions this app might request.
            else -> {
                // Ignore all other requests.
            }
        }
    }

    companion object {
        private const val PERMISSIONS_REQUEST_READ_CONTACTS = 103
    }


    private fun progressRefresh(show: Boolean) {
        swipeRefreshContact.isRefreshing = show
    }


    //To update the contact list if any contact added
    @SuppressWarnings("Unused")
    inner class AppLifecycleListener : LifecycleObserver {

        private var applicationPaused = false
        @OnLifecycleEvent(Lifecycle.Event.ON_START)
        fun onMoveToForeground() {
            // app moved to foreground
            if (applicationPaused) {
                applicationPaused = false
                // Check for the update contact
                contactListPresenter.updatedContactList()
            }
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
        fun onMoveToBackground() {
            // app moved to background
            applicationPaused = true
        }
    }

}
