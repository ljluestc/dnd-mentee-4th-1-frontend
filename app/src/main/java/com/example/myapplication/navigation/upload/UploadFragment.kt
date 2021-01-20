package com.example.myapplication.navigation.upload

/**
 *  하단 탭에서 Write 선택시 보여지는 Fragment
 *
 *  레시피를 입력받아서 서버로 전송해준다.
 */

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.data.datasource.remote.api.RecipeDTO
import com.example.myapplication.data.repository.Repository
import com.example.myapplication.data.repository.RepositoryImpl

class UploadFragment : Fragment() {

    private val REQUEST_GET_IMAGE = 105

    private var count = 1
    private var positionMain = 0

    private var postInfoList = ArrayList<RecipeDTO.Timeline>()
    private var list = ArrayList<RecipeDTO.Recipe>()

    private lateinit var v: View
    private lateinit var adapter: UploadRecipeAdapter
    private lateinit var itemMain: RecipeDTO.Recipe

    private val repository = RepositoryImpl()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.fragment_write, container, false)
        var btn_recipe_add = v.findViewById(R.id.btn_recipe_add) as Button
        var btn_recipe_del = v.findViewById(R.id.btn_recipe_del) as Button
        var btn_submit = v.findViewById(R.id.btn_submit) as Button

        list.clear()
        count = 0

        callRecycler()

        /**
         *  '요리 순서 추가' 버튼 이벤트
         */
        btn_recipe_add.setOnClickListener {
            addItem(
                adapter.itemCount,
                RecipeDTO.Recipe(Integer.toString(adapter.itemCount + 1) + "번", "", "")
            )
            count++
        }

        /**
         *  '요리 순서 삭제' 버튼 이벤트
         */
        btn_recipe_del.setOnClickListener {
            if (adapter.itemCount > 0) {
                removeItem(adapter.itemCount - 1)
                count--
            }
        }

        /**
         *  '전송' 버튼 이벤트
         */
/*        TODO("성호님 이 부분에 제가 만든 postInfoList  안에다가 " +
                "timelineId" +
                "title" +
                "subTitle" +
                "imageUrl배열" +
                "comment배열" +

                "넣어주세요."
        )*/


        btn_submit.setOnClickListener {
            repository.postTimeline(
                postInfoList,
                success = {

                }
            ) {

            }
        }

        return v
    }

    /**
     *  갤러리에서 사진을 눌렀을 때 요청 처리하는 함수.
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_GET_IMAGE ->
                    try {
                        var uri = data?.data
                        list[positionMain].image = uri.toString()
                        adapter.notifyDataSetChanged()
                        showItem()
                    } catch (e: Exception) {
                    }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    /**
     *  리사이클러 뷰 생성 및 갤러리 버튼 클릭 시 갤러리 호출하는 함수
     */
    fun callRecycler() {
        var rv_recipe_list = v.findViewById(R.id.rv_recipe_list) as RecyclerView

        adapter = UploadRecipeAdapter(v.context, list) { position, item ->
            positionMain = position
            itemMain = item

            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, REQUEST_GET_IMAGE)
        }

        rv_recipe_list.adapter = adapter
        rv_recipe_list.layoutManager = LinearLayoutManager(v.context)

    }

    fun addItem(position: Int, data: RecipeDTO.Recipe) {
        list.add(position, data)
        adapter.notifyItemInserted(position)
    }

    fun removeItem(position: Int) {
        list.removeAt(position)
        adapter.notifyItemRemoved(position)
    }

    fun showItem() {
        for (i in 0..10) {
            Log.d(
                "log",
                "${i} 번째 ,number : " + list.get(i).number + "comment :" + list.get(i).comment.toString() + "image : " + list.get(
                    i
                ).image.toString()
            )
        }
    }
}