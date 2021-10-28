package com.example.android.shimei_sha

import android.app.PendingIntent
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import com.example.android.shimei_sha.databinding.ActivityLoginAtivityBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.hbb20.CountryCodePicker


class LoginAtivity : AppCompatActivity() {
    private  lateinit var binding: ActivityLoginAtivityBinding
    private lateinit var phonenumber:String
    private lateinit var countrycode:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginAtivityBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.number.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable) {
                binding.logbtn.isEnabled = !(s.length < 10 || (s.isNullOrEmpty()))
            }
        })
        binding.logbtn.setOnClickListener { checknumber() }


    }

    private fun checknumber() {

        countrycode=findViewById<CountryCodePicker>(R.id.ccp).selectedCountryCodeWithPlus
        phonenumber=countrycode+ findViewById<EditText>(R.id.number).text.toString()

        notifyuser()
    }

    private fun notifyuser() {
        MaterialAlertDialogBuilder(this).apply {
            setMessage("We will be verifying the Phone Number:$phonenumber\n"+
                    "Is is Ok,or do you want to Edit the number")
    setPositiveButton("OK"){_,_->
        showOtpActivity()
    }
            setNegativeButton("Edit Text") { dialog, which ->
                dialog.dismiss()
            }
            setCancelable(false)
            create()
            show()

            }

        }
    private fun showOtpActivity() {
        val intent = Intent(this, OTPactivity::class.java).apply{
            putExtra(PHONE_NUMBER,phonenumber)
        }
        startActivity(intent)
    }
    }


