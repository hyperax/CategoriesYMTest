package ru.yandex.money.categories.view;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import de.greenrobot.event.EventBus;
import ru.yandex.money.categories.R;
import ru.yandex.money.categories.data.DataQuery;
import ru.yandex.money.categories.data.entity.Category;
import ru.yandex.money.categories.events.CategoriesObtainedEvent;
import ru.yandex.money.categories.network.NetworkService;
import ru.yandex.money.categories.network.api.yandex.YandexMoneyApi;
import ru.yandex.money.categories.network.api.yandex.request.GetCategoriesRequest;
import ru.yandex.money.categories.view.dialogs.ProgressDialogFragment;
import ru.yandex.money.categories.view.fragments.BaseFragment;
import ru.yandex.money.categories.view.fragments.CategoriesFragment;

public class MainActivity extends AppCompatActivity implements
        CategoriesFragment.Callback,
        FragmentManager.OnBackStackChangedListener {

    private static final String PROGRESS_DIALOG_TAG = "progress_dialog";

    private static final String PREFERENCE_IS_FIRST_RUN = "prefs_is_first_run";

    private SpiceManager spiceManager = new SpiceManager(NetworkService.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportFragmentManager().addOnBackStackChangedListener(this);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) replaceMainFragment(CategoriesFragment.newInstance(0L));
    }

    private void replaceMainFragment(Fragment fragment, String backStackName) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(backStackName)
                .commit();
    }

    private void replaceMainFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_update:
                startUpdate();
                break;
            case R.id.action_about:
                startAboutDialog();
                break;
            case android.R.id.home:
                onBackPressed();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void startUpdate() {
        startProgressDialog();
        GetCategoriesRequest request = new GetCategoriesRequest(this, YandexMoneyApi.SERVER_URI);
        spiceManager.execute(request, 0, DurationInMillis.ALWAYS_EXPIRED, new RequestListener<Void>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
                CategoriesObtainedEvent event = new CategoriesObtainedEvent();
                event.setSuccess(false);
                event.setMessage(spiceException.toString());
                EventBus.getDefault().postSticky(event);
            }

            @Override
            public void onRequestSuccess(Void aVoid) {
                // nothing to do. instance of request is already sent event
            }
        });
    }

    public void onEventMainThread(CategoriesObtainedEvent event) {
        if (!event.isHandled()) {
            stopProgressDialog();

            String message;
            if (event.isSuccess()) {
                message = getString(R.string.update_completed_successfully);
                resetFragments();
            } else {
                message = getString(R.string.update_error, event.getMessage());
            }
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();

            event.setHandled(true);
            EventBus.getDefault().removeStickyEvent(event);
        }
    }

    private void resetFragments() {
        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        replaceMainFragment(CategoriesFragment.newInstance(0L));
    }

    private void startAboutDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.about)
                .setMessage(R.string.about_content)
                .setNeutralButton(android.R.string.ok, null)
                .show();
    }

    @Override
    public void onClickCategory(Category category) {
        DataQuery dataQuery = DataQuery.get(this);
        if (dataQuery.hasChildCategories(category.getId())) {
            replaceMainFragment(CategoriesFragment.newInstance(category.getId()),
                    String.valueOf(category.getId()));
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        spiceManager.start(this);
        updateHomeButton();
        handleFirstRun();
    }

    private void handleFirstRun() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (prefs.getBoolean(PREFERENCE_IS_FIRST_RUN, true)) {
            prefs.edit().putBoolean(PREFERENCE_IS_FIRST_RUN, false).apply();
            startUpdate();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateTitle();
        EventBus.getDefault().registerSticky(this);
    }

    private void updateTitle() {
        BaseFragment f = getMainFragment();
        String title = null;

        if (f != null) {
            title = f.getInstanceTitle();
        }

        if (TextUtils.isEmpty(title)) {
            getSupportActionBar().setTitle(R.string.app_name);
        } else {
            getSupportActionBar().setTitle(title);
        }
    }

    private BaseFragment getMainFragment() {
        return (BaseFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        spiceManager.shouldStop();
    }

    private void startProgressDialog() {
        String title = getString(R.string.updating);
        String message = getString(R.string.updating_in_progress);
        DialogFragment dialog = ProgressDialogFragment.newInstance(title, message, false);
        dialog.show(getSupportFragmentManager(), PROGRESS_DIALOG_TAG);
    }

    private void stopProgressDialog() {
        DialogFragment dialog = (DialogFragment) getSupportFragmentManager()
                .findFragmentByTag(PROGRESS_DIALOG_TAG);
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    @Override
    public void onBackStackChanged() {
        updateHomeButton();
        updateTitle();
    }

    private void updateHomeButton() {
        boolean hasBackStack = getSupportFragmentManager().getBackStackEntryCount() > 0;
        getSupportActionBar().setDisplayHomeAsUpEnabled(hasBackStack);
    }

}
