package com.appscrip.olamapdemo.fragments

import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.appscrip.olamapdemo.databinding.ItemAddressBottomSheetBinding
import com.appscrip.olamapdemo.model.response.autocompletesearch.Prediction
import com.appscrip.olamapdemo.util.DataConstants.ADDRESS_DATA

class OlaAddressBottomSheet : BottomSheetDialogFragment() {

    private lateinit var binding: ItemAddressBottomSheetBinding
    private var addressData: Prediction? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ItemAddressBottomSheetBinding.inflate(inflater, container, false)
        getAddressData()
        Toast.makeText(requireContext(), "$addressData", Toast.LENGTH_SHORT).show()
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // used to show the bottom sheet dialog
        dialog?.setOnShowListener { it ->
            val d = it as BottomSheetDialog
            val bottomSheet =
                d.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.let {
                val behavior = BottomSheetBehavior.from(it)
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }
        return super.onCreateDialog(savedInstanceState)
    }

    private fun getAddressData() {
        arguments?.let {
            addressData = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                it.getParcelable(ADDRESS_DATA, Prediction::class.java)
            } else {
                it.getParcelable(ADDRESS_DATA)
            }
        }
    }

    companion object {
        const val TAG = "ModalBottomSheetDialog"
    }
}