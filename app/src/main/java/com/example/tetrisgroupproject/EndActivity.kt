package com.example.tetrisgroupproject

import android.app.DownloadManager
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import org.w3c.dom.Text

//private fun DownloadManager.Query.addValueEventListener(listener: com.example.tetrisgroupproject.ValueEventListener) {}

class EndActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_end)

        val name = intent.getStringExtra("name")
        val score = intent.getIntExtra("score", -1)
        val level = intent.getIntExtra("level", -1)

        val nameDisplay = findViewById<TextView>(R.id.nameDisplay)
        val scoreDisplay = findViewById<TextView>(R.id.scoreDisplay)
        val levelDisplay = findViewById<TextView>(R.id.levelDisplay)

        if(name == null || score == -1 || level == -1){
            nameDisplay.visibility = View.GONE
            scoreDisplay.visibility = View.GONE
            levelDisplay.visibility = View.GONE
        }
        nameDisplay.text = "Name: $name"
        scoreDisplay.text = "Score: $score"
        levelDisplay.text = "Level: $level"

        //Take Care of Play Again and Main Menu Buttons
        findViewById<Button>(R.id.main_menu_button).setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        findViewById<Button>(R.id.play_again_button).setOnClickListener{
            val intent = Intent(this, GameActivity::class.java)
            startActivity(intent)
            finish()
        }

        //Display Leaderboard
        displayLeaderboard()
    }

    fun displayLeaderboard(){
        val lb = FirebaseDatabase.getInstance().getReference("leaderboard")
        val query = lb.orderByChild("score").limitToLast(5)

        query.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                val list = p0.children.toList().reversed()
                var index = 1

                for(i in list){
                    val name = i.key
                    val score = i.child("score").value.toString()
                    val level = i.child("level").value.toString()

                    //Maybe change if need be? Cant do findViewById... because the name doesn't
                    //work dynamically
                    val nameId = resources.getIdentifier("r${index}_name", "id", packageName)
                    val scoreId = resources.getIdentifier("r${index}_score", "id", packageName)
                    val levelId = resources.getIdentifier("r${index}_level", "id", packageName)

                    findViewById<TextView>(nameId).text = "$index. $name"
                    findViewById<TextView>(scoreId).text = score
                    findViewById<TextView>(levelId).text = level

                    index++
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }

        })

    }
}