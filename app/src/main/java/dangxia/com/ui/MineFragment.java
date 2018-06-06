package dangxia.com.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Objects;

import dangxia.com.R;
import dangxia.com.utils.http.UrlHandler;

/**
 * Created by zhuang_ge on 2017/11/17.
 */

public class MineFragment extends Fragment implements View.OnClickListener {

    private static MineFragment fragment;

    public MineFragment() {
    }

    public static MineFragment newInstance() {
        if (fragment == null) {
            fragment = new MineFragment();
        }
        return fragment;
    }

    private ConstraintLayout checkServed;
    private ConstraintLayout checkBeServed;
    private ImageView icon;
    private TextView name;
    private TextView account;

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_mine, null);
        checkServed = v.findViewById(R.id.cons_check_served);
        checkBeServed = v.findViewById(R.id.cons_check_be_served);
        icon = v.findViewById(R.id.icon);
        name = v.findViewById(R.id.name);
        account = v.findViewById(R.id.accout);
        SharedPreferences sp = Objects.requireNonNull(getActivity()).getSharedPreferences("login_data", Context.MODE_PRIVATE);
        icon.setImageResource(UrlHandler.getUserId() == 2 ?
                R.mipmap.doge : R.mipmap.doge2);
        name.setText(sp.getString("name", ""));
        account.setText("账号：" + sp.getString("phone", ""));
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
