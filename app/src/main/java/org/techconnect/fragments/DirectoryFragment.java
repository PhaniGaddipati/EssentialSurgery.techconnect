package org.techconnect.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.centum.techconnect.R;
import org.techconnect.activities.ProfileActivity;
import org.techconnect.adapters.UserListAdapter;
import org.techconnect.asynctasks.SearchUsersAsyncTask;
import org.techconnect.model.User;
import org.techconnect.sql.TCDatabaseHelper;
import org.techconnect.views.UserListItemView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class DirectoryFragment extends Fragment implements View.OnClickListener {

    @Bind(R.id.search_editText)
    EditText searchEditText;
    @Bind(R.id.clear_search_imageView)
    ImageView clearSearchImageView;
    @Bind(R.id.content_linearLayout)
    LinearLayout contentLinearLayout;
    @Bind(R.id.user_ListView)
    ListView userListView;
    @Bind(R.id.progress)
    ProgressBar progressBar;
    @Bind(R.id.offline_label)
    TextView offlineTextView;

    private UserListAdapter adapter;
    private boolean isLoading = false;

    public DirectoryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_directory, container, false);
        ButterKnife.bind(this, view);
        Log.d("Directory Setup", "View Initialized");
        adapter = new UserListAdapter();
        userListView.setAdapter(adapter);
        userListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                User user = ((UserListItemView) view).getUser();
                Intent intent = new Intent(getContext(), ProfileActivity.class);
                intent.putExtra("user", user);
                startActivity(intent);
            }
        });
        searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                onRefresh(true);
                return true;
            }
        });
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                onRefresh(false);
            }
        });
        onRefresh(true);
        clearSearchImageView.setOnClickListener(this);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null) {
            getActivity().setTitle(R.string.directory);
        }
        onRefresh(true);
    }

    public void onRefresh(boolean force) {
        if (!isLoading || force) {
            adapter.setUsers(new ArrayList<User>());
            progressBar.setVisibility(View.VISIBLE);
            userListView.setVisibility(View.GONE);
            offlineTextView.setVisibility(View.GONE);
            isLoading = true;
            String query = searchEditText.getText().toString();
            new SearchUsersAsyncTask(getActivity(), query, 10, 0) {

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                }

                @Override
                protected void onPostExecute(List<User> users) {
                    if (users == null) {
                        adapter.setUsers(TCDatabaseHelper.get(getActivity()).getAllUsers());
                        offlineTextView.setVisibility(View.VISIBLE);
                    } else {
                        adapter.setUsers(users);
                    }
                    adapter.notifyDataSetChanged();
                    isLoading = false;
                    userListView.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                }

                @Override
                protected void onCancelled() {
                    isLoading = false;
                    userListView.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    adapter.setUsers(TCDatabaseHelper.get(getActivity()).getAllUsers());
                    offlineTextView.setVisibility(View.VISIBLE);
                }
            }.execute();
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.clear_search_imageView) {
            searchEditText.setText(null);
            onRefresh(true);
        }
    }
}