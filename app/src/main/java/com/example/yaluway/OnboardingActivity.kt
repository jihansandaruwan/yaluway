package com.example.yaluway

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import kotlin.math.abs

class OnboardingActivity : AppCompatActivity() {

    private val pages = listOf(
        OnboardPage(R.drawable.yaluway_onboard_help, R.string.onboard_help_title, R.string.onboard_help_body),
        OnboardPage(R.drawable.yaluway_onboard_services, R.string.onboard_services_title, R.string.onboard_services_body),
        OnboardPage(R.drawable.yaluway_onboard_market, R.string.onboard_market_title, R.string.onboard_market_body)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)

        val pager = findViewById<ViewPager2>(R.id.onboardPager)
        val btn = findViewById<Button>(R.id.btnNext)
        pager.adapter = OnboardingAdapter(pages)
        pager.setPageTransformer { page, position ->
            page.alpha = 0.25f + (1f - abs(position)) * 0.75f
            page.translationX = -position * page.width * 0.15f
            page.scaleY = 0.92f + (1f - abs(position)) * 0.08f
        }

        pager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                updateDots(position)
                btn.setText(if (position == pages.lastIndex) R.string.get_started else R.string.next)
            }
        })

        btn.setOnClickListener {
            if (pager.currentItem < pages.lastIndex) {
                pager.setCurrentItem(pager.currentItem + 1, true)
            } else {
                startActivity(Intent(this, HomeActivity::class.java))
                finish()
            }
        }
        updateDots(0)
    }

    private fun updateDots(page: Int) {
        styleDot(findViewById(R.id.dot0), page == 0)
        styleDot(findViewById(R.id.dot1), page == 1)
        styleDot(findViewById(R.id.dot2), page == 2)
    }

    private fun styleDot(dot: View, active: Boolean) {
        val params = dot.layoutParams
        params.width = resources.getDimensionPixelSize(
            if (active) R.dimen.dot_active_width else R.dimen.dot_size
        )
        params.height = resources.getDimensionPixelSize(R.dimen.dot_size)
        dot.layoutParams = params
        dot.setBackgroundResource(if (active) R.drawable.bg_dot_pill else R.drawable.bg_dot)
    }

    private data class OnboardPage(val image: Int, val title: Int, val body: Int)

    private class OnboardingAdapter(
        private val pages: List<OnboardPage>
    ) : RecyclerView.Adapter<OnboardingAdapter.Holder>() {

        class Holder(view: View) : RecyclerView.ViewHolder(view) {
            val image: ImageView = view.findViewById(R.id.imgOnboard)
            val title: TextView = view.findViewById(R.id.tvOnboardTitle)
            val body: TextView = view.findViewById(R.id.tvOnboardBody)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.page_onboarding, parent, false)
            return Holder(view)
        }

        override fun getItemCount(): Int = pages.size

        override fun onBindViewHolder(holder: Holder, position: Int) {
            val page = pages[position]
            holder.image.setImageResource(page.image)
            holder.title.setText(page.title)
            holder.body.setText(page.body)
        }
    }
}
