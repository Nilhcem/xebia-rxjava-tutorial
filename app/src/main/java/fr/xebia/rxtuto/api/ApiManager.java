package fr.xebia.rxtuto.api;

import retrofit.RestAdapter;
import retrofit.http.GET;
import retrofit.http.Path;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.concurrency.Schedulers;
import rx.subscriptions.Subscriptions;

public class ApiManager {

    private interface ApiManagerService {
        @GET("/users/{username}")
        GitHubMember getMember(@Path("username") String username);
    }

    private static final RestAdapter restAdapter = new RestAdapter.Builder()
            .setServer("https://api.github.com")
            .build();

    private static final ApiManagerService apiManager = restAdapter.create(ApiManagerService.class);

    public static Observable<GitHubMember> getGitHubMember(final String username) {
        return Observable.create(new Observable.OnSubscribeFunc<GitHubMember>() {
            @Override
            public Subscription onSubscribe(Observer<? super GitHubMember> observer) {
                try {
                    observer.onNext(apiManager.getMember(username));
                    observer.onCompleted();
                } catch (Exception e) {
                    observer.onError(e);
                }
                return Subscriptions.empty();
            }
        }).subscribeOn(Schedulers.threadPoolForIO());
    }
}
