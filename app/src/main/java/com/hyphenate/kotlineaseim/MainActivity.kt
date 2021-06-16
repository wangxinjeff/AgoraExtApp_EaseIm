package com.hyphenate.kotlineaseim

import android.os.Bundle
import android.util.Log
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
import com.hyphenate.kotlineaseim.viewmodel.TestViewModel
import com.hyphenate.util.EMLog

class MainActivity : AppCompatActivity() {

    companion object {
        const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.main_activity)
        requestPermissions()
        if (EaseIM.getInstance().init(this)) {
            val container = findViewById<FrameLayout>(R.id.fragment_container)
            //测量视图宽高
            container.post {
                ScreenUtil.instance.init(this)
                ScreenUtil.instance.screenWidth = container.width
                ScreenUtil.instance.screenHeight = container.height
                val testViewmodel: TestViewModel =
                    ViewModelProvider(this).get(TestViewModel::class.java)
                testViewmodel.testObservable.observe(this, {
                    if (it["errorCode"].equals("0"))
                        testViewmodel.joinChatRoom(EaseConstant.CHATROOM_ID)
                    else
                        runOnUiThread(Runnable {
                            Toast.makeText(
                                this@MainActivity,
                                "Login Failed:" + it["errorMsg"],
                                Toast.LENGTH_SHORT
                            ).show()
                        })
                })
                testViewmodel.joinObservable.observe(this, {
                    runOnUiThread(Runnable {
                        if (it["errorCode"].equals("0"))
                            Toast.makeText(this@MainActivity, "Join Success!!!", Toast.LENGTH_SHORT)
                                .show()
                        else
                            Toast.makeText(
                                this@MainActivity,
                                "Join Failed:" + it["errorMsg"],
                                Toast.LENGTH_SHORT
                            ).show()
                    })
                })


//        testViewmodel.loginObservable.observe(this, {
//            Log.e("loginObservable:", it.toString())
//        })
//
                testViewmodel.login("easemob", "1")
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
}