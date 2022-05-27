package com.example.newsapp


import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView

import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request

import com.android.volley.toolbox.JsonObjectRequest

import androidx.core.content.ContextCompat
import androidx.core.view.isVisible


class MainActivity : AppCompatActivity(), NewsItemClicked {

    private lateinit var mAdapter: NewsListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        fetchData()

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        mAdapter = NewsListAdapter(this)
        recyclerView.adapter = mAdapter

    }

    private fun fetchData() {

        val reloadButton: View = findViewById(R.id.reloadButton)
        val error: TextView = findViewById(R.id.error)

        val url =
            "https://newsdata.io/api/1/news?apikey=pub_11854c45367786ed792a951d2bd5f794c2b5&language=en&country=in"

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            {

                if (reloadButton.isVisible) {
                    reloadButton.visibility = View.GONE
                    error.visibility = View.GONE
                }

                val newsJsonArray = it.getJSONArray("results")
                val newsArray = ArrayList<News>()
                for (i in 0 until newsJsonArray.length()) {
                    val newsJsonObject = newsJsonArray.getJSONObject(i)
                    val news = News(
                        newsJsonObject.getString("title"),
                        newsJsonObject.getString("source_id"),
                        newsJsonObject.getString("link"),
                        newsJsonObject.getString("image_url"),
                        newsJsonObject.getString("description"),
                        newsJsonObject.getString("pubDate")
                    )
                    newsArray.add(news)
                }
                mAdapter.updateNews(newsArray)
            },
            {
                reloadButton.visibility = View.VISIBLE
                error.visibility = TextView.VISIBLE
            })

        MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest)
    }

    override fun onItemClicked(item: News) {

        val builder = CustomTabsIntent.Builder()
        builder.setToolbarColor(
            ContextCompat.getColor(
                this@MainActivity,
                R.color.blue2
            )
        )
        builder.addDefaultShareMenuItem()

        val customTabsIntent = builder.build()
        customTabsIntent.launchUrl(this, Uri.parse(item.url))
    }


    fun reload(view: View) {
        fetchData()
    }
}

