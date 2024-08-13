package com.weekly.weeklychecklist

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.weekly.weeklychecklist.ui.WeeklyChecklistApp
import com.weekly.weeklychecklist.vm.CheckListViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val clVM: CheckListViewModel by viewModels()
    private val TAG = javaClass.simpleName
    var backPressedCount = 0
    var pressedTime = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //DB init
        onBackPressedDispatcher.addCallback(backPressedCallBack(this))

        setContent {
            WeeklyChecklistApp(clVM = clVM)
        }
        //splash screen
        val content = findViewById<View>(android.R.id.content)
        content.viewTreeObserver.addOnPreDrawListener (
            object : ViewTreeObserver.OnPreDrawListener{
                override fun onPreDraw(): Boolean {
                    return if(clVM.isFinished){
                        //다 되면 화면 시작
                        content.viewTreeObserver.removeOnPreDrawListener(this)
                        true
                    } else {
                        //splash 화면 중 데이터 베이스 init
                        clVM.getCheckLists()
                        clVM.isSplashed = true
                        clVM.switchInitialization(applicationContext)
                        false
                    }
                }
            }
        )
    }

    //뒤로가기 콜백
    private fun backPressedCallBack(context: Context) = object: OnBackPressedCallback(true){
        override fun handleOnBackPressed() {
            backPressedCount++
            when(backPressedCount){
                1 -> {
                    CoroutineScope(Dispatchers.Default).launch {
                        delay(2000)
                        if(backPressedCount == 2)
                            return@launch
                        else
                            backPressedCount = 0
                    }
                    pressedTime = System.currentTimeMillis()
                    Toast.makeText(context, "한 번 더 누르면 앱을 종료합니다", Toast.LENGTH_SHORT).show()
                }
                2 -> {
                    if(System.currentTimeMillis() > pressedTime + 2000){
                        backPressedCount = 0
                    } else{
                        backPressedCount = 0
                        finishAndRemoveTask()
                    }
                }
            }
        }
    }
    
    override fun onResume() {
        super.onResume()
        if(!clVM.isSplashed) {
            Log.d(javaClass.simpleName, "onResume 스위치 초기화")
            clVM.switchInitialization(this)
            clVM.onResumeRefreshed.value = !clVM.onResumeRefreshed.value
        }
    }

    override fun onPause(){
        super.onPause()
        Log.d(javaClass.simpleName, "onPause DB Insert")
        clVM.isSplashed = false
        clVM.updateCheckListAll()
//        clVM.updateTest()
    }
}

@Composable
fun rememberLifecycleEvent(lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current): Lifecycle.Event{
    var state by remember { mutableStateOf(Lifecycle.Event.ON_ANY) }
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver{_, event ->
            state = event
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
    return state
}