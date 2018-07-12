package com.mall.flickrdownloadtags.ViewFragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.mall.flickrdownloadtags.Interfaces.OnPhotoSearchRequestListener;
import com.mall.flickrdownloadtags.Interfaces.OnPhotoSelectedListener;
import com.mall.flickrdownloadtags.Models.Photo;
import com.mall.flickrdownloadtags.R;
import com.mall.flickrdownloadtags.RecyclerViewComponents.PhotoAdapter;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class GalleryViewFragment extends Fragment implements OnPhotoSelectedListener {

    public static final String TAG = "GalleryFragment";

    @BindView(R.id.search_view)
    SearchView searchView;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.empty_list_background)
    LinearLayout emptyListBackground;
    Unbinder unbinder;
    @BindView(R.id.search_frag)
    FrameLayout searchFrag;
    @BindView(R.id.swipe_refresher)
    SwipeRefreshLayout swipeRefresher;

    private String searchQuery;
    private String urlString;

    private ArrayList<Photo> mPhotos;

    private PhotoAdapter mAdapter;

    private OnPhotoSearchRequestListener mListener;
    private OnPhotoSaveListener mPhotoSaveListener;

    public interface OnPhotoSaveListener {
        void onPhotoSaveInitiated(Photo photo);
    }

    /**
     * Fragment Lifecycle Methods
     */


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_gallery_view, container, false);

        unbinder = ButterKnife.bind(this, view);

        searchFrag.setVisibility(View.VISIBLE);

        mPhotos = new ArrayList<>();

        mAdapter = new PhotoAdapter(getActivity(), mPhotos, this);

        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        recyclerView.setAdapter(mAdapter);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchQuery = query;
                query.replace(" ", ",").toLowerCase();
                mListener.onPhotoSearchRequest(searchQuery);
                searchView.setQuery(searchQuery, false);
                searchView.onActionViewCollapsed();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                if (mAdapter.getItemCount() == 0) {
                    emptyListBackground.setVisibility(View.VISIBLE);
                } else {
                    emptyListBackground.setVisibility(View.INVISIBLE);
                }
            }
        });

        swipeRefresher.setOnRefreshListener(null);
        swipeRefresher.setRefreshing(false);
        swipeRefresher.setEnabled(false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Toast.makeText(getActivity(), "Load 25 photos.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        mListener = null;
        //mPhotoSaveListener = null;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (OnPhotoSearchRequestListener) context;
            //mPhotoSaveListener = (OnPhotoSaveListener) context;
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }

    /**
     * Class Methods
     */


    public void setPhotos(ArrayList<Photo> photos) {

        if(swipeRefresher.isRefreshing()){
            swipeRefresher.setRefreshing(false);
        }

        searchView.setQuery(searchQuery, false);

        if (photos != null) {
            mPhotos = photos;
        } else {
            mPhotos = new ArrayList<>();
        }
        mAdapter.setPhotoDataSet(mPhotos);
        mAdapter.notifyDataSetChanged();

    }

    /**
     * OnPhotoSelectedListener Methods
     */

    @Override
    public void onPhotoSelected(Photo photo) {
        //We dont do anythinhg
    }
}
