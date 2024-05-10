package com.analysisimgnativeapp

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import com.facebook.react.bridge.NativeModule
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod

 class AnalysisImageActivity : AppCompatActivity() {
    private  val client = OkHttpClient()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_analysis_image)
        val etQuestion = findViewById<EditText>(R.id.etQuestion)
                val btnSubmit = findViewById<Button>(R.id.btnSubmit)
        val txtResponse = findViewById<TextView>(R.id.txtResponse)

        btnSubmit.setOnClickListener {
            val question = etQuestion.text.toString()
            Toast.makeText(this, question, Toast.LENGTH_SHORT).show()
            getResponse(question) { response ->
                runOnUiThread {
                    txtResponse.text = response
                }

            }
        }
    }
        fun getResponse (question: String, callback: (String) -> Unit){
            val apiKey = ""
            val url = "https://api.openai.com/v1/chat/completions"

            val requestBody = """
            {
            "model": "gpt-4-turbo",
            "max_tokens": 1000,
            "temperature": 0,
            "messages": [
                    {
                        "role": "system",
                        "content": "You are a professional analyst."
                    },
                    {
                        "role": "user",
                        "content": "$question"
                    }
		        ]
            }
        """.trimIndent()

            val request = Request.Builder()
                .url(url)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer $apiKey")
                .addHeader("Organization_ID", "org-TrMYzGfE6CEz4NLanWyeictN")
                .post(requestBody.toRequestBody("application/json".toMediaTypeOrNull()))
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.e("error","API failed", e)
                }

                override fun onResponse(call: Call, response: Response) {
                    val body = response.body?.string()
                    if(body != null) {
                        Log.v("data", body)
                    }
                    else {
                        Log.v("data", "empty")
                    }
                    val jsonObject = JSONObject(body)

                    // Get content value
                    val choices = jsonObject.getJSONArray("choices")
                    val firstChoice = choices.getJSONObject(0)
                    val message = firstChoice.getJSONObject("message")
                    val textResult = message.getString("content")
                    callback(textResult)

//                val jsonObject = JSONObject(body)
//                val jsonArray: JSONArray = jsonObject.getJSONArray("choices")
//                println(jsonArray)
//                val textResult = jsonArray.getJSONObject(0).getString("text")
//                callback(textResult)
                }
            })


        }
    }
