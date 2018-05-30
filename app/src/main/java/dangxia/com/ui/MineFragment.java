package dangxia.com.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Objects;

import dangxia.com.R;

/**
 * Created by zhuang_ge on 2017/11/17.
 */

public class MineFragment extends Fragment implements View.OnClickListener {

    private static MineFragment fragment;

    public MineFragment() {
    }

    public static MineFragment newInstance() {
        if(fragment == null) {
            fragment = new MineFragment();
        }
        return fragment;
    }

    private ConstraintLayout checkServed;
    private ConstraintLayout checkBeServed;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_mine,null);
        checkServed = v.findViewById(R.id.cons_check_served);
        checkBeServed = v.findViewById(R.id.cons_check_be_served);
        Intent intent = new Intent(getContext(), HistoryActivity.class);
        checkServed.setOnClickListener(view -> {
            intent.putExtra("showProvidedServices", true);
            Objects.requireNonNull(getActivity()).startActivity(intent);
        });
        checkBeServed.setOnClickListener(view -> {
            intent.putExtra("showProvidedServices", false);
            Objects.requireNonNull(getActivity()).startActivity(intent);
        });
        return v;
    }

    @Override
    public void onClick(View view) {

    }
}
