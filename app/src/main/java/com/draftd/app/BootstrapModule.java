package com.draftd.app;

import android.accounts.AccountManager;
import android.content.Context;

import com.draftd.app.authenticator.ApiKeyProvider;
import com.draftd.app.authenticator.BootstrapAuthenticatorActivity;
import com.draftd.app.authenticator.LogoutService;
import com.draftd.app.core.BootstrapService;
import com.draftd.app.core.Constants;
import com.draftd.app.core.PostFromAnyThreadBus;
import com.draftd.app.core.RestAdapterRequestInterceptor;
import com.draftd.app.core.RestErrorHandler;
import com.draftd.app.core.TimerService;
import com.draftd.app.core.UserAgentProvider;
import com.draftd.app.ui.BootstrapTimerActivity;
import com.draftd.app.ui.CheckInsListFragment;
import com.draftd.app.ui.MainActivity;
import com.draftd.app.ui.NavigationDrawerFragment;
import com.draftd.app.ui.NewsActivity;
import com.draftd.app.ui.NewsListFragment;
import com.draftd.app.ui.UserActivity;
import com.draftd.app.ui.UserListFragment;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.otto.Bus;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;

/**
 * Dagger module for setting up provides statements.
 * Register all of your entry points below.
 */
@Module(
        complete = false,

        injects = {
                BootstrapApplication.class,
                BootstrapAuthenticatorActivity.class,
                MainActivity.class,
                BootstrapTimerActivity.class,
                CheckInsListFragment.class,
                NavigationDrawerFragment.class,
                NewsActivity.class,
                NewsListFragment.class,
                UserActivity.class,
                UserListFragment.class,
                TimerService.class
        }
)
public class BootstrapModule {

    @Singleton
    @Provides
    Bus provideOttoBus() {
        return new PostFromAnyThreadBus();
    }

    @Provides
    @Singleton
    LogoutService provideLogoutService(final Context context, final AccountManager accountManager) {
        return new LogoutService(context, accountManager);
    }

    @Provides
    BootstrapService provideBootstrapService(RestAdapter restAdapter) {
        return new BootstrapService(restAdapter);
    }

    @Provides
    BootstrapServiceProvider provideBootstrapServiceProvider(RestAdapter restAdapter, ApiKeyProvider apiKeyProvider) {
        return new BootstrapServiceProvider(restAdapter, apiKeyProvider);
    }

    @Provides
    ApiKeyProvider provideApiKeyProvider(AccountManager accountManager) {
        return new ApiKeyProvider(accountManager);
    }

    @Provides
    Gson provideGson() {
        /**
         * GSON instance to use for all request  with date format set up for proper parsing.
         * <p/>
         * You can also configure GSON with different naming policies for your API.
         * Maybe your API is Rails API and all json values are lower case with an underscore,
         * like this "first_name" instead of "firstName".
         * You can configure GSON as such below.
         * <p/>
         *
         * public static final Gson GSON = new GsonBuilder().setDateFormat("yyyy-MM-dd")
         *         .setFieldNamingPolicy(LOWER_CASE_WITH_UNDERSCORES).create();
         */
        return new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
    }

    @Provides
    RestErrorHandler provideRestErrorHandler(Bus bus) {
        return new RestErrorHandler(bus);
    }

    @Provides
    RestAdapterRequestInterceptor provideRestAdapterRequestInterceptor(UserAgentProvider userAgentProvider) {
        return new RestAdapterRequestInterceptor(userAgentProvider);
    }

    @Provides
    RestAdapter provideRestAdapter(RestErrorHandler restErrorHandler, RestAdapterRequestInterceptor restRequestInterceptor, Gson gson) {
        return new RestAdapter.Builder()
                .setEndpoint(Constants.Http.URL_BASE)
                .setErrorHandler(restErrorHandler)
                .setRequestInterceptor(restRequestInterceptor)
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setConverter(new GsonConverter(gson))
                .build();
    }

}
