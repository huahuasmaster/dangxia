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

public class EmptyFragment extends Fragment{

    private static EmptyFragment fragment;

    public EmptyFragment() {
    }

    public static EmptyFragment newInstance() {
        if(fragment == null) {
            fragment = new EmptyFragment();
        }
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_message,null);
        return v;
    }
}
