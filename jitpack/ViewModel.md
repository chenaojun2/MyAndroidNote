# ViewModel

## 什么是ViewModel

具备宿主生命后期感知能力的数据存储组件

ViewModel保存的数据，在页面因配置变更导致页面销毁重建后也依然存在。

配置变更：横竖屏切换，分辨率调整，权限变更，系统字体样式

## 基本使用

```
class MainRepository {
    suspend fun getNameList(): List<String> {
        return withContext(Dispatchers.IO) {
            listOf("张三", "李四")
        }
    }
}


class MainViewModel: ViewModel() {
    private val nameList = MutableLiveData<List<String>>()
    val nameListResult: LiveData<List<String>> = nameList
    private val mainRepository = MainRepository()
    
    fun getNames() {
        nameList.value = mainRepository.getNameList()
    }
}


class MainActivity : AppCompatActivity() {
    // 创建 ViewModel 方式 1
    // 通过 kotlin 委托特性创建 ViewModel
    // 需添加依赖 implementation 'androidx.activity:activity-ktx:1.2.3'
    // viewModels() 内部也是通过 创建 ViewModel 方式 2 来创建的 ViewModel
    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate  (savedInstanceState)
        setContentView(R.layout.activity_main)
        val mainViewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        mainViewModel.nameListResult.observe(this, {
            Log.i("MainActivity", "mainViewModel: nameListResult: $it")
            Log.i("MainActivity", "MainActivity: ${this@MainActivity} mainViewModel: $mainViewModel  mainViewModel.nameListResult: ${mainViewModel.nameListResult}")
        })
        mainViewModel.getNames()
    }
}

```

## 复用实现原理

如何做到的宿主销毁时，我们的数据还能继续存在呢。我们猜测前后获取的是同一个ViewModel实例对象。

我们先看是如何获取的ViewModel

```
//一般情况下会传入我们的默认factory
//为什么要求传入ViewModelStoreOwner，而我们传入activity也会有有用呢。自然是因为ComponentActivity有实现接口

public ViewModelProvider(@NonNull ViewModelStoreOwner owner) {
        this(owner.getViewModelStore(), owner instanceof HasDefaultViewModelProviderFactory
                ? ((HasDefaultViewModelProviderFactory) owner).getDefaultViewModelProviderFactory()
                : NewInstanceFactory.getInstance());
    }

public ViewModelProvider(@NonNull ViewModelStoreOwner owner, @NonNull Factory factory) {
        this(owner.getViewModelStore(), factory);
    }

public ViewModelProvider(@NonNull ViewModelStore store, @NonNull Factory factory) {
        mFactory = factory;
        mViewModelStore = store;
    }

public <T extends ViewModel> T get(@NonNull String key, @NonNull Class<T> modelClass) {

        //mViewModelStore是从我们的activity传进来的
        ViewModel viewModel = mViewModelStore.get(key);
        //判断实例是不是传入的class类型
        if (modelClass.isInstance(viewModel)) {
            if (mFactory instanceof OnRequeryFactory) {
                ((OnRequeryFactory) mFactory).onRequery(viewModel);
            }
            return (T) viewModel;
        } else {
            //noinspection StatementWithEmptyBody
            if (viewModel != null) {
                // TODO: log a warning.
            }
        }
        //不是传入的实例就需要工厂创建
        if (mFactory instanceof KeyedFactory) {
            viewModel = ((KeyedFactory) (mFactory)).create(key, modelClass);
        } else {
            viewModel = (mFactory).create(modelClass);
        }
        创建之后放入缓存
        mViewModelStore.put(key, viewModel);
        return (T) viewModel;
    }

```

我们可以看到需要想要复用viewModel必然  **mViewModelStore必须复用** 我们来看我们的ViewModelStore在Activity的哪里

```
public ViewModelStore getViewModelStore() {
        if (getApplication() == null) {
            throw new IllegalStateException("Your activity is not yet attached to the "
                    + "Application instance. You can't request ViewModel before onCreate call.");
        }
        //下面三个判断可以看出我们的mViewModelStore来自NonConfigurationInstances
        if (mViewModelStore == null) {
            NonConfigurationInstances nc =
                    (NonConfigurationInstances) getLastNonConfigurationInstance();
            if (nc != null) {
                // Restore the ViewModelStore from NonConfigurationInstances
                mViewModelStore = nc.viewModelStore;
            }
            if (mViewModelStore == null) {
                mViewModelStore = new ViewModelStore();
            }
        }
        return mViewModelStore;
    }

    //这个方法是直接获取，我们看不出什么。
    public Object getLastNonConfigurationInstance() {
        return mLastNonConfigurationInstances != null
                ? mLastNonConfigurationInstances.activity : null;
    }
```

我们从上面看到我们的mViewModelStore来自NonConfigurationInstances。那它是什么时候存进去的什么时候恢复的呢。我们继续查询资料和源码发现了如下方法

```
    //本方法用于保留所有适当的非配置状态
    public final Object onRetainNonConfigurationInstance() {
        Object custom = onRetainCustomNonConfigurationInstance();

        ViewModelStore viewModelStore = mViewModelStore;
        if (viewModelStore == null) {
            // No one called getViewModelStore(), so see if there was an existing
            // ViewModelStore from our last NonConfigurationInstance
            NonConfigurationInstances nc =
                    (NonConfigurationInstances) getLastNonConfigurationInstance();
            if (nc != null) {
                viewModelStore = nc.viewModelStore;
            }
        }

        if (viewModelStore == null && custom == null) {
            return null;
        }
        //我们的ViewModelStore就在这里保存
        NonConfigurationInstances nci = new NonConfigurationInstances();
        nci.custom = custom;
        nci.viewModelStore = viewModelStore;
        return nci;
    }

    //上面的方法在Activity中的retainNonConfigurationInstances调用，而这个方法的调用入口在ActivityThread中的performDestroyActivity。这个会被调用是因为handleRelaunchActivity会在销毁重建Activity时调用
```

我们继续看看performDestroyActivity,删掉一些无关代码

```
ActivityClientRecord performDestroyActivity(IBinder token, boolean finishing,
            int configChanges, boolean getNonConfigInstance, String reason) {
        ActivityClientRecord r = mActivities.get(token);
        Class<? extends Activity> activityClass = null;
        if (localLOGV) Slog.v(TAG, "Performing finish of " + r);
        if (r != null) {
            if (getNonConfigInstance) {
                try {
                    //这里可以看见我们的数据存在了ActivityClientRecord中。
                    r.lastNonConfigurationInstances
                            = r.activity.retainNonConfigurationInstances();
                } catch (Exception e) {
                    if (!mInstrumentation.onException(r.activity, e)) {
                        throw new RuntimeException(
                                "Unable to retain activity "
                                + r.intent.getComponent().toShortString()
                                + ": " + e.toString(), e);
                    }
                }
            }
           
    }
```
我们找到的存储的地方，那么重建的时候也自然是从这里取值的。

恢复的方法如下

```
private Activity performLaunchActivity(ActivityClientRecord r, Intent customIntent) {
        ActivityInfo aInfo = r.activityInfo;
       
        try {
            Application app = r.packageInfo.makeApplication(false, mInstrumentation);

            if (localLOGV) Slog.v(TAG, "Performing launch of " + r);
            if (localLOGV) Slog.v(
                    TAG, r + ": app=" + app
                    + ", appName=" + app.getPackageName()
                    + ", pkg=" + r.packageInfo.getPackageName()
                    + ", comp=" + r.intent.getComponent().toShortString()
                    + ", dir=" + r.packageInfo.getAppDir());

            if (activity != null) {
                CharSequence title = r.activityInfo.loadLabel(appContext.getPackageManager());
                Configuration config = new Configuration(mCompatConfiguration);
                if (r.overrideConfig != null) {
                    config.updateFrom(r.overrideConfig);
                }
                if (DEBUG_CONFIGURATION) Slog.v(TAG, "Launching activity "
                        + r.activityInfo.name + " with config " + config);
                Window window = null;
                if (r.mPendingRemoveWindow != null && r.mPreserveWindow) {
                    window = r.mPendingRemoveWindow;
                    r.mPendingRemoveWindow = null;
                    r.mPendingRemoveWindowManager = null;
                }
                appContext.setOuterContext(activity);
                //此处传入我们的需要恢复的数据
                activity.attach(appContext, this, getInstrumentation(), r.token,
                        r.ident, app, r.intent, r.activityInfo, title, r.parent,
                        r.embeddedID, r.lastNonConfigurationInstances, config,
                        r.referrer, r.voiceInteractor, window, r.configCallback,
                        r.assistToken);

                if (customIntent != null) {
                    activity.mIntent = customIntent;
                }
                r.lastNonConfigurationInstances = null;
                checkAndBlockForNetworkAccess();
                activity.mStartedActivity = false;
                int theme = r.activityInfo.getThemeResource();
                if (theme != 0) {
                    activity.setTheme(theme);
                }

                activity.mCalled = false;
                if (r.isPersistable()) {
                    mInstrumentation.callActivityOnCreate(activity, r.state, r.persistentState);
                } else {
                    mInstrumentation.callActivityOnCreate(activity, r.state);
                }
                if (!activity.mCalled) {
                    throw new SuperNotCalledException(
                        "Activity " + r.intent.getComponent().toShortString() +
                        " did not call through to super.onCreate()");
                }
                r.activity = activity;
            }
            r.setState(ON_CREATE);

            // updatePendingActivityConfiguration() reads from mActivities to update
            // ActivityClientRecord which runs in a different thread. Protect modifications to
            // mActivities to avoid race.
            synchronized (mResourcesManager) {
                mActivities.put(r.token, r);
            }

        } catch (SuperNotCalledException e) {
            throw e;

        } catch (Exception e) {
            if (!mInstrumentation.onException(activity, e)) {
                throw new RuntimeException(
                    "Unable to start activity " + component
                    + ": " + e.toString(), e);
            }
        }

        return activity;
    }
    
```