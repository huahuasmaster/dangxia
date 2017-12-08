package dangxia.com.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import dangxia.com.R;

/**
 * Created by zhuang_ge on 2017/11/17.
 */

public class MineFragment extends Fragment{

    private static MineFragment fragment;

    public MineFragment() {
    }

    public static MineFragment newInstance() {
        if(fragment == null) {
            fragment = new MineFragment();
        }
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_mine,null);
        return v;
    }
}
