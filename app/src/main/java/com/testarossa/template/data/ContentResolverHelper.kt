package com.testarossa.template.data

import android.content.Context
import android.database.Cursor
import android.provider.MediaStore
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject


class ContentResolverHelper @Inject
constructor(@ApplicationContext val context: Context) {
    private var mCursor: Cursor? = null

    private val projection: Array<String> = arrayOf(
        MediaStore.Images.Media._ID,
        MediaStore.Images.Media.BUCKET_ID,
        MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
    )

    private var selectionClause: String =
        MediaStore.Images.Media.MIME_TYPE + "=? or " + MediaStore.Images.Media.MIME_TYPE + "=? or " + MediaStore.Images.Media.MIME_TYPE + "=?"
    private var selectionArg = arrayOf("image/jpeg", "image/png", "image/jpg")
    private val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"


    /*@WorkerThread
    fun getGalleryData(): Gallery {
        return getCursorData()
    }


    private fun getCursorData(): Gallery {
        val buckets = mutableListOf<Bucket>()
        val photos = mutableListOf<Photo>()
        mCursor = context.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            selectionClause,
            selectionArg,
            sortOrder
        )

        mCursor?.use { cursor ->
            val idColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val idBucketColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_ID)
            val bucketNameColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val bucketId = cursor.getLong(idBucketColumn)
                val bucketName = cursor.getStringOrNull(bucketNameColumn) ?: "0"
                val contentUri = ContentUris.withAppendedId(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    id
                )
                photos += Photo(id = id, uri = contentUri, bucket = bucketName, bucketId = bucketId)
            }
        }

        val map = photos.groupBy { photo -> photo.bucketId }
        map.keys.forEach { bucketId ->
            val list = map[bucketId]
            if (!list.isNullOrEmpty()) {
                buckets += Bucket(
                    id = bucketId,
                    name = list.first().bucket,
                    size = list.size,
                    list.first().uri
                )
            }
        }

        val defaultBucketName = context.getString(R.string.default_bucket)
        buckets.add(
            0,
            Bucket(
                id = 0,
                name = defaultBucketName,
                size = photos.size,
                if (photos.isEmpty()) Uri.EMPTY else photos[0].uri
            )
        )

        return Gallery(buckets, photos)
    }*/


}