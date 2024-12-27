package com.example.easyfood.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.transition.Visibility
import com.bumptech.glide.Glide
import com.example.easyfood.R
import com.example.easyfood.databinding.ActivityMealBinding
import com.example.easyfood.db.MealDatabase
import com.example.easyfood.model.Meal
import com.example.easyfood.presentation.HomeFragment
import com.example.easyfood.viewModel.MealViewModel
import com.example.easyfood.viewModel.MealViewModelFactory

class MealActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMealBinding
    private lateinit var mealId: String
    private lateinit var mealThumb: String
    private lateinit var mealName: String
    private lateinit var viewModel: MealViewModel
    private lateinit var youtubeLink: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMealBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mealDatabase = MealDatabase.getInstance(this)
        val viewModelFactory = MealViewModelFactory(mealDatabase)
        viewModel = ViewModelProvider(this, viewModelFactory)[MealViewModel::class.java]

        getMealInformationFromIntent()
        setInformationViews()
        loadingCase()

        viewModel = ViewModelProviders.of(this)[MealViewModel::class.java]
        viewModel.getMealDetails(mealId)
        observeMealDetailsLiveData()
        onYoutubeImageClick()
        onFavouriteClick()
    }

    private fun onFavouriteClick() {
        binding.btnFavourite.setOnClickListener {
            mealToSave?.let {
                viewModel.insertMeal(it)
                Toast.makeText(this, "Meal saved", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private var mealToSave : Meal? = null

    private fun observeMealDetailsLiveData() {
        viewModel.mealDetailsLiveData.observe(this, object : Observer<Meal> {
            override fun onChanged(value: Meal) {
                onResponse()
                val meal = value
                mealToSave = meal
                binding.categoriesTv.text = "Category : ${value.strCategory}"
                binding.locationTV.text = "Area : ${value.strArea}"
                binding.descriptionTV.text = value.strInstructions
                youtubeLink = value.strYoutube.toString()
            }

        })
    }

    private fun setInformationViews() {
        Glide.with(applicationContext)
            .load(mealThumb)
            .into(binding.imgMealDetail)

        binding.collapsingToolbar.title = mealName
        binding.collapsingToolbar.setCollapsedTitleTextColor(resources.getColor(R.color.white))
        binding.collapsingToolbar.setExpandedTitleColor(resources.getColor(R.color.white))
    }

    private fun getMealInformationFromIntent() {
        val intent = intent
        mealId = intent.getStringExtra(HomeFragment.MEAL_ID)!!
        mealName = intent.getStringExtra(HomeFragment.MEAL_NAME)!!
        mealThumb = intent.getStringExtra(HomeFragment.MEAL_THUMB)!!
    }

    private fun loadingCase() {
        binding.instructionTV.visibility = View.INVISIBLE
        binding.categoriesTv.visibility = View.INVISIBLE
        binding.locationTV.visibility = View.INVISIBLE
        binding.btnFavourite.visibility = View.INVISIBLE
        binding.progressIndicator.visibility = View.VISIBLE
        binding.imgYoutube.visibility = View.INVISIBLE

    }

    private fun onResponse() {
        binding.instructionTV.visibility = View.VISIBLE
        binding.categoriesTv.visibility = View.VISIBLE
        binding.locationTV.visibility = View.VISIBLE
        binding.btnFavourite.visibility = View.VISIBLE
        binding.imgYoutube.visibility = View.VISIBLE
        binding.progressIndicator.visibility = View.INVISIBLE

    }

    private fun onYoutubeImageClick() {
        binding.imgYoutube.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(youtubeLink))
            startActivity(intent)
        }
    }
}