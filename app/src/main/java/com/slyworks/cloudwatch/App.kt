package com.slyworks.cloudwatch

import android.annotation.SuppressLint
import android.app.Application
import android.content.ContentResolver
import android.content.Context


/**
 *Created by Joshua Sylvanus, 6:55 PM, 4/2/2022.
 */
class App : Application() {
    companion object{
        @SuppressLint("StaticFieldLeak")
        private var mContext: Context? = null
        private var mContentResolver: ContentResolver? = null

        fun getContext(): Context { return mContext!! }

        fun getContentResolver(): ContentResolver { return mContentResolver!! }
        fun setContentResolver(cr: ContentResolver?){ this.mContentResolver = cr!! }
        fun nullifyContentResolver(){ this.mContentResolver = null }

    }

    override fun onCreate() {
        super.onCreate()
        setContext()
    }


    private fun setContext(){
        mContext = this;
    }

}