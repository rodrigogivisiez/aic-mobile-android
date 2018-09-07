package artic.edu.search


import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.view.View
import com.fuzz.rx.bindToMain
import com.fuzz.rx.disposedBy
import edu.artic.adapter.itemChanges
import edu.artic.adapter.itemClicksWithPosition
import edu.artic.analytics.ScreenCategoryName
import edu.artic.base.utils.asDeepLinkIntent
import edu.artic.navigation.NavigationConstants
import edu.artic.navigation.NavigationConstants.Companion.ARG_SEARCH_OBJECT
import edu.artic.viewmodel.BaseViewModelFragment
import edu.artic.viewmodel.Navigate
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.fragment_default_search_suggestions.*
import kotlin.reflect.KClass


class DefaultSearchSuggestionsFragment : BaseViewModelFragment<DefaultSearchSuggestionsViewModel>() {

    companion object {
        const val MAX_ARTWORKS_PER_ROW: Int = 5
    }

    override val viewModelClass: KClass<DefaultSearchSuggestionsViewModel> = DefaultSearchSuggestionsViewModel::class

    override val title = "Search"

    override val layoutResId = R.layout.fragment_default_search_suggestions

    override val screenCategory: ScreenCategoryName? = ScreenCategoryName.Search

    override val overrideStatusBarColor: Boolean = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        items.apply {
            layoutManager = GridLayoutManager(view.context, MAX_ARTWORKS_PER_ROW, GridLayoutManager.VERTICAL, false)
            (layoutManager as GridLayoutManager).spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return (adapter as DefaultSuggestionAdapter).getSpanCount(position)
                }
            }
            adapter = DefaultSuggestionAdapter()
        }
    }

    override fun setupBindings(viewModel: DefaultSearchSuggestionsViewModel) {
        super.setupBindings(viewModel)
        val adapter = items.adapter as DefaultSuggestionAdapter

        adapter.itemClicksWithPosition()
                .subscribeBy { (pos, searchViewModel) ->
                    viewModel.onClickItem(pos, searchViewModel)
                }.disposedBy(disposeBag)

        viewModel.cells
                .bindToMain(adapter.itemChanges())
                .disposedBy(disposeBag)

    }

    override fun setupNavigationBindings(viewModel: DefaultSearchSuggestionsViewModel) {
        super.setupNavigationBindings(viewModel)
        viewModel.navigateTo.subscribeBy { navigation ->
            when (navigation) {
                is Navigate.Forward -> {
                    when (navigation.endpoint) {
                        is DefaultSearchSuggestionsViewModel.NavigationEndpoint.ArticObjectDetails -> {
                            val o = (navigation.endpoint as DefaultSearchSuggestionsViewModel.NavigationEndpoint.ArticObjectDetails).articObject
                            val mapIntent = NavigationConstants.MAP.asDeepLinkIntent().apply {
                                putExtra(ARG_SEARCH_OBJECT, o)
                                flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT or Intent.FLAG_ACTIVITY_NO_ANIMATION
                            }
                            startActivity(mapIntent)
                        }
                    }
                }
                is Navigate.Back -> {

                }
            }
        }.disposedBy(disposeBag)
    }
}
