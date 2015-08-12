package ru.yandex.money.categories.view.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import java.util.List;

import ru.yandex.money.categories.R;
import ru.yandex.money.categories.data.DataQuery;
import ru.yandex.money.categories.data.entity.Category;
import ru.yandex.money.categories.view.adapters.CategoriesAdapter;

public class CategoriesFragment extends BaseFragment implements AdapterView.OnItemClickListener {

    public static final String ARG_PARENT_ID = "parent_id";

    private long categoryParentId;

    private GridView categoriesGridView;

    private Callback listener;

    public interface Callback {
        void onClickCategory(Category category);
    }

    public static CategoriesFragment newInstance(long parentId) {
        Bundle args = new Bundle();
        args.putLong(ARG_PARENT_ID, parentId);

        CategoriesFragment fragment = new CategoriesFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        categoryParentId = getArguments().getLong(ARG_PARENT_ID);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_categories_list, container, false);
        initCategoriesGridView(v);
        return v;
    }

    private void initCategoriesGridView(View contentView) {
        categoriesGridView = (GridView) contentView.findViewById(R.id.categories_gridview);
        categoriesGridView.setEmptyView(contentView.findViewById(R.id.empty_view));
        categoriesGridView.setOnItemClickListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        updateCategories();
    }

    private void updateCategories() {
        List<Category> categories = DataQuery.get(getActivity()).getCategories(categoryParentId);
        CategoriesAdapter adapter = (CategoriesAdapter) categoriesGridView.getAdapter();
        if (adapter == null) {
            categoriesGridView.setAdapter(new CategoriesAdapter(getActivity(), categories));
        } else {
            adapter.setItems(categories);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Category clickedCategory = ((CategoriesAdapter) parent.getAdapter()).getItem(position);
        listener.onClickCategory(clickedCategory);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (getHostParent() instanceof Callback) {
            listener = (Callback) getHostParent();
        } else {
            throw new ClassCastException("Host activity or parent fragment must implements "
                    + Callback.class.getCanonicalName() + " interface");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @Override
    public String getInstanceTitle() {
        Category parentCategory = DataQuery.get(getActivity()).getCategory(categoryParentId);
        return parentCategory != null ? parentCategory.getTitle() : null;
    }
}
