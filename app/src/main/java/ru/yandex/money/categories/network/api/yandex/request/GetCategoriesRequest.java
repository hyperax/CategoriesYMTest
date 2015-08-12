package ru.yandex.money.categories.network.api.yandex.request;

import android.content.Context;
import android.support.v4.util.ArrayMap;

import com.octo.android.robospice.request.SpiceRequest;

import java.util.List;

import de.greenrobot.event.EventBus;
import retrofit.RestAdapter;
import ru.yandex.money.categories.R;
import ru.yandex.money.categories.data.DataQuery;
import ru.yandex.money.categories.data.entity.Category;
import ru.yandex.money.categories.events.CategoriesObtainedEvent;
import ru.yandex.money.categories.helpers.Utils;
import ru.yandex.money.categories.network.api.yandex.YandexMoneyApi;
import ru.yandex.money.categories.network.api.yandex.model.CategoryNetworkModel;

public class GetCategoriesRequest extends SpiceRequest<Void> {

    private Context context;
    private String url;

    public GetCategoriesRequest(Context context, String url) {
        super(Void.class);
        this.url = url;
        this.context = context.getApplicationContext();
    }

    @Override
    public Void loadDataFromNetwork() throws Exception {

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(url)
                .build();

        YandexMoneyApi retrofitService = restAdapter.create(YandexMoneyApi.class);

        CategoriesObtainedEvent event = new CategoriesObtainedEvent();
        boolean isLoadSuccess = loadCategories(retrofitService.getCategories());

        if (isLoadSuccess) {
            event.setSuccess(true);
            event.setMessage(context.getString(R.string.update_completed_successfully));
        } else {
            event.setSuccess(false);
            event.setMessage(context.getString(R.string.error_processing));
        }

        EventBus.getDefault().postSticky(event);

        return null;
    }

    private boolean loadCategories(List<CategoryNetworkModel> categoriesNetwork) {

        DataQuery dataQuery = DataQuery.get(context);
        dataQuery.beginTransaction();
        dataQuery.clearCategories();

        ArrayMap<Long, List<CategoryNetworkModel>> loadBuffer = new ArrayMap<>();

        loadBuffer.put(0L, categoriesNetwork);

        try {
            while (loadBuffer.size() > 0) {
                long parentId = loadBuffer.keyAt(0);
                List<CategoryNetworkModel> toLoadCategories = loadBuffer.valueAt(0);

                for (CategoryNetworkModel cnm : toLoadCategories) {
                    long categoryId = dataQuery.putCategory(convertCategory(parentId, cnm));
                    if (!Utils.isEmpty(cnm.getSubs())) {
                        loadBuffer.put(categoryId, cnm.getSubs());
                    }
                }

                loadBuffer.removeAt(0);
            }

            dataQuery.setTransactionSuccessful();
        } catch (Exception e) {
            return false;
        } finally {
            dataQuery.endTransaction();
        }

        return true;
    }

    private Category convertCategory(long parentId, CategoryNetworkModel cnm) {
        return new Category()
                .setTitle(cnm.getTitle())
                .setOuterId(cnm.getId())
                .setParentId(parentId);
    }
}
