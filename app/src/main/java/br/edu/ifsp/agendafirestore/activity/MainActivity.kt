package br.edu.ifsp.agendafirestore.activity

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.edu.ifsp.agendafirestore.R
import br.edu.ifsp.agendafirestore.adapter.ContatoAdapter
import br.edu.ifsp.agendafirestore.model.Contato
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    lateinit var contatoAdapter: ContatoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener {
            val intent = Intent(applicationContext, CadastroActivity::class.java)
            startActivity(intent)
        }
    }

    private fun updateUI() {
        val db = Firebase.firestore
        val query: Query = db.collection("contatos").orderBy("nome")
        val options: FirestoreRecyclerOptions<Contato> = FirestoreRecyclerOptions.Builder<Contato>()
            .setQuery(query, Contato::class.java).build()

        contatoAdapter = ContatoAdapter(options)

        val recyclerview = findViewById<RecyclerView>(R.id.recyclerview)
        recyclerview.layoutManager = LinearLayoutManager(this)
        recyclerview.adapter = contatoAdapter

        val clickListener = object : ContatoAdapter.ContatoClickListener {
            override fun onItemClick(pos: Int) {
                val intent = Intent(applicationContext, DetalheActivity::class.java)
                intent.putExtra("contatoID", contatoAdapter.snapshots.getSnapshot(pos).id)
                startActivity(intent)
            }

            override fun onImageClick(pos: Int) {
                var fav: Boolean? = contatoAdapter.snapshots.getSnapshot(pos).getBoolean("favorito")
                if (fav == null) fav = false
                db.collection("contatos").document(contatoAdapter.snapshots
                    .getSnapshot(pos).id).update("favorito", if (fav) false else true)
            }
        }

        contatoAdapter.clickListener = clickListener
    }

    override fun onStart() {
        super.onStart()
        updateUI()
        contatoAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        contatoAdapter.stopListening()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)

        val item = menu?.findItem(R.id.action_search)
        val searchView = item?.actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                TODO("Not yet implemented")
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                if (p0 != "") {
                    contatoAdapter.stopListening()
                    val query: Query = FirebaseFirestore.getInstance().collection("contatos")
                        .whereGreaterThanOrEqualTo("nome", p0.toString())
                        .whereLessThanOrEqualTo("nome", p0.toString() + "\uf8ff")

                    val newOptions: FirestoreRecyclerOptions<Contato> = FirestoreRecyclerOptions
                        .Builder<Contato>()
                        .setQuery(query, Contato::class.java).build()

                    contatoAdapter.updateOptions(newOptions)
                    contatoAdapter.notifyDataSetChanged()
                    contatoAdapter.startListening()
                } else {
                    contatoAdapter.stopListening()
                    updateUI()
                    contatoAdapter.startListening()
                }

                return true
            }
        })

        return super.onCreateOptionsMenu(menu)
    }
}