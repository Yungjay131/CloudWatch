package com.slyworks.authentication

import android.content.ContentResolver


/**
 *Created by Joshua Sylvanus, 10:33 AM, 4/13/2022.
 */
object FirebaseStore {
    //region Vars
    private var mContentResolver: ContentResolver? = null
    //endregion

    fun setContentResolver(cr:ContentResolver){
        mContentResolver = cr
    }

    fun getContentResolver():ContentResolver{
        return mContentResolver!!
    }

    fun nullifyContentResolver() {
        mContentResolver = null
    }
}