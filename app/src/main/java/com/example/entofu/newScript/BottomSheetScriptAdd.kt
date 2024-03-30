package com.example.entofu.newScript

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.example.entofu.HomeFragment
import com.example.entofu.MainActivity
import com.example.entofu.R
import com.example.entofu.apiInterface.CreateScriptService
import com.example.entofu.databinding.BottomSheetScriptAddBinding
import com.example.entofu.updateHome
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.googlecode.tesseract.android.TessBaseAPI
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import kotlin.concurrent.thread

class BottomSheetScriptAdd : BottomSheetDialogFragment()  {

    private lateinit var mainAct : MainActivity
    lateinit var binding: BottomSheetScriptAddBinding
    var mDataPath= ""
    private lateinit var tess : TessBaseAPI

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = BottomSheetScriptAddBinding.inflate(inflater, container, false)
        mainAct = activity as MainActivity
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val shakeAnimator: Animation by lazy {
            AnimationUtils.loadAnimation(requireContext(), R.anim.shake)
        }

        // record
        binding.titleVoice.setOnClickListener {
            NewScriptRecord.record(it,requireContext(),binding.editTextTextTitle,binding.titleVoice)
        }
        binding.contentVoice.setOnClickListener {
            NewScriptRecord.record(it,requireContext(),binding.editTextTextContent,binding.contentVoice)
        }
        //------------------------------------------------

        binding.addButton.setOnClickListener {
            if(binding.editTextTextTitle.text.toString() =="")binding.editTextTextTitle.startAnimation(shakeAnimator)
            else if(binding.editTextTextContent.text.toString() =="") binding.editTextTextContent.startAnimation(shakeAnimator)
            else postMemberScript()

        }
        binding.galleryButton.setOnClickListener {

            if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
                // When permission is not granted
                // Result permission

                ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 2)
//                ActivityCompat.requestPermissions(this, arrayOf(READ_EXTERNAL_STORAGE), 1)
            }
            else {
                tesseract()
                val intent = Intent(Intent.ACTION_PICK)
                intent.type = MediaStore.Images.Media.CONTENT_TYPE
                startForResult.launch(intent)
            }

        }
        binding.FileAddButton.setOnClickListener {

            if (ActivityCompat.checkSelfPermission(requireActivity(), arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.MANAGE_EXTERNAL_STORAGE
                ).toString()) != PackageManager.PERMISSION_GRANTED) {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    ActivityCompat.requestPermissions(requireActivity(),arrayOf(
                        Manifest.permission.READ_MEDIA_AUDIO,
                        Manifest.permission.READ_MEDIA_VIDEO,
                        Manifest.permission.READ_MEDIA_IMAGES
                    ), 1)
                }
                else {
                    ActivityCompat.requestPermissions(requireActivity(), arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.MANAGE_EXTERNAL_STORAGE
                    ), 1)

                }
            }
            else {
                selectPDF()

            }
        }




    }
    private fun postMemberScript(){
        val json = JSONObject("""{"title":"${binding.editTextTextTitle.text}", "content":"${binding.editTextTextContent.text}"}""")
        thread {
            val response = CreateScriptService().scriptService.postMemberScriptRequest(memberIdx = mainAct.user.getIdx()!!,body= JsonParser.parseString(json.toString()) as JsonObject).execute()
            CoroutineScope(Dispatchers.Main).launch {
                if (response.isSuccessful) {

                    mainAct.scriptListAdapter.addMemberScriptOne(response.body()!!.data!!.idx,response.body()!!.data!!.status,"${binding.editTextTextTitle.text}",
                        "${binding.editTextTextContent.text}",resources.getString(R.string.app_image_url))
                    dismiss()
                    Log.e("postMemberScriptCheck", "success !")
                } else {
                    Log.e("postMemberScriptCheck", "response-fail!")
                    Log.e("postMemberScriptCheck", "error code : " + response.code())
                    Log.e("postMemberScriptCheck", "error message : " + response.message())
                }
            }
        }

    }
    private fun postPdfRequest(file: MultipartBody.Part){
        thread {
            val response = CreateScriptService().scriptService.postPdfRequest(memberIdx = mainAct.user.getIdx()!!,files= file).execute()
            CoroutineScope(Dispatchers.Main).launch {
                if (response.isSuccessful) {
                    updateHome =true
                    Snackbar.make(binding.root,"Uploaded successfully", Snackbar.LENGTH_LONG).show()
                    Log.e("postPdfRequest", "success ! : "+ response.body()?.string())
                } else {
                    Log.e("postPdfRequest", "response-fail!")
                    Log.e("postPdfRequest", "error code : " + response.code())
                    Log.e("postPdfRequest", "error message : " + response.message())
                }
            }
        }
    }
    private fun postTxtRequest(file: MultipartBody.Part){
        thread {
            val response = CreateScriptService().scriptService.postTxtRequest(memberIdx = mainAct.user.getIdx()!!,files= file).execute()
            CoroutineScope(Dispatchers.Main).launch {
                if (response.isSuccessful) {
                    updateHome =true
                    Snackbar.make(binding.root,"Uploaded successfully", Snackbar.LENGTH_LONG).show()
                    Log.e("postPdfRequest", "success ! : "+ response.body()?.string())
                } else {
                    Log.e("postPdfRequest", "response-fail!")
                    Log.e("postPdfRequest", "error code : " + response.code())
                    Log.e("postPdfRequest", "error message : " + response.message())
                }
            }
        }
    }
    @RequiresApi(Build.VERSION_CODES.Q)
    private fun selectPDF() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.type = "*/*"
        startForResultForPdf.launch(intent)
    }
    private val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult())
    { result : ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK){
            if(result.data != null){
                val tempFile : File
                val sUri: Uri = result.data!!.data!!
                Log.d("starForREsult", "uri :$sUri")

                val photoUri: Uri = result.data!!.data!!
                var cursor: Cursor? = null
                try {
                    //  Uri 스키마를 content:/// 에서 file:/// 로  변경
                    val proj = arrayOf(MediaStore.Images.Media.DATA)
                    cursor = mainAct.contentResolver.query(photoUri, proj, null, null, null)
                    assert(cursor != null)
                    val columnIndex: Int =
                        cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                    cursor.moveToFirst()
                    tempFile = File(cursor.getString(columnIndex))
                } finally {
                    cursor?.close()
                }

                val options = BitmapFactory.Options()
                val originalBm = BitmapFactory.decodeFile(tempFile.absolutePath, options)

                tesseract()
                processImage(originalBm)
            }
        }
    }
    @RequiresApi(Build.VERSION_CODES.Q)
    private val startForResultForPdf = registerForActivityResult(ActivityResultContracts.StartActivityForResult())
    { result : ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK){
            if(result.data != null){

                val pdfUri: Uri = result.data!!.data!!
                val path = getRealPathFromURI(pdfUri)!!
                val files = File(path)
                val requestFile : RequestBody
                val body :MultipartBody.Part
                when(path.split(".")[1]){
                    "pdf" -> {
                        Log.e("postPdfRequest","pdf file")
                        requestFile = RequestBody.create(MediaType.parse("application/pdf"), files)
                        body = MultipartBody.Part.createFormData("files", files.name, requestFile)
                        postPdfRequest(body)
                    }
                    "txt" -> {
                        Log.e("postPdfRequest", "txt selected path : $path")
                        requestFile = RequestBody.create(MediaType.parse("text/plain"), files)
                        body = MultipartBody.Part.createFormData("files", files.name, requestFile)
                        postTxtRequest(body)
                    }
                    else -> Snackbar.make(binding.root,"Only pdf and txt files are available.", Snackbar.LENGTH_LONG).show()
                }
            }
        }

    }

    @Deprecated("Deprecated in Java")
    @RequiresApi(Build.VERSION_CODES.R)
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1 && grantResults.isNotEmpty() && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
            selectPDF()
        } else if (requestCode == 2 && grantResults.isNotEmpty() && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = MediaStore.Images.Media.CONTENT_TYPE
            startForResult.launch(intent)
        } else {
            Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show()
        }
    }
    private fun tesseract() {
        mDataPath = "${mainAct.filesDir}/tesseract/"
        val lang = "eng"

        checkFile(File (mDataPath + "tessdata/"), lang)
        tess =  TessBaseAPI()
        tess.init(mDataPath, lang)

    }
    private fun processImage(bitmap: Bitmap){
        Toast.makeText(context,"recognizing", Toast.LENGTH_LONG).show()
        tess.setImage(bitmap)
        val result = tess.utF8Text ?: "can't recognize"
        binding.editTextTextContent.setText(result.replace("\""," "))
    }
    private fun checkFile(dir: File, Language: String) {
        if (!dir.exists() && dir.mkdirs()) {
            copyFiles(Language)
        }
        if (dir.exists()) {
            val datafilePath = "$mDataPath/tessdata/$Language.traineddata"
            val datafile = File(datafilePath)
            if (!datafile.exists()) {
                copyFiles(Language)
            }
        }
    }
    private fun copyFiles(Language: String) {
        try {
            val filepath = "$mDataPath/tessdata/$Language.traineddata"
            val assetManager = mainAct.assets
            val inStream: InputStream = assetManager.open("tessdata/$Language.traineddata")
            val outStream: OutputStream = FileOutputStream(filepath)
            val buffer = ByteArray(1024)
            var read: Int
            while (inStream.read(buffer).also { read = it } != -1) {
                outStream.write(buffer, 0, read)
            }
            outStream.flush()
            outStream.close()
            inStream.close()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    @SuppressLint("Recycle")
    private fun getRealPathFromURI(contentUri: Uri): String? {
        if (contentUri.path!!.startsWith("/storage")) {
            return contentUri.path
        }
        val id = DocumentsContract.getDocumentId(contentUri).split(":").toTypedArray()[1]
        val columns = arrayOf(MediaStore.Files.FileColumns.DATA)
        val selection = MediaStore.Files.FileColumns._ID + "=" + id
        val cursor = mainAct.contentResolver.query(MediaStore.Files.getContentUri("external"), null, selection, null, null)

        try {
            cursor?.moveToFirst()
            val columnIndex = cursor!!.getColumnIndex(columns[0])
            return cursor.getString(columnIndex)

        } finally {
            cursor!!.close()
        }
        return null
    }

    companion object {
        const val TAG = "BottomSheetScriptAdd"
    }
}