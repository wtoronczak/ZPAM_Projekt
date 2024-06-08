package com.example.myapplication.recycleView

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.firestore.DogFirestore
import com.example.myapplication.firestore.DogFirestoreHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class DogAdapter(val dogList: MutableList<DogFirestore>) : RecyclerView.Adapter<DogAdapter.DogViewHolder>(){

    private val dogFirestoreHandler = DogFirestoreHandler()

    class DogViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val textView: TextView = itemView.findViewById(R.id.dog_name)
        val imageView: ImageView = itemView.findViewById(R.id.dog_image)
        val deleteButton: Button = itemView.findViewById(R.id.delete_dog)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DogViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.card_view_design, parent, false)
        return DogViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return dogList.size
    }

    override fun onBindViewHolder(holder: DogViewHolder, position: Int) {
        val currentDog = dogList[position]
        holder.textView.text = currentDog.name

        if(currentDog.photoUrl != ""){
            Glide.with(holder.itemView.context)
                .load(currentDog.photoUrl)
                .into(holder.imageView)
        }

        holder.deleteButton.setOnClickListener {
            removeDog(currentDog)
        }
    }

    private fun removeDog(dog: DogFirestore) {
        val position = dogList.indexOf(dog)
        if (position != -1) {
            GlobalScope.launch(Dispatchers.Main) {
                //Usuniecie psa z bazy danych Firestore
                dogFirestoreHandler.deleteDog(dog.dogId)
            }

            dogList.removeAt(position)
            notifyItemRemoved(position)
        }
    }
}