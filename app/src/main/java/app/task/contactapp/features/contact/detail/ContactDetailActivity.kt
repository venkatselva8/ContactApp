package app.task.contactapp.features.contact.detail

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import app.task.contactapp.R
import app.task.contactapp.features.contact.ContactModel
import app.task.contactapp.helpers.AppUtils
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textview.MaterialTextView
import com.squareup.picasso.Picasso

/*
 * Created by venkatesh on 19-July-19.
 */

class ContactDetailActivity : AppCompatActivity() {

    companion object {
        private const val KEY_CONTACT_MODEL = "contact_model"
        fun open(context: Context, contact: ContactModel) {
            val intent = Intent(context, ContactDetailActivity::class.java)
            intent.putExtra(KEY_CONTACT_MODEL, contact)
            context.startActivity(intent)
        }

        private const val PERMISSIONS_REQUEST_CALL_PHONE = 101

    }

    private lateinit var constraintContactDetail: ConstraintLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_detail)
        val backButton = findViewById<ImageButton>(R.id.back_button)
        constraintContactDetail = findViewById(R.id.constraint_contact_detail)

        backButton.setOnClickListener {
            finish()
        }

        intent?.let {
            setDataToDisplay(intent.getParcelableExtra(KEY_CONTACT_MODEL) as ContactModel)
        }
    }


    private fun setDataToDisplay(contact: ContactModel) {
        val tvName = findViewById<TextView>(R.id.tv_name)
        val tvPhone = findViewById<MaterialTextView>(R.id.tv_phone_number)
        val tvMail = findViewById<MaterialTextView>(R.id.tv_email)
        val ivPersonPhoto = findViewById<ImageView>(R.id.imv_person)

        val fabCall = findViewById<FloatingActionButton>(R.id.fab_call)
        val fabMessage = findViewById<FloatingActionButton>(R.id.fab_message)
        val fabMail = findViewById<FloatingActionButton>(R.id.fab_mail)

        tvPhone.text = contact.phone

        tvName.text = contact.name
        if (contact.email != null && contact.email!!.isNotEmpty() && contact.email != "null") {
            tvMail.text = contact.email
            fabMail.visibility = View.VISIBLE
            fabMail.setOnClickListener {
                sendEmail(contact.email!!)
            }
        } else {
            tvMail.text = "Email N/A"
            fabMail.visibility = View.GONE
        }

        Picasso.get().load(contact.photo!!)
            .placeholder(R.drawable.ic_person).error(R.drawable.ic_person)
            .into(ivPersonPhoto)

        fabCall.setOnClickListener {
            contact.phone?.let { it1 -> makePhoneCall(it1) }
        }
        fabMessage.setOnClickListener {
            contact.phone?.let { it1 -> sendMessage(it1) }
        }
    }

    private fun sendMessage(phone: String) {
        val uri = Uri.parse("smsto:$phone")
        val intent = Intent(Intent.ACTION_SENDTO, uri)
        intent.putExtra("sms_body", "")
        startActivity(intent)
    }

    private fun sendEmail(emailId: String) {
        try {
            val emailIntent = Intent(Intent.ACTION_SENDTO)
            val uriText = "mailto:" + Uri.encode(emailId)
            val uri = Uri.parse(uriText)
            emailIntent.data = uri
            startActivity(Intent.createChooser(emailIntent, "Choose an Email client :"))
        } catch (ignored: Exception) {
        }
    }

    private fun makePhoneCall(contactPhone: String) {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CALL_PHONE
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            // Permission is not granted
            //request the permission.
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CALL_PHONE, Manifest.permission.CALL_PHONE),
                PERMISSIONS_REQUEST_CALL_PHONE
            )
        } else {
            val intent = Intent(Intent.ACTION_CALL)
            intent.data = Uri.parse("tel:$contactPhone")
            startActivity(intent)
        }
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSIONS_REQUEST_CALL_PHONE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                // permission granted
                AppUtils.showSnackBarWithActionOK(constraintContactDetail, "Permission granted")
            } else {
                // permission denied
                AppUtils.showSnackBarWithActionOK(constraintContactDetail, "Permission denied. please try again")
            }
        }
    }
}
