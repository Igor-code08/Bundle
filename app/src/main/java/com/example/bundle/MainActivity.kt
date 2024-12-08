import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.bundle.R

class MainActivity : AppCompatActivity() {

    private lateinit var dbHelper: ProductDatabaseHelper
    private lateinit var etProductName: EditText
    private lateinit var etProductWeight: EditText
    private lateinit var etProductPrice: EditText
    private lateinit var btnSave: Button
    private lateinit var btnUpdate: Button
    private lateinit var btnDelete: Button
    private lateinit var lvProducts: ListView

    private var selectedProductId: Int? = null
    private val productList = mutableListOf<Product>()
    private lateinit var productAdapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Инициализация базы данных
        dbHelper = ProductDatabaseHelper(this)

        // Инициализация UI-компонентов
        etProductName = findViewById(R.id.etProductName)
        etProductWeight = findViewById(R.id.etProductWeight)
        etProductPrice = findViewById(R.id.etProductPrice)
        btnSave = findViewById(R.id.btnSave)
        btnUpdate = findViewById(R.id.btnUpdate)
        btnDelete = findViewById(R.id.btnDelete)
        lvProducts = findViewById(R.id.lvProducts)

        // Настройка адаптера для ListView
        productAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, mutableListOf())
        lvProducts.adapter = productAdapter

        // Загрузка данных из базы и обновление списка
        loadProducts()

        // Обработчики кнопок
        btnSave.setOnClickListener { saveProduct() }
        btnUpdate.setOnClickListener { updateProduct() }
        btnDelete.setOnClickListener { deleteProduct() }

        // Обработчик выбора элемента списка
        lvProducts.setOnItemClickListener { _, _, position, _ ->
            val product = productList[position]
            selectedProductId = product.id
            etProductName.setText(product.name)
            etProductWeight.setText(product.weight.toString())
            etProductPrice.setText(product.price.toString())
            Toast.makeText(this, "Выбрано: ${product.name}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveProduct() {
        val name = etProductName.text.toString().trim()
        val weight = etProductWeight.text.toString().toDoubleOrNull()
        val price = etProductPrice.text.toString().toDoubleOrNull()

        if (name.isEmpty() || weight == null || price == null) {
            Toast.makeText(this, "Введите корректные данные", Toast.LENGTH_SHORT).show()
            return
        }

        val id = dbHelper.insertProduct(name, weight, price)
        if (id > 0) {
            Toast.makeText(this, "Продукт добавлен", Toast.LENGTH_SHORT).show()
            clearFields()
            loadProducts()
        } else {
            Toast.makeText(this, "Ошибка добавления", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateProduct() {
        val id = selectedProductId ?: run {
            Toast.makeText(this, "Выберите продукт для изменения", Toast.LENGTH_SHORT).show()
            return
        }

        val name = etProductName.text.toString().trim()
        val weight = etProductWeight.text.toString().toDoubleOrNull()
        val price = etProductPrice.text.toString().toDoubleOrNull()

        if (name.isEmpty() || weight == null || price == null) {
            Toast.makeText(this, "Введите корректные данные", Toast.LENGTH_SHORT).show()
            return
        }

        val rowsUpdated = dbHelper.updateProduct(id, name, weight, price)
        if (rowsUpdated > 0) {
            Toast.makeText(this, "Продукт обновлен", Toast.LENGTH_SHORT).show()
            clearFields()
            loadProducts()
        } else {
            Toast.makeText(this, "Ошибка обновления", Toast.LENGTH_SHORT).show()
        }
    }

    private fun deleteProduct() {
        val id = selectedProductId ?: run {
            Toast.makeText(this, "Выберите продукт для удаления", Toast.LENGTH_SHORT).show()
            return
        }

        val rowsDeleted = dbHelper.deleteProduct(id)
        if (rowsDeleted > 0) {
            Toast.makeText(this, "Продукт удален", Toast.LENGTH_SHORT).show()
            clearFields()
            loadProducts()
        } else {
            Toast.makeText(this, "Ошибка удаления", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadProducts() {
        productList.clear()
        productList.addAll(dbHelper.getAllProducts())

        // Обновление адаптера для ListView
        val productNames = productList.map { "${it.name} - ${it.weight}г - ${it.price}₽" }
        productAdapter.clear()
        productAdapter.addAll(productNames)
        productAdapter.notifyDataSetChanged()
    }

    private fun clearFields() {
        etProductName.text.clear()
        etProductWeight.text.clear()
        etProductPrice.text.clear()
        selectedProductId = null
    }
}
