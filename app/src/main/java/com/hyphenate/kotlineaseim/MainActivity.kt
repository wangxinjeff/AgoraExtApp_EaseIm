package com.hyphenate.kotlineaseim

import android.app.ProgressDialog
import android.os.Bundle
import android.os.Handler
import android.view.Window
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.hyphenate.kotlineaseim.constant.EaseConstant
import com.hyphenate.kotlineaseim.permission.PermissionsManager
import com.hyphenate.kotlineaseim.permission.PermissionsResultAction
import com.hyphenate.kotlineaseim.utils.ScreenUtil
import com.hyphenate.kotlineaseim.view.ui.widget.ChatViewPager
import com.hyphenate.kotlineaseim.viewmodel.LoginViewModel
import kotlin.properties.Delegates

class MainActivity : AppCompatActivity() {

    private var dialog: ProgressDialog? = null
    private var dialogCreateTime by Delegates.notNull<Long>()
    private lateinit var handler: Handler
    companion object {
        const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.main_activity)
        requestPermissions()
        handler = Handler(this.mainLooper)
        if (EaseIM.getInstance().init(this)) {
            val container = findViewById<FrameLayout>(R.id.fragment_container)
            //测量视图宽高
            container.post {
                ScreenUtil.instance.init(this)
                ScreenUtil.instance.screenWidth = container.width
                ScreenUtil.instance.screenHeight = container.height
                val loginViewmodel: LoginViewModel =
                    ViewModelProvider(this).get(LoginViewModel::class.java)
                loginViewmodel.testObservable.observe(this, { result ->
                    if (result["errorCode"].equals("0"))
                        loginViewmodel.joinChatRoom(EaseConstant.CHATROOM_ID)
                    else
                        runOnUiThread {
                            dismissLoading()
                            Toast.makeText(
                                this@MainActivity,
                                "Login Failed:" + result["errorMsg"],
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                })
                loginViewmodel.joinObservable.observe(this, { result ->
                    runOnUiThread {
                        dismissLoading()
                        if (result["errorCode"].equals("0"))
                            Toast.makeText(this@MainActivity, "Join Success!!!", Toast.LENGTH_SHORT)
                                .show()
                        else
                            Toast.makeText(
                                this@MainActivity,
                                "Join Failed:" + result["errorMsg"],
                                Toast.LENGTH_SHORT
                            ).show()
                    }
                })


//        testViewmodel.loginObservable.observe(this, {
//            Log.e("loginObservable:", it.toString())
//        })
//
                loginViewmodel.login("easemob", "1")
                showLoading()
            }


            val fragment = ChatViewPager()
            supportFragmentManager.beginTransaction().replace(R.id.fragment_container, fragment)
                .commit()
        }
    }

    private fun requestPermissions() {
        PermissionsManager.getInstance()
            .requestAllManifestPermissionsIfNecessary(this, object : PermissionsResultAction() {
                override fun onGranted() {

                }

                override fun onDenied(permission: String?) {

                }
            })
    }

    private fun showLoading(){
        if(dialog?.isShowing == true)
            dialog?.dismiss()
        dialogCreateTime = System.currentTimeMillis()
        dialog = ProgressDialog(this, R.style.Dialog_Light)
        dialog?.setMessage("加载中")
        dialog?.setCancelable(false)
        dialog?.show()
    }

    private fun dismissLoading(){
        if(dialog?.isShowing == true){
            if(System.currentTimeMillis() - dialogCreateTime < 500){
                handler.postDelayed(Runnable {
                    if(dialog?.isShowing == true){
                        dialog?.dismiss()
                        dialog = null
                    }
                }, 1000)
            }else{
                dialog?.dismiss()
                dialog = null
            }
        }
    }
}