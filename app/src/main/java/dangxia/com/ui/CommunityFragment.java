package dangxia.com.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.flyco.tablayout.SlidingTabLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import dangxia.com.R;

/**
 * Created by zhuang_ge on 2017/11/17.
 *
 */

public class CommunityFragment extends Fragment{

    private SlidingTabLayout tabLayout;
    private ViewPager viewPager;
    private View allTaskPage;
    private View myTaskPage;
    private SearchView searchView;
    private String[] titles = {"已发布","待解决"};
    private List<String> titleList = Arrays.asList(titles);
    private List<View> viewList = new ArrayList<>();

    private static CommunityFragment fragment;

    public CommunityFragment() {
    }

    public static CommunityFragment newInstance() {
        if(fragment == null) {
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
        allTaskPage = inflater.inflate(R.layout.page_all_task,null);
        myTaskPage = inflater.inflate(R.layout.page_my_task,null);

        //填充数据源
        viewList.add(allTaskPage);
        viewList.add(myTaskPage);

        //设置适配器
        MyPagerAdapter adapter = new MyPagerAdapter(viewList);
        viewPager.setAdapter(adapter);

        tabLayout.setViewPager(viewPager);
        initAllTaskPage(allTaskPage);
        return v;
    }

    private void initAllTaskPage(View view) {
        view.findViewById(R.id.include1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(),TaskDetailActivity.class);
                getActivity().startActivity(intent);
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
