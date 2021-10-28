package com.example.android.shimei_sha

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import com.example.android.shimei_sha.databinding.ActivityLoginAtivityBinding
import com.example.android.shimei_sha.databinding.ActivityOtpactivityBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import java.util.concurrent.TimeUnit
import android.text.style.ClickableSpan as ClickableSpan1

const val PHONE_NUMBER="phoneNumber"
class OTPactivity : AppCompatActivity(), View.OnClickListener {
    lateinit var callbacks:PhoneAuthProvider.OnVerificationStateChangedCallbacks
    var phoneNumber: String? = null
    var mVerificationId:String?=null
    var mResendToken:PhoneAuthProvider.ForceResendingToken?=null
    private lateinit var progressDialog:ProgressDialog
    private var mCounterDown :CountDownTimer?=null
    private lateinit var binding: ActivityOtpactivityBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOtpactivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setContentView(R.layout.activity_otpactivity)
        initviews()
        startVerify()

    }

    private fun startVerify() {
        val options = PhoneAuthOptions.newBuilder()
            .setPhoneNumber(phoneNumber!!)       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this)                 // Activity (for callback binding)
            .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
        showtimer(60000)
        progressDialog=createProgressDialog("Sending a verification code",false)
        progressDialog.show()



    }

    private fun showtimer(milliSecInFuture: Long) {
        binding.resend.isEnabled=false
        mCounterDown= object:CountDownTimer(milliSecInFuture,1000){
            override fun onTick(millisUntilFinished: Long) {
                binding.cno.isVisible = true
                binding.cno.text = getString(R.string.second, millisUntilFinished % 1000)
            }

            override fun onFinish() {
                binding.resend.isEnabled=true
                binding.cno.isVisible=false
            }
        }.start()
        }
        override fun onDestroy() {
            super.onDestroy()
            if(mCounterDown!=null){
                mCounterDown!!.cancel()
            }
        }



    private fun initviews() {
        phoneNumber = intent.getStringExtra(PHONE_NUMBER)
        binding.vno.text = getString(R.string.verify_num, phoneNumber)
        setSpannablestring()
        binding.verification.setOnClickListener(this)
        binding.resend.setOnClickListener(this)


        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                if( :: progressDialog.isInitialized){
                    progressDialog.dismiss()
                }
                val smsCode=credential.smsCode
                if(!smsCode.isNullOrEmpty()){
                    binding.otp.setText(smsCode)
                }
                signInWithPhoneAuthCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {

                if (e is FirebaseAuthInvalidCredentialsException) {

                } else if (e is FirebaseTooManyRequestsException) {

                }


            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                mVerificationId = verificationId
                mResendToken = token
            }
        }


    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
         val mAuth= FirebaseAuth.getInstance()
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener {
                if(it.isSuccessful){
                    val intent = Intent(this, SignUpactivity::class.java).apply{
                    }
                    startActivity(intent)

                }else{
                    notifyUserAndRetry("Your Phone number verification failed.Try Again !!")
                }
            }
    }

    private fun notifyUserAndRetry(message: String) {
        MaterialAlertDialogBuilder(this).apply {
            setMessage(message)
            setPositiveButton("OK"){_,_->
                showloginActivity()
            }
            setNegativeButton("Cancel"){dialog,_ ->
                dialog.dismiss()
            }
            setCancelable(false)
            create()
            show()
        }
    }

    private fun setSpannablestring() {
        val span = SpannableString(getString(R.string.waiting_text, phoneNumber))
        val clickableSpan = object : ClickableSpan1() {
            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText=false
                ds.color=ds.linkColor
            }

            override fun onClick(widget: View) {
                showloginActivity()


            }
        }
        span.setSpan(clickableSpan,span.length-13,span.length,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding.wno.movementMethod=LinkMovementMethod.getInstance()
    }

    private fun showloginActivity() {
        val intent = Intent(this, LoginAtivity::class.java)
            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
    }

    override fun onBackPressed() {

    }

    override fun onClick(v: View?) {
        when(v){
            binding.verification-> {

                val code = binding.otp.text.toString()
                if (code.isNotEmpty() && mVerificationId.isNullOrEmpty()) {
                    progressDialog = createProgressDialog("Please wait...", false)
                    progressDialog.show()
                    val credential = PhoneAuthProvider.getCredential(mVerificationId!!, code)
                    signInWithPhoneAuthCredential(credential)
                }
            }
            binding.resend -> {
                    val code=binding.otp.text.toString()
                    if(mResendToken != null) {
                        showtimer(60000)
                        progressDialog = createProgressDialog("Sending a verification Code ", false)
                        progressDialog.show()
                        val credential = PhoneAuthProvider.getCredential(mVerificationId!!, code)
                        signInWithPhoneAuthCredential(credential)
                        val options = PhoneAuthOptions.newBuilder()
                            .setPhoneNumber(phoneNumber!!)       // Phone number to verify
                            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                            .setActivity(this)                 // Activity (for callback binding)
                            .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
                            .setForceResendingToken(mResendToken!!)
                            .build()
                        PhoneAuthProvider.verifyPhoneNumber(options)
                    }
            }
        }
    }
}
fun Context.createProgressDialog(message:String,isCancellable:Boolean):ProgressDialog {
    return ProgressDialog(this).apply {
        setCancelable(false)
        setMessage(message)
        setCanceledOnTouchOutside(false)

    }
}