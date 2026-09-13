package com.example.yaluway

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.yaluway.adapter.RequestAdapter
import com.example.yaluway.data.PostRepository

class RequestsActivity : AppCompatActivity() {

    private lateinit var incomingAdapter: RequestAdapter
    private lateinit var sentAdapter: RequestAdapter
    private var filterPostId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_requests)
        filterPostId = intent.getIntExtra(LoginActivity.EXTRA_POST_ID, -1)
        findViewById<ImageButton>(R.id.btnBack).setOnClickListener { finish() }

        incomingAdapter = RequestAdapter(emptyList(), incoming = true, onAccept = { request ->
            PostRepository.setRequestStatus(request.id, "ACCEPTED")
            Toast.makeText(this, getString(R.string.request_accepted_toast), Toast.LENGTH_SHORT).show()
            refresh()
        }, onDecline = { request ->
            PostRepository.setRequestStatus(request.id, "DECLINED")
            Toast.makeText(this, getString(R.string.request_declined_toast), Toast.LENGTH_SHORT).show()
            refresh()
        })
        sentAdapter = RequestAdapter(emptyList(), incoming = false, onAccept = {}, onDecline = {})

        findViewById<RecyclerView>(R.id.rvIncoming).apply {
            layoutManager = LinearLayoutManager(this@RequestsActivity)
            adapter = incomingAdapter
        }
        findViewById<RecyclerView>(R.id.rvSent).apply {
            layoutManager = LinearLayoutManager(this@RequestsActivity)
            adapter = sentAdapter
        }
        refresh()
    }

    override fun onResume() {
        super.onResume()
        refresh()
    }

    private fun refresh() {
        val incoming = PostRepository.incomingRequests().filter {
            filterPostId <= 0 || it.postId == filterPostId
        }
        val sent = PostRepository.sentRequests()
        incomingAdapter.submitList(incoming)
        sentAdapter.submitList(sent)
        findViewById<TextView>(R.id.tvIncomingEmpty).visibility =
            if (incoming.isEmpty()) View.VISIBLE else View.GONE
        findViewById<TextView>(R.id.tvSentEmpty).visibility =
            if (sent.isEmpty()) View.VISIBLE else View.GONE
        findViewById<RecyclerView>(R.id.rvIncoming).visibility =
            if (incoming.isEmpty()) View.GONE else View.VISIBLE
        findViewById<RecyclerView>(R.id.rvSent).visibility =
            if (sent.isEmpty()) View.GONE else View.VISIBLE
    }
}
