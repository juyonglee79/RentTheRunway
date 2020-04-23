package rent.the.runway.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import rent.the.runway.R

package com.android.dutchman.ui.activity.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import com.android.dutchman.R
import com.android.dutchman.ui.fragment.mypage.MyPageFragment
import com.android.dutchman.ui.fragment.myroom.MyRoomFragment
import com.android.dutchman.ui.fragment.notice.NoticeFragment
import com.android.dutchman.ui.fragment.posting.PostingFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.Subject
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.toast

class MainActivity : AppCompatActivity() {

    private val backButtonSubject: Subject<Long> =
        BehaviorSubject.createDefault(0L)
            .toSerialized()

    private val backButtonSubjectDisposable: Disposable = backButtonSubject
        .buffer(2, 1)
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe {
            if (it[1] - it[0] <= 1500) finish()
            else toast("뒤로가기 버튼을 한 번 더 누르시면 종료됩니다.")
        }

    override fun onBackPressed() {
        if (main_bottom_navigation.selectedItemId == R.id.navigation_posting)
            backButtonSubject.onNext(System.currentTimeMillis())
        else main_bottom_navigation.selectedItemId = R.id.navigation_posting
    }

    private val navigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        val transaction = supportFragmentManager.beginTransaction()
        when (item.itemId) {
            R.id.navigation_posting -> {
                transaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right)
                transaction.replace(R.id.main_container, PostingFragment())
                transaction.commit()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_myroom -> {
                transaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right)
                transaction.replace(R.id.main_container, MyRoomFragment())
                transaction.commit()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_alert -> {
                transaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right)
                transaction.replace(R.id.main_container, NoticeFragment())
                transaction.commit()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_mypage -> {
                transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
                transaction.replace(R.id.main_container, MyPageFragment())
                transaction.commit()
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportFragmentManager.beginTransaction().run {
            replace(R.id.main_container, PostingFragment())
            commit()
        }
        main_bottom_navigation.setOnNavigationItemSelectedListener(navigationItemSelectedListener)
    }

    override fun onDestroy() {
        super.onDestroy()
        backButtonSubjectDisposable.dispose()
    }
}
