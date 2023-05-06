package com.basindevapp.speed_0.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.basindevapp.speed_0.MainViewModel
import com.basindevapp.speed_0.R
import com.basindevapp.speed_0.adapter.ThresholdAdapter
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ThresholdBottomSheetFragment : BottomSheetDialogFragment() {

    private val viewModel: MainViewModel by activityViewModels()
    lateinit var thresholdAdapter: ThresholdAdapter
    lateinit var thresholdRV: RecyclerView
    lateinit var v: View
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        v = inflater.inflate(R.layout.bottom_sheet_layout, container, false)
        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViews()
        setObservations()
    }

    private fun setUpViews() {
        thresholdRV = v.findViewById(R.id.thresholdRV)
        thresholdAdapter = ThresholdAdapter {
            viewModel.setThresshold(it)
            dismissAllowingStateLoss()
        }
        thresholdRV.layoutManager = LinearLayoutManager(context)
        thresholdRV.adapter = thresholdAdapter
    }

    private fun setObservations() {
        viewModel.thresholdLimit.observe(viewLifecycleOwner) { thresholdLimit ->
            thresholdAdapter.updateItems(thresholdLimit)
        }
        viewModel.threshold.observe(this) {

        }
    }

    companion object {
        @JvmStatic
        fun newInstance(bundle: Bundle): ThresholdBottomSheetFragment {
            val fragment = ThresholdBottomSheetFragment()
            fragment.arguments = bundle
            return fragment
        }
    }
}