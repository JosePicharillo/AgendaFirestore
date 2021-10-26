package br.edu.ifsp.agendafirestore.activity

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import br.edu.ifsp.agendafirestore.R
import br.edu.ifsp.agendafirestore.model.Contato
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

class DetalheActivity : AppCompatActivity() {

    val db = Firebase.firestore
    lateinit var contatoID: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalhe)

        contatoID = this.intent.getStringExtra("contatoID") as String

        db.collection("contatos").document(contatoID)
            .addSnapshotListener { value, error ->
                if (value != null) {
                    val c = value.toObject<Contato>()

                    val nome = findViewById<EditText>(R.id.editTextNome)
                    val fone = findViewById<EditText>(R.id.editTextFone)
                    val email = findViewById<EditText>(R.id.editTextEmail)

                    nome.setText(c?.nome.toString())
                    fone.setText(c?.fone.toString())
                    email.setText(c?.email.toString())
                }
            }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_detalhe, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == R.id.action_alterarContato) {
            val nome = findViewById<EditText>(R.id.editTextNome).text.toString()
            val fone = findViewById<EditText>(R.id.editTextFone).text.toString()
            val email = findViewById<EditText>(R.id.editTextEmail).text.toString()

            val c = Contato(nome, fone, email)
            db.collection("contatos").document(contatoID).set(c)
            Toast.makeText(this, "Informações alteradas", Toast.LENGTH_LONG).show()
            finish()
        }

        if (item.itemId == R.id.action_excluirContato) {

            db.collection("contatos").document(contatoID).delete()
            Toast.makeText(this, "Contato excluído", Toast.LENGTH_LONG).show()
            finish()
        }

        return super.onOptionsItemSelected(item)
    }


}