package com.example.divisas.provider

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import com.example.divisas.data.AppDatabase
import com.example.divisas.data.TipoCambioDao

class DivisasContentProvider : ContentProvider() {

    companion object {
        // Authority único del ContentProvider
        const val AUTHORITY = "com.example.divisas.provider"
        // URI base para acceder a la información
        val CONTENT_URI: Uri = Uri.parse("content://$AUTHORITY/divisas")
    }

    private lateinit var tipoCambioDao: TipoCambioDao

    override fun onCreate(): Boolean {
        // Inicializa la base de datos y el DAO
        context?.let {
            val db = AppDatabase.getDatabase(it)
            tipoCambioDao = db.tipoCambioDao()
            return true
        } ?: return false
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        // Se esperan tres parámetros en la URI: moneda, fechaInicio y fechaFin
        // Ejemplo de URI:
        // content://com.example.divisas.provider/divisas?moneda=USD&fechaInicio=1650000000000&fechaFin=1650100000000
        val moneda = uri.getQueryParameter("moneda") ?: return null
        val fechaInicio = uri.getQueryParameter("fechaInicio")?.toLongOrNull() ?: return null
        val fechaFin = uri.getQueryParameter("fechaFin")?.toLongOrNull() ?: return null

        return tipoCambioDao.getExchangeRateCursor(moneda, fechaInicio, fechaFin)
    }

    override fun getType(uri: Uri): String? {
        // MIME type para múltiples elementos
        return "vnd.android.cursor.dir/vnd.$AUTHORITY.divisas"
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        throw UnsupportedOperationException("Insert not supported")
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        throw UnsupportedOperationException("Delete not supported")
    }

    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<out String>?): Int {
        throw UnsupportedOperationException("Update not supported")
    }
}
