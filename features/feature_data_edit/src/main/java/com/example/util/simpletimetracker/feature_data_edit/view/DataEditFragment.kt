package com.example.util.simpletimetracker.feature_data_edit.view

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import com.example.util.simpletimetracker.core.base.BaseFragment
import com.example.util.simpletimetracker.core.extension.hideKeyboard
import com.example.util.simpletimetracker.core.extension.showKeyboard
import com.example.util.simpletimetracker.feature_base_adapter.BaseRecyclerAdapter
import com.example.util.simpletimetracker.feature_base_adapter.category.createCategoryAdapterDelegate
import com.example.util.simpletimetracker.feature_data_edit.dialog.DataEditTagSelectionDialogListener
import com.example.util.simpletimetracker.feature_data_edit.dialog.DataEditTypeSelectionDialogListener
import com.example.util.simpletimetracker.feature_data_edit.model.DataEditAddTagsState
import com.example.util.simpletimetracker.feature_data_edit.model.DataEditChangeActivityState
import com.example.util.simpletimetracker.feature_data_edit.model.DataEditChangeButtonState
import com.example.util.simpletimetracker.feature_data_edit.model.DataEditChangeCommentState
import com.example.util.simpletimetracker.feature_data_edit.viewModel.DataEditViewModel
import com.example.util.simpletimetracker.feature_views.extension.setOnClick
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import dagger.hilt.android.AndroidEntryPoint
import com.example.util.simpletimetracker.feature_data_edit.databinding.DataEditFragmentBinding as Binding

@AndroidEntryPoint
class DataEditFragment :
    BaseFragment<Binding>(),
    DataEditTypeSelectionDialogListener,
    DataEditTagSelectionDialogListener {

    override val inflater: (LayoutInflater, ViewGroup?, Boolean) -> Binding =
        Binding::inflate

    private val viewModel: DataEditViewModel by viewModels()

    private val addTagsPreviewAdapter: BaseRecyclerAdapter by lazy {
        BaseRecyclerAdapter(
            createCategoryAdapterDelegate(),
        )
    }

    override fun initUi(): Unit = with(binding) {
        rvDataEditAddTagsPreview.apply {
            layoutManager = FlexboxLayoutManager(requireContext()).apply {
                flexDirection = FlexDirection.ROW
                justifyContent = JustifyContent.FLEX_START
                flexWrap = FlexWrap.WRAP
            }
            adapter = addTagsPreviewAdapter
        }
    }

    override fun initUx() = with(binding) {
        btnDataEditSelectRecords.setOnClick(throttle(viewModel::onSelectRecordsClick))
        checkboxDataEditChangeActivity.setOnClick(viewModel::onChangeActivityClick)
        checkboxDataEditChangeComment.setOnClick(viewModel::onChangeCommentClick)
        checkboxDataEditAddTag.setOnClick(viewModel::onAddTagsClick)
        etDataEditChangeComment.doAfterTextChanged { viewModel.onCommentChange(it.toString()) }
        btnDataEditChange.setOnClick(throttle(viewModel::onChangeClick))
    }

    override fun initViewModel(): Unit = with(viewModel) {
        with(binding) {
            selectedRecordsCountViewData.observe(tvDataEditSelectedRecords::setText)
            changeActivityState.observe(::setChangeActivityState)
            changeCommentState.observe(::setChangeCommentState)
            addTagsState.observe(::setAddTagState)
            changeButtonState.observe(::setChangeButtonState)
            keyboardVisibility.observe { visible ->
                if (visible) showKeyboard(etDataEditChangeComment)
                else hideKeyboard()
            }
        }
    }

    override fun onResume() = with(binding) {
        super.onResume()
        checkboxDataEditChangeActivity.jumpDrawablesToCurrentState()
        checkboxDataEditChangeComment.jumpDrawablesToCurrentState()
        checkboxDataEditAddTag.jumpDrawablesToCurrentState()
        checkboxDataEditRemoveTag.jumpDrawablesToCurrentState()
    }

    override fun onTypeSelected(typeId: Long) {
        viewModel.onTypeSelected(typeId)
    }

    override fun onTypeDismissed() {
        viewModel.onTypeDismissed()
    }

    override fun onTagsSelected(tagIds: List<Long>) {
        viewModel.onTagsSelected(tagIds)
    }

    override fun onTagsDismissed() {
        viewModel.onTagsDismissed()
    }

    private fun setChangeActivityState(
        state: DataEditChangeActivityState,
    ) = with(binding) {
        when (state) {
            is DataEditChangeActivityState.Disabled -> {
                checkboxDataEditChangeActivity.isChecked = false
                viewDataEditChangeActivityPreview.isVisible = false
            }
            is DataEditChangeActivityState.Enabled -> {
                checkboxDataEditChangeActivity.isChecked = true
                viewDataEditChangeActivityPreview.isVisible = true
                viewDataEditChangeActivityPreview.apply {
                    itemColor = state.viewData.color
                    itemIcon = state.viewData.iconId
                    itemIconColor = state.viewData.iconColor
                    itemIconAlpha = state.viewData.iconAlpha
                    itemName = state.viewData.name
                }
            }
        }
    }

    private fun setChangeCommentState(
        state: DataEditChangeCommentState,
    ) = with(binding) {
        when (state) {
            is DataEditChangeCommentState.Disabled -> {
                checkboxDataEditChangeComment.isChecked = false
                inputDataEditChangeComment.isVisible = false
            }
            is DataEditChangeCommentState.Enabled -> {
                checkboxDataEditChangeComment.isChecked = true
                inputDataEditChangeComment.isVisible = true
                if (etDataEditChangeComment.text.toString() != state.viewData) {
                    etDataEditChangeComment.setText(state.viewData)
                }
            }
        }
    }

    private fun setAddTagState(
        state: DataEditAddTagsState,
    ) = with(binding) {
        when (state) {
            is DataEditAddTagsState.Disabled -> {
                checkboxDataEditAddTag.isChecked = false
                rvDataEditAddTagsPreview.isVisible = false
            }
            is DataEditAddTagsState.Enabled -> {
                checkboxDataEditAddTag.isChecked = true
                rvDataEditAddTagsPreview.isVisible = true
                addTagsPreviewAdapter.replace(state.viewData)
            }
        }
    }

    private fun setChangeButtonState(
        state: DataEditChangeButtonState
    ) = with(binding) {
        btnDataEditChange.isEnabled = state.enabled
        btnDataEditChange.backgroundTintList = ColorStateList.valueOf(state.backgroundTint)
    }
}
