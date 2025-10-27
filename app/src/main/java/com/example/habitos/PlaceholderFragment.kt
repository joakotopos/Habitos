package com.example.habitos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment

class PlaceholderFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = TextView(requireContext())
        view.text = "Tareas ${arguments?.getString(ARG_SECTION_NAME)}"
        view.textSize = 24f
        view.textAlignment = View.TEXT_ALIGNMENT_CENTER
        return view
    }

    companion object {
        private const val ARG_SECTION_NAME = "SECTION_NAME"

        fun newInstance(sectionName: String): PlaceholderFragment {
            val fragment = PlaceholderFragment()
            val args = Bundle()
            args.putString(ARG_SECTION_NAME, sectionName)
            fragment.arguments = args
            return fragment
        }
    }
}
