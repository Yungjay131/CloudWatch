package com.slyworks.authentication

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import com.slyworks.models.Outcome
import io.reactivex.rxjava3.core.Single
import java.io.ByteArrayOutputStream


/**
 *Created by Joshua Sylvanus, 8:51 AM, 4/13/2022.
 */
object ImageCompressor {
    //region Vars
    private val TAG: String? = ImageCompressor::class.simpleName

    private val MB_THRESHOLD = 5.0
    private val MB = 1024 * 1024
    //endregion

    fun compressImage(uri: Uri): Single<Outcome> =
      Single.just(getBitmapFromFile(uri))
        .map(::getByteArrayFromBitmap)
        .map(::getResult)


    private fun getBitmapFromFile(uri:Uri):Bitmap{
        var bitmap:Bitmap? = null
        try{
            if(Build.VERSION.SDK_INT < 28)
                bitmap = MediaStore.Images.Media.getBitmap(FirebaseStore.getContentResolver(), uri)
            else
                bitmap = ImageDecoder.decodeBitmap(ImageDecoder.createSource(FirebaseStore.getContentResolver(), uri))
        }catch (e:Exception){
            Log.e(TAG, "getBitmapFromFile2: failed", e )
        }

        return bitmap!!
    }

    private fun getByteArrayFromBitmap(bitmap:Bitmap):ByteArray?{
        fun _getByteArrayFromBitmap(bitmap:Bitmap, quality:Int):ByteArray{
            val stream: ByteArrayOutputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality,stream)
            return stream.toByteArray()
        }

        var imageByteArray:ByteArray? = null
        for(index in 1..10){
            var imageQuality:Int = 100/index
            var bytes:ByteArray = _getByteArrayFromBitmap(bitmap,imageQuality)

            Log.e(TAG, "getByteArrayFromBitmap: megabytes: (" + (11 - index) + "0%) " + bytes.size / MB + " MB")

            val condition = (bytes.size / MB) <= MB_THRESHOLD
            if(condition){
                imageByteArray = bytes
                break
            }
        }

        return imageByteArray
    }

    private fun getResult(byteArray: ByteArray?):Outcome =
        if(byteArray != null)
            Outcome.SUCCESS(byteArray)
        else
            Outcome.FAILURE(null)
}