import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class ProductDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "products.db"
        private const val DATABASE_VERSION = 1

        const val TABLE_NAME = "products"
        const val COLUMN_ID = "id"
        const val COLUMN_NAME = "name"
        const val COLUMN_WEIGHT = "weight"
        const val COLUMN_PRICE = "price"

        private const val CREATE_TABLE = """
            CREATE TABLE $TABLE_NAME (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_NAME TEXT,
                $COLUMN_WEIGHT REAL,
                $COLUMN_PRICE REAL
            )
            """
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(CREATE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun insertProduct(name: String, weight: Double, price: Double): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NAME, name)
            put(COLUMN_WEIGHT, weight)
            put(COLUMN_PRICE, price)
        }
        return db.insert(TABLE_NAME, null, values)
    }

    fun getAllProducts(): List<Product> {
        val products = mutableListOf<Product>()
        val db = readableDatabase
        val cursor = db.query(TABLE_NAME, null, null, null, null, null, null)

        with(cursor) {
            while (moveToNext()) {
                val id = getInt(getColumnIndexOrThrow(COLUMN_ID))
                val name = getString(getColumnIndexOrThrow(COLUMN_NAME))
                val weight = getDouble(getColumnIndexOrThrow(COLUMN_WEIGHT))
                val price = getDouble(getColumnIndexOrThrow(COLUMN_PRICE))
                products.add(Product(id, name, weight, price))
            }
            close()
        }

        return products
    }

    fun updateProduct(id: Int, name: String, weight: Double, price: Double): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NAME, name)
            put(COLUMN_WEIGHT, weight)
            put(COLUMN_PRICE, price)
        }

        return db.update(TABLE_NAME, values, "$COLUMN_ID=?", arrayOf(id.toString()))
    }

    fun deleteProduct(id: Int): Int {
        val db = writableDatabase
        return db.delete(TABLE_NAME, "$COLUMN_ID=?", arrayOf(id.toString()))
    }
}

data class Product(val id: Int, val name: String, val weight: Double, val price: Double)