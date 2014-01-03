package fr.xebia.rxtuto;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import fr.xebia.rxtuto.api.ApiManager;
import fr.xebia.rxtuto.api.GitHubMember;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.concurrency.AndroidSchedulers;
import rx.concurrency.Schedulers;
import rx.util.functions.Func1;
import rx.util.functions.Func2;

public class MainActivity extends Activity implements Observer<String> {

    private static final String[] GITHUB_MEMBERS = new String[]{"mojombo", "JakeWharton", "mattt"};

    private TextView mMembersView;
    private Subscription mSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mMembersView = (TextView) findViewById(R.id.textview);
        mSubscription = Observable.from(GITHUB_MEMBERS)
                .mapMany(new Func1<String, Observable<GitHubMember>>() {
                    @Override
                    public Observable<GitHubMember> call(String s) {
                        return ApiManager.getGitHubMember(s);
                    }
                })
                .map(new Func1<GitHubMember, String>() {
                    @Override
                    public String call(GitHubMember gitHubMember) {
                        return gitHubMember.toString();
                    }
                })
                .aggregate(new Func2<String, String, String>() {
                    @Override
                    public String call(String s, String s2) {
                        return s + "\n" + s2;
                    }
                })
                .subscribeOn(Schedulers.threadPoolForIO())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this);
    }

    @Override
    protected void onDestroy() {
        mSubscription.unsubscribe();
        super.onDestroy();
    }

    @Override
    public void onCompleted() {
    }

    @Override
    public void onError(Throwable throwable) {
        Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNext(String member) {
        mMembersView.setText(member);
    }
}
