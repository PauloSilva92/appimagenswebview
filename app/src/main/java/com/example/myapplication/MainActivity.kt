package com.example.myapplication

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.webkit.JavascriptInterface
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebChromeClient.FileChooserParams
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    private var filePathCallback: ValueCallback<Array<Uri>>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        webView = findViewById(R.id.webView)

        webView.settings.javaScriptEnabled = true
        webView.settings.allowFileAccess = true
        webView.settings.allowContentAccess = true
        WebView.setWebContentsDebuggingEnabled(true)

        webView.webViewClient = object : WebViewClient(){
            override fun shouldOverrideUrlLoading(view: WebView?, url: String): Boolean {
                view?.loadUrl(url)
                return true
            }
        }

        webView.webChromeClient = object : WebChromeClient(){

            override fun onShowFileChooser(
                webView: WebView?,
                filePathCallback: ValueCallback<Array<Uri>>?,
                fileChooserParams: FileChooserParams?
            ): Boolean {

                this@MainActivity.filePathCallback = filePathCallback

                Intent(Intent.ACTION_GET_CONTENT).apply {
                    type = "image/*"
                    putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                    startActivityForResult(this, 0)
                }

                return true
            }
        }

        webView.loadUrl("http://10.0.2.2:8080")
    }




    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {


        if (requestCode == 0 && resultCode == RESULT_OK) {
            data?.let {
                if (it.clipData != null) {
                    val count = it.clipData!!.itemCount
                    val uris = Array(count) { i -> it.clipData!!.getItemAt(i).uri }
                    filePathCallback?.onReceiveValue(uris)
                    return
                } else {
                    val uri = it.data
                    val uris = arrayOf(uri!!)
                    filePathCallback?.onReceiveValue(uris)
                }

            }

        }

        super.onActivityResult(requestCode, resultCode, data)
    }
}