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
import com.hyphenate.EMError
import com.hyphenate.chat.EMClient
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
    private var loginLimit = 0
    private var joinLimit = 0
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
                loginViewmodel.registerObservable.observe(this, { result ->
                    if (result[EaseConstant.ERROR_CODE].equals("0"))
                        loginViewmodel.login(EaseConstant.USERNAME, EaseConstant.PASSWORD)
                    else
                        runOnUiThread {
                            if(result[EaseConstant.ERROR_CODE].equals(EMError.USER_ALREADY_EXIST.toString())){
                                loginViewmodel.login(EaseConstant.USERNAME, EaseConstant.PASSWORD)
                            }else{
                                dismissLoading()
                                Toast.makeText(
                                    this@MainActivity,
                                    "Register Failed:" + result[EaseConstant.ERROR_MSG],
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                })
                loginViewmodel.loginObservable.observe(this, { result ->
                    loginLimit ++
                    if (result[EaseConstant.ERROR_CODE].equals("0"))
                        loginViewmodel.joinChatRoom(EaseConstant.CHATROOM_ID)
                    else
                        runOnUiThread {
                            if(loginLimit == 2){
                                dismissLoading()
                                Toast.makeText(
                                    this@MainActivity,
                                    "Login Failed:" + result[EaseConstant.ERROR_MSG],
                                    Toast.LENGTH_SHORT
                                ).show()
                            }else
                                loginViewmodel.login(EaseConstant.USERNAME, EaseConstant.PASSWORD)
                        }
                })
                loginViewmodel.joinObservable.observe(this, { result ->
                    joinLimit ++
                    runOnUiThread {
                        dismissLoading()
                        if (result[EaseConstant.ERROR_CODE].equals("0")) {
                            Toast.makeText(this@MainActivity, "Join Success!!!", Toast.LENGTH_SHORT)
                                .show()
                            val fragment = ChatViewPager()
                            supportFragmentManager.beginTransaction().replace(R.id.fragment_container, fragment)
                                .commit()
                        }else{
                            if(joinLimit == 2){
                                Toast.makeText(
                                    this@MainActivity,
                                    "Join Failed:" + result[EaseConstant.ERROR_MSG],
                                    Toast.LENGTH_SHORT
                                ).show()
                            }else
                                loginViewmodel.joinChatRoom(EaseConstant.CHATROOM_ID)
                        }


                    }
                })

                loginViewmodel.createUser(EaseConstant.USERNAME, EaseConstant.PASSWORD)
                showLoading()
            }
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

    /**
     * 显示加载中
     */
    private fun showLoading(){
        if(dialog?.isShowing == true)
            dialog?.dismiss()
        dialogCreateTime = System.currentTimeMillis()
        dialog = ProgressDialog(this, R.style.Dialog_Light)
        dialog?.setMessage("加载中")
        dialog?.setCancelable(false)
        dialog?.show()
    }

    /**
     * 取消显示
     */
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

    override fun onDestroy() {
        super.onDestroy()
        EMClient.getInstance().chatroomManager().leaveChatRoom(EaseConstant.CHATROOM_ID)
        EMClient.getInstance().logout(false)
        handler.removeCallbacksAndMessages(null)
    }
}