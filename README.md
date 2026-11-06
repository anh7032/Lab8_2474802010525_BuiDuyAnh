# LAB 15: HTTP NETWORK PROGRAMMING, JSON PARSING & ANDROID PERMISSIONS

- **Họ và tên sinh viên:** Bùi Duy Anh
- **Mã số sinh viên (MSSV):** 2474802010525
- **Môn học:** Mobile Application Programming (71ITSE30603 / 72ITSE30603) — Trường Đại học Văn Lang (VLU)
- **Tên dự án:** `NewsReader_2474802010525`
- **Public GitHub Repository:** [https://github.com/anh7032/Lab8_2474802010525_BuiDuyAnh](https://github.com/anh7032/Lab8_2474802010525_BuiDuyAnh)
- **File báo cáo đính kèm:** `Debugging_Report.pdf` (Báo cáo Debug 1 trang chuẩn A4)

---

## 1. Thông Tin Nộp Bài (Submission Metadata)

> [!IMPORTANT]
> Vì hệ thống Moodle/LMS không có ô nhập Online Text trực tiếp, toàn bộ liên kết mã nguồn công khai (Public GitHub Repository URL) và mã Commit Hash mới nhất được đính kèm trực tiếp tại đây và trong tệp `Debugging_Report.pdf`.

* **GitHub Repository URL:** `https://github.com/anh7032/Lab8_2474802010525_BuiDuyAnh`
* **Nhánh chính (Branch):** `main`
* **Quy cách đóng gói:** Tệp nén `.zip` đã được Clean Project hoàn chỉnh, dung lượng nhỏ gọn dưới 15 MB.

---

## 2. Kiến Trúc Dự Án & Các Yêu Cầu Kỹ Thuật

Dự án được cấu trúc chuẩn hóa theo Clean Package Architecture:

| Package | Tệp tin (Files) | Trách nhiệm & Điểm nhấn kỹ thuật |
| :--- | :--- | :--- |
| **`com.vlu.newsreader.model`** | `NewsItem.kt` | Data class ánh xạ 4 trường từ API JSON (`id`, `userId`, `title`, `body`) sử dụng annotation `@SerializedName`. |
| **`com.vlu.newsreader.network`** | `HttpClient.kt` | Cài đặt HTTP GET client bằng `HttpURLConnection`.<br>• Tác vụ mạng được cô lập tuyệt đối trên luồng `withContext(Dispatchers.IO)` đảm bảo **Zero UI Thread Blocking**.<br>• Cung cấp hàm đồng bộ `getSyncRaw` hỗ trợ thử thách debug cố ý. |
| **`com.vlu.newsreader.parser`** | `NewsJsonParser.kt` | **Dual JSON Parsing Strategy:**<br>1. *Manual Parsing:* Duyệt mảng `JSONArray` và lấy từng `JSONObject`.<br>2. *Automated Parsing:* Sử dụng thư viện `Gson().fromJson`. |
| **`com.vlu.newsreader.adapter`** | `NewsAdapter.kt` | Adapter hiển thị danh sách bài viết trên RecyclerView sử dụng `ItemNewsBinding`. |
| **`com.vlu.newsreader.ui`** | `MainActivity.kt` | Quản lý Activity theo vòng đời `lifecycleScope.launch`.<br>• Quản lý 3 trạng thái giao diện: Loading (`ProgressBar`), Success (`RecyclerView`), Error (`layoutError` + nút Retry).<br>• Bắt lỗi thân thiện và an toàn khi người dùng bật **Chế độ máy bay (Airplane Mode)** hoặc mất mạng. |

---

## 3. Tóm Tắt Thử Thách Debug Cố Ý (Step 6: Intentional Debugging)

Chi tiết đầy đủ với Logcat trích xuất và phân tích nguyên nhân gốc (Root Cause) xem tại tệp **`Debugging_Report.pdf`** nằm ngay trong thư mục gốc dự án:

1. **Lỗi 1 - `android.os.NetworkOnMainThreadException`:**
   * **Nguyên nhân:** Gọi trực tiếp hàm mạng trên Main UI Thread vi phạm chính sách `StrictMode` của Android (nhằm tránh đơ giao diện ANR).
   * **Khắc phục:** Đưa vào Kotlin Coroutines chạy trên `Dispatchers.IO`.
2. **Lỗi 2 - `java.lang.SecurityException (Permission denied)`:**
   * **Nguyên nhân:** Thiếu khai báo quyền `<uses-permission android:name="android.permission.INTERNET" />` trong `AndroidManifest.xml`.
   * **Khắc phục:** Thêm quyền Internet trực tiếp vào Manifest.
