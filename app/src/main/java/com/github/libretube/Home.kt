package com.github.libretube

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

import okhttp3.*
import retrofit2.HttpException
import com.github.libretube.adapters.TrendingAdapter
import java.io.IOException


class Home : Fragment() {

    private val TAG = "HomeFragment"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recyclerView =  view.findViewById<RecyclerView>(R.id.recview)
        recyclerView.layoutManager = GridLayoutManager(view.context, resources.getInteger(R.integer.grid_items))
        val progressbar = view.findViewById<ProgressBar>(R.id.progressBar)
        fetchJson(progressbar,recyclerView)


    }


   private fun fetchJson(progressBar: ProgressBar, recyclerView: RecyclerView) {
        fun run() {
            lifecycleScope.launchWhenCreated {
                val response = try {
                    RetrofitInstance.api.getTrending("US")
                }catch(e: IOException) {
                    println(e)
                    Log.e(TAG, "IOException, you might not have internet connection")
                    return@launchWhenCreated
                } catch (e: HttpException) {
                    Log.e(TAG, "HttpException, unexpected response")
                    return@launchWhenCreated
                }
                runOnUiThread {
                    progressBar.visibility = View.GONE
                    recyclerView.adapter = TrendingAdapter(response)
                }
            }
        }
       run()

    }
    private fun Fragment?.runOnUiThread(action: () -> Unit) {
        this ?: return
        if (!isAdded) return // Fragment not attached to an Activity
        activity?.runOnUiThread(action)
    }

    override fun onDestroyView() {
        view?.findViewById<RecyclerView>(R.id.recview)?.adapter=null
        super.onDestroyView()
    }
}
