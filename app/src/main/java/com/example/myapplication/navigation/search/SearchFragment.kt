package com.example.myapplication.navigation.search

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.TextView.OnEditorActionListener
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.arasthel.spannedgridlayoutmanager.SpanSize
import com.arasthel.spannedgridlayoutmanager.SpannedGridLayoutManager
import com.example.myapplication.R
import com.example.myapplication.data.datasource.remote.api.RecipeDTO
import com.example.myapplication.data.repository.Repository
import com.example.myapplication.result.ResultFragement
import java.util.*
import kotlin.collections.ArrayList


class SearchFragment : Fragment() {

    private lateinit var v: View
    private lateinit var vAutoCompleteTextView: View

    private lateinit var searchAdapter: SearchAdapter
    private lateinit var autoTextview: AutoCompleteTextView
    private lateinit var srl_update: SwipeRefreshLayout

//    private val tempRandomRecipes = ArrayList<RecipeDTO.RecipeFinal>()

    private var searchHistoryArrayList = ArrayList<String>()// 검색어 저장 List

    private val repository = Repository()

    private lateinit var tvRecommand: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
/*      tempRandomRecipes.add(RecipeDTO.tempRandomRecipes(1,"R.drawable.ic_home",null,null,null,null,null,null))
        tempRandomRecipes.add(RecipeDTO.tempRandomRecipes(2,"R.drawable.ic_home",null,null,null,null,null,null))
        tempRandomRecipes.add(RecipeDTO.tempRandomRecipes(3,"R.drawable.ic_home",null,null,null,null,null,null))
        tempRandomRecipes.add(RecipeDTO.tempRandomRecipes(4,"R.drawable.ic_home",null,null,null,null,null,null))
        tempRandomRecipes.add(RecipeDTO.tempRandomRecipes(5,"R.drawable.ic_home",null,null,null,null,null,null))
        tempRandomRecipes.add(RecipeDTO.tempRandomRecipes(6,"R.drawable.ic_home",null,null,null,null,null,null))
        tempRandomRecipes.add(RecipeDTO.tempRandomRecipes(7,"R.drawable.ic_home",null,null,null,null,null,null))
        tempRandomRecipes.add(RecipeDTO.tempRandomRecipes(8,"R.drawable.ic_home",null,null,null,null,null,null))
        tempRandomRecipes.add(RecipeDTO.tempRandomRecipes(9,"R.drawable.ic_home",null,null,null,null,null,null))
        tempRandomRecipes.add(RecipeDTO.tempRandomRecipes(10,"R.drawable.ic_home",null,null,null,null,null,null))
        tempRandomRecipes.add(RecipeDTO.tempRandomRecipes(11,"R.drawable.ic_home",null,null,null,null,null,null))
        tempRandomRecipes.add(RecipeDTO.tempRandomRecipes(12,"R.drawable.ic_home",null,null,null,null,null,null))
        tempRandomRecipes.add(RecipeDTO.tempRandomRecipes(13,"R.drawable.ic_home",null,null,null,null,null,null))*/
        v = inflater.inflate(R.layout.fragment_search, container, false)
        vAutoCompleteTextView = inflater.inflate(
            R.layout.custom_auto_complete_item_line,
            container,
            false
        )

        setRecyclerView()
        setAutoCompleteTextView()
        setButtonSearch()
        setSwipeRefreshLayout()
        pickRandomNumberOnRecommandTextView()

        return v

    }
    /** Fragment 생명주기 */
    override fun onResume() {
        autoTextview.setText("")
        super.onResume()
    }

    /**   AutoCompleteTextView 설정   */
    private fun setAutoCompleteTextView() {
        // 자동완성기능 Sample Data들
        searchAdapter.notifyDataSetChanged()
        searchHistoryArrayList = repository.getSavedSearchList()

        autoTextview = v.findViewById<AutoCompleteTextView>(R.id.actv_recipe)
        autoTextview.setText("", TextView.BufferType.EDITABLE) // 검색 프로그먼트 다시 돌아왔을 때, 텍스트 Null로 초기화

        val adapter = object : ArrayAdapter<String>(
            v.context,
            R.layout.custom_auto_complete_item_line,
            R.id.tv_auto_complete_item,
            searchHistoryArrayList
        ) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

                val view = super.getView(position, convertView, parent)

                val tvRecentHistory = view.findViewById<TextView>(R.id.tv_auto_complete_item)
                val btnDeleteHistory = view.findViewById<Button>(R.id.btn_delete_search_history)// 검색 기록 지우기 버튼 리스너
                btnDeleteHistory.setOnClickListener {
                    autoTextview.run {
                        repository.deleteSearchHistory(tvRecentHistory.text.toString())
                        searchHistoryArrayList = repository.getSavedSearchList()
                        notifyDataSetChanged()
                    }
                    this.notifyDataSetChanged()
                    autoTextview.setAdapter(this)
                }
                return view
            }
        }
        // 자동완성목록 Item 클릭 리스너
        autoTextview.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                val selectedItem = parent.getItemAtPosition(position).toString()
                hideKeyboard(autoTextview)
                Toast.makeText(v.context, "Selected : $selectedItem", Toast.LENGTH_SHORT).show()

                repository.saveSearchHistory(selectedItem)// 검색어 저장
                searchHistoryArrayList = repository.getSavedSearchList()
                adapter.notifyDataSetChanged()
            }

        autoTextview.setOnItemClickListener(OnItemClickListener { parent, view, position, id ->
            // click event (such as filling the autocomplete section)
        })

        autoTextview.setAdapter(adapter)

        // 키보드 입력 후 [Enter]클릭 리스너
        autoTextview.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                hideKeyboard(autoTextview)
                autoTextview.dismissDropDown()// 목록에 없는 Text 검색 시, Data목록 사라지는 기능

                repository.saveSearchHistory(v.text.toString()) // 검색어 저장
                searchHistoryArrayList = repository.getSavedSearchList()
                adapter.notifyDataSetChanged()
            }
            searchAdapter.notifyDataSetChanged()
            adapter.notifyDataSetChanged()//TODO : 방금 검색한 Text 바로 검색기록에 안뜸
            false
        })

        //검색기록 삭제 버튼
        vAutoCompleteTextView.findViewById<Button>(R.id.btn_delete_search_history).setOnClickListener {
            Toast.makeText(v.context, "fafasf", Toast.LENGTH_SHORT).show()
        }
    }


    private fun setButtonSearch() {
        val btnSearch = v.findViewById<Button>(R.id.btn_search)
        btnSearch.setOnClickListener {

            repository.saveSearchHistory(autoTextview.text.toString())//검색어 저장
            val bundle = Bundle()
            bundle.putString("input_search", autoTextview.text.toString())

            val activity = v.context as AppCompatActivity
            val transaction = activity.supportFragmentManager.beginTransaction()
            val resultFragment: Fragment = ResultFragement()
            resultFragment.arguments = bundle

            transaction.replace(R.id.fl_container, resultFragment)
            transaction.setCustomAnimations(
                R.anim.enter_from_left,
                R.anim.exit_to_right,
                R.anim.enter_from_left,
                R.anim.exit_to_right
            )
            transaction.addToBackStack(null)
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            transaction.commit()
        }
    }

    /**   RecyclerView 설정   */
    private fun setRecyclerView() {
        searchAdapter = SearchAdapter()
        requestRandomRecipes()

        val sgLayoutManager = SpannedGridLayoutManager(
            orientation = SpannedGridLayoutManager.Orientation.VERTICAL,
            spans = 3
        )
        sgLayoutManager.itemOrderIsStable = true

        val rvRandomRecipe = v.findViewById<RecyclerView>(R.id.rv_random_recipe)
        rvRandomRecipe.layoutManager = sgLayoutManager
        rvRandomRecipe.adapter = searchAdapter

        sgLayoutManager.spanSizeLookup = SpannedGridLayoutManager.SpanSizeLookup { position ->
            when (position % 12) {
                1, 6 -> {
                    SpanSize(2, 2)
                }
                else -> {
                    SpanSize(1, 1)
                }
            }
        }

    }

    /**  스와이프 동작 시, 리싸이클러뷰 아이템 재요청  */
    private fun setSwipeRefreshLayout() {
        srl_update = v.findViewById<SwipeRefreshLayout>(R.id.srl_update)
        srl_update.setColorSchemeResources(R.color.colorAccent)
        srl_update.setOnRefreshListener {
            Toast.makeText(v.context, "목록들 가져오는중", Toast.LENGTH_SHORT).show()
            requestRandomRecipes()           //repository에게 재요청
            srl_update.isRefreshing = false //swipe 에니메이션 삭제

            pickRandomNumberOnRecommandTextView()
        }
    }

    /**  Repository에게 타임라인 목록 요청  */
    private fun requestRandomRecipes() {
        searchAdapter.randomRecipes.clear()
        repository.getRandomRecipes(//TODO : getAllTimelinesList -> getRandomRecipes
            success = {
                it.run {
                    searchAdapter.randomRecipes = it.list!!

                    searchAdapter.updateRandomRecipeList(searchAdapter.randomRecipes)
                    searchAdapter.notifyDataSetChanged()
                }
            },
            fail = {
                Log.d("fail", "fail fail fail")
            }
        )
    }

    /** 키보드 숨기기 */
    private fun hideKeyboard(view: AutoCompleteTextView) {
        val imm = v.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    /** Random 뽑기 in (3, 6, 9) */
    private fun pickRandomNumberOnRecommandTextView() {
        val arr369 = arrayOf(3, 6, 9)
        val randomText = arr369.get(Random().nextInt(3))

        val ssb = SpannableStringBuilder("오늘은\n$randomText" + "컷요리 어때요?")
        ssb.setSpan(
            ForegroundColorSpan(Color.parseColor("#FF7051")),
            3,
            8,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        tvRecommand = v.findViewById<TextView>(R.id.tv_recommand)
        tvRecommand.text = ssb
    }

}