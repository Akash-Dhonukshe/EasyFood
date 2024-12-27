package com.example.easyfood.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.easyfood.db.MealDatabase
import com.example.easyfood.model.Category
import com.example.easyfood.model.CategoryList
import com.example.easyfood.model.MealsByCategoryList
import com.example.easyfood.model.MealsByCategory
import com.example.easyfood.model.Meal
import com.example.easyfood.model.MealList
import com.example.easyfood.retrofit.RetrofitInstance
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeViewModel(
    private val mealDatabase: MealDatabase
) : ViewModel() {
    private var _randomMealLiveData = MutableLiveData<Meal>()
    val randomMealLiveData : LiveData<Meal> get() = _randomMealLiveData

    private var _popularItemLiveData = MutableLiveData<List<MealsByCategory>>()
    val popularItemLiveData : LiveData<List<MealsByCategory>> get() = _popularItemLiveData

    private var _categoriesLiveData = MutableLiveData<List<Category>>()
    val categoriesLiveData : LiveData<List<Category>> get() = _categoriesLiveData

    private var _favouriteMealsLiveData = mealDatabase.mealDao().getAllMeals()
    val favouriteMealsLiveData : LiveData<List<Meal>> get() = _favouriteMealsLiveData

    private var _searchMealLiveData = MutableLiveData<List<Meal>>()
    val searchMealLiveData : LiveData<List<Meal>> get() = _searchMealLiveData

    init {
        getRandomMeal()
        getPopularItems()
        getCategories()
    }

    fun getRandomMeal(){
        RetrofitInstance.retrofit.getRandomMeal().enqueue(object : Callback<MealList> {
            override fun onResponse(call: Call<MealList>, response: Response<MealList>) {
                if (response.body() != null){
                    val randomMeal : Meal = response.body()!!.meals[0]
                    Log.d("Test", "meal id : ${randomMeal.strMeal}")
                    _randomMealLiveData.value = randomMeal

                } else{
                    return
                }
            }

            override fun onFailure(call: Call<MealList>, t: Throwable) {
                Log.d("HomeFragment", t.message.toString())
            }

        })
    }

    fun getPopularItems(){
        RetrofitInstance.retrofit.getPopularItems("Seafood").enqueue(object : Callback<MealsByCategoryList> {
            override fun onResponse(call: Call<MealsByCategoryList>, response: Response<MealsByCategoryList>) {
                if(response.body() != null){
                    _popularItemLiveData.value = response.body()!!.meals
                }
            }

            override fun onFailure(call: Call<MealsByCategoryList>, t: Throwable) {
                Log.d("HomeViewModel", t.message.toString())
            }

        })
    }

    fun getCategories(){
        RetrofitInstance.retrofit.getCategories().enqueue(object : Callback<CategoryList>{
            override fun onResponse(call: Call<CategoryList>, response: Response<CategoryList>) {
                // Kotlin way to write api call
                response.body()?.let { categoryList ->
                    _categoriesLiveData.postValue(categoryList.categories)
                }
            }

            override fun onFailure(call: Call<CategoryList>, t: Throwable) {
                Log.d("Categories", t.message.toString())
            }

        })
    }

    fun searchMeal(searchQuery : String){
        RetrofitInstance.retrofit.searchMeals(searchQuery).enqueue(object : Callback<MealList>{
            override fun onResponse(call: Call<MealList>, response: Response<MealList>) {
                response.body()?.let {searchList ->
                    _searchMealLiveData.postValue(searchList.meals)
                }
            }

            override fun onFailure(call: Call<MealList>, t: Throwable) {
                Log.d("SearchQuery", t.message.toString())
            }

        })
    }

    fun deleteMeal(meal: Meal){
        viewModelScope.launch {
            mealDatabase.mealDao().delete(meal)
        }
    }

    fun insertMeal(meal: Meal){
        viewModelScope.launch {
            mealDatabase.mealDao().upsert(meal)
        }
    }

}