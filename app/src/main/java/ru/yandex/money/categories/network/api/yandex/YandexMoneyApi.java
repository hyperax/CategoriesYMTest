package ru.yandex.money.categories.network.api.yandex;

import java.util.List;

import retrofit.http.POST;
import ru.yandex.money.categories.network.api.yandex.model.CategoryNetworkModel;

public interface YandexMoneyApi {

    String SERVER_URI = "https://money.yandex.ru/api/";

    @POST("/categories-list")
    List<CategoryNetworkModel> getCategories();
}
