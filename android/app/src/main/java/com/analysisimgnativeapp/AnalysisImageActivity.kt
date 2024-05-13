package com.analysisimgnativeapp

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
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
import java.io.File

class AnalysisImageActivity : AppCompatActivity() {
     private val client = OkHttpClient()
     private lateinit var captureIV : ImageView
     private lateinit var imageUrl : Uri

     private val contract = registerForActivityResult(ActivityResultContracts.TakePicture()){
         captureIV.setImageURI(null)
         captureIV.setImageURI(imageUrl)
         println("imageUrl::"+ imageUrl)
     }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_analysis_image)

        imageUrl = createImageUri()
        captureIV = findViewById(R.id.captureImageView)
        val captureImgBtn = findViewById<Button>(R.id.captureImgBtn)
        captureImgBtn.setOnClickListener{
            contract.launch(imageUrl)
        }


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

     private fun createImageUri(): Uri {
         val image = File(filesDir,"camera_photo.jpg")
         return FileProvider.getUriForFile(this, "com.analysisimgnativeapp.FileProvider", image)
     }
        fun getResponse (question: String, callback: (String) -> Unit){
            val apiKey = ""
            val url = "https://api.openai.com/v1/chat/completions"

            val requestBody = """
            {
                "model": "gpt-4-turbo",
                "max_tokens": 2000,
                "temperature": 0,
                "messages": [
                    {
                       "role": "system",
                       "content": "You are a professional analyst"
                    },
                    {
                        "role": "user",
                        "content": "$question"
                    }
                ]
            }
        """.trimIndent()

//            "content": "$question"
//            "content": [
//            {
//                "type": "text",
//                "text": "$question"
//            },
//            {
//                "type": "image_url",
//                "image_url": {
//                "url": "https://i.pinimg.com/736x/e1/11/76/e11176f2caf121fa8dbd8c6a6b660efd.jpg"
//            }
//            }
//            ]
//        }
//    ]

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
