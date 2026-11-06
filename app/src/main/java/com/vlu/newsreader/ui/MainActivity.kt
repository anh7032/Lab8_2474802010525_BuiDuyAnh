package com.vlu.newsreader.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.vlu.newsreader.adapter.NewsAdapter
import com.vlu.newsreader.databinding.ActivityMainBinding
import com.vlu.newsreader.network.HttpClient
import com.vlu.newsreader.parser.NewsJsonParser
import com.vlu.newsreader.parser.ParsingStrategy
import kotlinx.coroutines.launch
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

// Student Information
// Full Name: Bùi Duy Anh
// Student ID: 2474802010525

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: NewsAdapter

    companion object {
        private const val TAG = "NewsReader_Log"
        private const val API_ENDPOINT = "https://jsonplaceholder.typicode.com/posts"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupListeners()

        // Fetch data initially on app start
        fetchNews()
    }

    private fun setupRecyclerView() {
        adapter = NewsAdapter()
        binding.rvNews.adapter = adapter
    }

    private fun setupListeners() {
        // Fetch button
        binding.btnFetch.setOnClickListener {
            fetchNews()
        }

        // Retry button in error state
        binding.btnRetry.setOnClickListener {
            fetchNews()
        }

        // RadioGroup strategy selection change
        binding.rgStrategy.setOnCheckedChangeListener { _, _ ->
            fetchNews()
        }

        // Debug challenge button (Step 6)
        binding.btnDebugChallenge.setOnClickListener {
            showDebugChallengeDialog()
        }
    }

    /**
     * Executes asynchronous network request inside lifecycleScope.launch.
     * Uses withContext(Dispatchers.IO) inside HttpClient to ensure ZERO UI thread blocking.
     */
    private fun fetchNews() {
        showLoadingState()

        val strategy = if (binding.rbGson.isChecked) {
            ParsingStrategy.GSON
        } else {
            ParsingStrategy.MANUAL
        }

        lifecycleScope.launch {
            val startTime = System.currentTimeMillis()
            try {
                Log.d(TAG, "Starting HTTP GET request to $API_ENDPOINT using $strategy strategy")

                // Step 3: Network request executed on Dispatchers.IO via HttpClient
                val rawJson = HttpClient.get(API_ENDPOINT)

                // Step 4: Dual JSON Parsing Strategy
                val newsList = NewsJsonParser.parse(rawJson, strategy)
                val duration = System.currentTimeMillis() - startTime

                Log.d(TAG, "Successfully fetched and parsed ${newsList.size} posts in ${duration}ms")

                // Step 5: Update UI with parsed items
                showSuccessState(newsList.size, strategy.name, duration)
                adapter.submitList(newsList)

            } catch (e: Exception) {
                val duration = System.currentTimeMillis() - startTime
                Log.e(TAG, "Error fetching/parsing news after ${duration}ms: ${e.message}", e)
                handleNetworkError(e)
            }
        }
    }

    /**
     * Handles network errors gracefully, especially when Airplane Mode is ON or No Internet.
     */
    private fun handleNetworkError(throwable: Throwable) {
        val errorMessage = when (throwable) {
            is UnknownHostException -> {
                "Không thể phân giải địa chỉ máy chủ!\n(Bạn đang tắt mạng hoặc đang bật Chế độ máy bay - Airplane Mode)"
            }
            is SocketTimeoutException -> {
                "Kết nối tới máy chủ quá thời gian (Timeout)!\nVui lòng kiểm tra lại đường truyền Internet."
            }
            is ConnectException -> {
                "Không thể kết nối đến máy chủ.\nVui lòng kiểm tra lại kết nối mạng."
            }
            is IOException -> {
                "Lỗi I/O mạng: ${throwable.localizedMessage}\nVui lòng kiểm tra kết nối mạng."
            }
            else -> {
                "Đã xảy ra lỗi không xác định:\n${throwable.localizedMessage}"
            }
        }
        showErrorState(errorMessage)
    }

    private fun showLoadingState() {
        binding.layoutLoading.visibility = View.VISIBLE
        binding.rvNews.visibility = View.GONE
        binding.layoutError.visibility = View.GONE
        binding.tvStatus.text = "Đang tải dữ liệu từ máy chủ..."
        binding.btnFetch.isEnabled = false
    }

    private fun showSuccessState(count: Int, strategyName: String, durationMs: Long) {
        binding.layoutLoading.visibility = View.GONE
        binding.rvNews.visibility = View.VISIBLE
        binding.layoutError.visibility = View.GONE
        binding.tvStatus.text = "Đã tải $count bài viết ($strategyName Strategy) trong ${durationMs}ms"
        binding.btnFetch.isEnabled = true
    }

    private fun showErrorState(message: String) {
        binding.layoutLoading.visibility = View.GONE
        binding.rvNews.visibility = View.GONE
        binding.layoutError.visibility = View.VISIBLE
        binding.tvErrorMessage.text = message
        binding.tvStatus.text = "Lỗi khi tải dữ liệu!"
        binding.btnFetch.isEnabled = true

        Snackbar.make(binding.root, "Không thể tải dữ liệu mạng!", Snackbar.LENGTH_LONG)
            .setAction("Thử lại") { fetchNews() }
            .show()
    }

    /**
     * Dialog to test and reproduce the Intentional Debugging Challenges (Step 6).
     */
    private fun showDebugChallengeDialog() {
        val options = arrayOf(
            "1. Tái hiện NetworkOnMainThreadException (Chạy mạng trên UI thread)",
            "2. Xem hướng dẫn tái hiện SecurityException (Xóa quyền INTERNET)"
        )

        AlertDialog.Builder(this)
            .setTitle("Step 6: Intentional Debugging")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> triggerNetworkOnMainThreadException()
                    1 -> showSecurityExceptionInstruction()
                }
            }
            .setNegativeButton("Đóng", null)
            .show()
    }

    /**
     * Intentionally executes raw HTTP connection directly on Main UI Thread.
     * This reproduces android.os.NetworkOnMainThreadException safely for Step 6 observation.
     */
    private fun triggerNetworkOnMainThreadException() {
        try {
            Log.w("INTENTIONAL_DEBUG", "Executing raw network call directly on Main Thread...")
            // Directly call synchronous method without coroutine or background thread
            HttpClient.getSyncRaw(API_ENDPOINT)
        } catch (e: Exception) {
            Log.e("INTENTIONAL_DEBUG", "CRAUGHT INTENTIONAL EXCEPTION: ${e.javaClass.name}", e)
            AlertDialog.Builder(this)
                .setTitle("Đã bắt được ngoại lệ cố ý!")
                .setMessage("Tên lỗi: ${e.javaClass.name}\n\nStack Trace:\n${Log.getStackTraceString(e)}")
                .setPositiveButton("OK", null)
                .show()
        }
    }

    private fun showSecurityExceptionInstruction() {
        AlertDialog.Builder(this)
            .setTitle("Tái hiện SecurityException")
            .setMessage("Để tái hiện java.lang.SecurityException:\n\n1. Mở AndroidManifest.xml\n2. Xóa hoặc comment dòng: <uses-permission android:name=\"android.permission.INTERNET\" />\n3. Chạy lại app -> Hệ điều hành Android sẽ ném java.lang.SecurityException: Permission denied (missing INTERNET permission).\n\nChi tiết đầy đủ đã được phân tích và trích xuất trong báo cáo Debugging Report PDF.")
            .setPositiveButton("Đã hiểu", null)
            .show()
    }
}
