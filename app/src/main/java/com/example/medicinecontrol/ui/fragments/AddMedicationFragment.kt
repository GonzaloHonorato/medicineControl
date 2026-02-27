package com.example.medicinecontrol.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import com.example.medicinecontrol.HomeActivity
import com.example.medicinecontrol.MedicationFormView
import com.example.medicinecontrol.R
import com.example.medicinecontrol.ui.theme.MedicineControlTheme

class AddMedicationFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_compose, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<ComposeView>(R.id.compose_view).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                MedicineControlTheme {
                    MedicationFormView(
                        onSaved = {
                            (requireActivity() as? HomeActivity)?.navigateToHome()
                        },
                        onBack = {
                            parentFragmentManager.popBackStack()
                        }
                    )
                }
            }
        }
    }
}
