package dangxia.com.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.flyco.tablayout.SlidingTabLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import dangxia.com.R;
import dangxia.com.adapter.TaskItemAdapter;
import dangxia.com.adapter.listener.TaskItemCallBackListener;
import dangxia.com.entity.TaskDto;
import dangxia.com.utils.http.HttpCallbackListener;
import dangxia.com.utils.http.HttpUtil;
import dangxia.com.utils.http.UrlHandler;
import dangxia.com.utils.location.LocationUtil;

/**
 * Created by zhuang_ge on 2017/11/17.
 */

public class CommunityFragment extends Fragment {

    private SlidingTabLayout tabLayout;
    private ViewPager viewPager;
    private View allTaskPage;
    private View myTaskPage;
    private SearchView searchView;
    private String[] titles = {"已发布", "待解决"};
    private List<String> titleList = Arrays.asList(titles);
    private List<View> viewList = new ArrayList<>();

    //所有任务页面
    private RecyclerView allTaskRV;
    private TaskItemAdapter allTaskAdapter;
    private TaskItemCallBackListener listener;
    private List<TaskDto> allTask;
    private SwipeRefreshLayout swipeAllTask;

    //我的任务页面
    private List<TaskDto> myTask;
    private RecyclerView myTaskRV;
    private TaskItemAdapter myTaskAdapter;
    private SwipeRefreshLayout swipeMyTask;

    private static CommunityFragment fragment;

    public CommunityFragment() {
    }

    public static CommunityFragment newInstance() {
        if (fragment == null) {
            fragment = new CommunityFragment();
        }
        return fragment;
    }

    @SuppressLint("InflateParams")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        @SuppressLint("InflateParams") View v = inflater.inflate(R.layout.fragment_community, null);
        tabLayout = (SlidingTabLayout) v.findViewById(R.id.community_tab);
        viewPager = (ViewPager) v.findViewById(R.id.page);
        searchView = (SearchView) v.findViewById(R.id.searchView);
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tabLayout.setVisibility(View.INVISIBLE);
            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                tabLayout.setVisibility(View.VISIBLE);
                return false;
            }
        });
        allTaskPage = inflater.inflate(R.layout.page_all_task, null);
        myTaskPage = inflater.inflate(R.layout.page_my_task, null);

        //填充数据源
        viewList.add(allTaskPage);
        viewList.add(myTaskPage);

        //设置适配器
        MyPagerAdapter adapter = new MyPagerAdapter(viewList);
        viewPager.setAdapter(adapter);

        tabLayout.setViewPager(viewPager);

        listener = new TaskItemCallBackListener() {
            @Override
            public void onIcon(int userId) {
                Intent intent = new Intent(getContext(), OthersInfoActivity.class);
                intent.putExtra("userId", userId);
                getActivity().startActivity(intent);
            }

            @Override
            public void onMain(int taskId) {
                Intent intent = new Intent(getContext(), TaskDetailActivity.class);
                intent.putExtra("taskId", taskId);
                getActivity().startActivity(intent);
            }
        };

        initAllTaskPage(allTaskPage);
        initMyTaskPage(myTaskPage);
        return v;
    }

    private void initMyTaskPage(View view) {
        myTaskRV = (RecyclerView) view.findViewById(R.id.my_task_rv);
        swipeMyTask = (SwipeRefreshLayout) view.findViewById(R.id.swipe_my_task);
        swipeMyTask.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshMyTask();
            }
        });
        swipeMyTask.setRefreshing(true);
        refreshMyTask();
    }


    private void initAllTaskPage(View view) {
        allTaskRV = (RecyclerView) view.findViewById(R.id.all_task_rv);
        swipeAllTask = (SwipeRefreshLayout) view.findViewById(R.id.swipe_all_task);
        swipeAllTask.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshAllTask();
            }
        });
        swipeAllTask.setRefreshing(true);
        refreshAllTask();
    }

    public void refreshMyTask() {
        HttpUtil.getInstance().get(UrlHandler.getMyTask(), new HttpCallbackListener() {


            @Override
            public void onFinish(String response) {
                Log.i("mytask", "onFinish: " + response);
                myTask = new Gson().fromJson(response, new TypeToken<List<TaskDto>>() {
                }.getType());
                if (myTaskAdapter == null) {
                    myTaskAdapter = new TaskItemAdapter();
                    myTaskAdapter.setContext(getContext());
                    myTaskAdapter.setListener(listener);
                    myTaskRV.setLayoutManager(new LinearLayoutManager(getContext()));
                    myTaskRV.setAdapter(myTaskAdapter);
                }
                if (myTask == null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (swipeAllTask.isRefreshing()) {
                                swipeAllTask.setRefreshing(false);
                            }
                        }
                    });
                    return;
                }
                myTaskAdapter.setTaskDtos(myTask);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        myTaskAdapter.notifyDataSetChanged();
                        if (swipeMyTask.isRefreshing()) {
                            swipeMyTask.setRefreshing(false);
                        }
                    }
                });

            }
        });

    }

    public void refreshAllTask() {
        if (!swipeAllTask.isRefreshing()) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    swipeAllTask.setRefreshing(true);

                }
            });
        }
        //获取数据
        HttpUtil.getInstance().get(UrlHandler.getAllTask(LocationUtil.getInstance().getLatitude(),
                LocationUtil.getInstance().getLongitude(), 5.0), new HttpCallbackListener() {


            @Override
            public void onFinish(String response) {
                Log.i("alltask", "onFinish: " + response);
                allTask = new Gson().fromJson(response, new TypeToken<List<TaskDto>>() {
                }.getType());
                if (allTaskAdapter == null) {
                    allTaskAdapter = new TaskItemAdapter();
                    allTaskAdapter.setContext(getContext());
                    allTaskAdapter.setListener(listener);
                    allTaskRV.setLayoutManager(new LinearLayoutManager(getContext()));
                    allTaskRV.setAdapter(allTaskAdapter);
                }
                if (allTask == null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (swipeAllTask.isRefreshing()) {
                                swipeAllTask.setRefreshing(false);
                            }
                        }
                    });

                    return;
                }
                allTaskAdapter.setTaskDtos(allTask);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        allTaskAdapter.notifyDataSetChanged();
                        if (swipeAllTask.isRefreshing()) {
                            swipeAllTask.setRefreshing(false);
                        }
                    }
                });

            }
        });

    }

    //ViewPager适配器
    class MyPagerAdapter extends PagerAdapter {
        private List<View> mViewList;

        MyPagerAdapter(List<View> mViewList) {
            this.mViewList = mViewList;
        }

        @Override
        public int getCount() {
            return mViewList.size();//页卡数
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;//官方推荐写法
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(mViewList.get(position));//添加页卡
            return mViewList.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(mViewList.get(position));//删除页卡
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titleList.get(position);//页卡标题
        }

    }
}
