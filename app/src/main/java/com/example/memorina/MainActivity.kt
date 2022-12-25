package com.example.memorina

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private val pairsNumber: Int = 8
    private val rowNumber: Int = 4
    private var previousCard: View? = null
    private val openCardDelay: Long = 1000
    private var openPairs: Int = 0
    private lateinit var cards: ArrayList<ImageView>
    private val cardsResources = hashMapOf(
        "p1" to R.drawable.p1,
        "p2" to R.drawable.p2,
        "p3" to R.drawable.p3,
        "p4" to R.drawable.p4,
        "p5" to R.drawable.p5,
        "p6" to R.drawable.p6,
        "p7" to R.drawable.p7,
        "p8" to R.drawable.p8,
        "faceDown" to R.drawable.face_down_card
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(applicationContext)
        layout.orientation = LinearLayout.VERTICAL

        val layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT)
        layoutParams.weight = 1f

        val onClickListener = View.OnClickListener() {
            when (previousCard) {
                null -> {
                    if (it.isClickable) {
                        openCard(cards[it.id])
                        previousCard = it
                    } else Log.d("Memorina: card", "2 cards are open already")
                }
                else -> {
                    if (previousCard?.id == it.id) {
                        faceCardDown(cards[it.id])
                        Log.d(
                            "Memorina: card",
                            "Clicked on the same one card ${it.id} -- ${previousCard?.id}"
                        )
                    } else {
                        openCards(it)

                        if (openPairs == pairsNumber) {
                            Toast
                                .makeText(applicationContext, "You won", Toast.LENGTH_LONG)
                                .show()
                        }
                    }
                    previousCard = null
                }
            }
        }

        cards = getCards(
            numberOfPairs = pairsNumber,
            params = layoutParams,
            onClickListener = onClickListener,
            shuffle = true
        )

        fillLayoutWithCards(layout, cards, rowNumber)

        setContentView(layout)
    }

    private fun openCards(it: View) {
        val nonNullPreviousCard = previousCard!!
        GlobalScope.launch(Dispatchers.Main) {

            delay(openCardDelay)
            openCard(cards[it.id])
            delay(openCardDelay)

            if (nonNullPreviousCard.tag == it.tag) {
                it.visibility = View.INVISIBLE
                it.isClickable = false
                nonNullPreviousCard.visibility = View.INVISIBLE
                nonNullPreviousCard.isClickable = false

                openPairs++
            } else {
                faceCardDown(cards[it.id])
                faceCardDown(cards[nonNullPreviousCard.id])
            }
        }
    }

    private fun fillLayoutWithCards(
        layout: LinearLayout,
        cards: ArrayList<ImageView>,
        rows: Int
    ) {
        val rowLayouts = Array(rows) { LinearLayout(applicationContext) }

        cards.forEachIndexed { idx, card -> rowLayouts[idx / rows].addView(card) }

        rowLayouts.forEach { layout.addView(it) }
    }

    private fun getCards(
        numberOfPairs: Int,
        params: LinearLayout.LayoutParams,
        onClickListener: View.OnClickListener,
        shuffle: Boolean = true
    ): ArrayList<ImageView> {
        val cards = ArrayList<ImageView>()
        val tags = cardsResources.keys.toList()

        for (pairNumber in 0 until numberOfPairs) {
            for (i in 0..1) {
                val card = ImageView(applicationContext)
                    .apply {
                        setImageResource(cardsResources[tags[pairNumber]]!!)
                        layoutParams = params
                        setOnClickListener(onClickListener)
                        tag = tags[pairNumber]
                    }

                cards.add(card)
                faceCardDown(card)
            }
        }

        cards.shuffle()
        cards.forEachIndexed { index, it -> it.id = index }

        return cards
    }

    private fun faceCardDown(card: ImageView) {
        card.setImageResource(cardsResources["faceDown"]!!)
    }

    private fun openCard(card: ImageView) {
        card.setImageResource(cardsResources[card.tag]!!)
    }


}