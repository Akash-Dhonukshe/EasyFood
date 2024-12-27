package com.example.easyfood.presentation

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.easyfood.R
import com.example.easyfood.activities.CategoryMealActivity
import com.example.easyfood.activities.MainActivity
import com.example.easyfood.activities.MealActivity
import com.example.easyfood.adapters.CategoriesAdapter
import com.example.easyfood.adapters.PopularMealAdapter
import com.example.easyfood.databinding.FragmentHomeBinding
import com.example.easyfood.model.Category
import com.example.easyfood.model.MealsByCategory
import com.example.easyfood.model.Meal
import com.example.easyfood.viewModel.HomeViewModel


class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var viewModel: HomeViewModel
    private lateinit var randomMeal : Meal
    private lateinit var popularItemAdapter : PopularMealAdapter
    private lateinit var categoriesAdapter : CategoriesAdapter


    companion object{
        const val MEAL_ID = "com.example.easyfood.presentation.idMeal"
        const val MEAL_NAME = "com.example.easyfood.presentation.nameMeal"
        const val MEAL_THUMB = "com.example.easyfood.presentation.thumbMeal"
        const val CATEGORY_NAME = "com.example.easyfood.presentation.category"

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = (activity as MainActivity).viewModel
        popularItemAdapter = PopularMealAdapter()
        Log.d("HomeFragment", "$popularItemAdapter")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observerRandomMeal()
        onRandomMealClick()

        observePopularItemsLiveData()
        preparePopularItemRecyclerview()
        onPopularItemClick()

        observeCategoriesLiveData()
        prepareCategoriesRecyclerView()
        onCategoryClick()

        onSearchIconClick()
    }

    private fun onSearchIconClick() {
        binding.searchIV.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_searchFragment)
        }
    }

    private fun onCategoryClick() {
        categoriesAdapter.onItemClick = { category ->
            val intent = Intent(activity, CategoryMealActivity::class.java)
            intent.putExtra(CATEGORY_NAME, category.strCategory)
            startActivity(intent)
        }
    }

    private fun prepareCategoriesRecyclerView() {
        categoriesAdapter = CategoriesAdapter()
        binding.viewCategoriesRv.apply {
            layoutManager = GridLayoutManager(context, 3, GridLayoutManager.VERTICAL, false)
            adapter = categoriesAdapter
        }
    }

    private fun observeCategoriesLiveData() {
        viewModel.categoriesLiveData.observe(viewLifecycleOwner, Observer { categories ->
//            categories.forEach { category ->
//                Log.d("test", "observeCategoriesLiveData: ${category.strCategory}")
//            }
            categoriesAdapter.setCategoryList(categories)

        })
    }

    private fun onPopularItemClick() {
        popularItemAdapter.onItemClick = { meal ->
            val intent = Intent(activity, MealActivity::class.java)
            intent.putExtra(MEAL_ID, meal.idMeal)
            intent.putExtra(MEAL_NAME, meal.strMeal)
            intent.putExtra(MEAL_THUMB, meal.strMealThumb)
            startActivity(intent)
        }
    }

    private fun preparePopularItemRecyclerview() {
        binding.popularMealRv.apply {
            layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL,false)
            adapter = popularItemAdapter
        }
    }

    private fun observePopularItemsLiveData() {
        viewModel.popularItemLiveData.observe(viewLifecycleOwner) { mealList ->
            popularItemAdapter.setMeals(mealList = mealList as ArrayList<MealsByCategory>)
            Log.d("HomeFragment", "$mealList")
        }
    }


    private fun observerRandomMeal() {
        viewModel.randomMealLiveData.observe(viewLifecycleOwner) { meal ->
            Glide.with(this@HomeFragment)
                .load(meal.strMealThumb)
                .into(binding.imgRandomMeal)

            this.randomMeal = meal

        }
    }

    private fun onRandomMealClick() {
        binding.mealImgCardView.setOnClickListener {
            val intent = Intent(activity, MealActivity::class.java)
            intent.putExtra(MEAL_ID, randomMeal.idMeal)
            intent.putExtra(MEAL_NAME, randomMeal.strMeal)
            intent.putExtra(MEAL_THUMB, randomMeal.strMealThumb)
            startActivity(intent)
        }
    }

}