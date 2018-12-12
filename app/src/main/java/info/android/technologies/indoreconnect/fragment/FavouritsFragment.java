package info.android.technologies.indoreconnect.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ListView;
import android.widget.TextView;

import info.android.technologies.indoreconnect.R;
import info.android.technologies.indoreconnect.adapter.Adp_Fav_Listing;
import info.android.technologies.indoreconnect.adapter.Adp_ShopListing;
import info.android.technologies.indoreconnect.model.BuisnessList;
import info.android.technologies.indoreconnect.model.Favourites;
import info.android.technologies.indoreconnect.util.HelperManager;
import info.android.technologies.indoreconnect.util.SessionManager;

import java.util.ArrayList;

/**
 * Created by kamlesh on 12/7/2017.
 */
public class FavouritsFragment extends Fragment {
    Context ctx;
    View view;

    RecyclerView recyclerView;
    TextView nodata_tv;

    HelperManager helperManager;
    SessionManager sessionManager;

    ArrayList<BuisnessList> fav_list;
    ArrayList<BuisnessList> buisness_List;

    Adp_ShopListing adp_shopListing;

    public FavouritsFragment(Context ctx) {
        this.ctx = ctx;
        helperManager = new HelperManager(ctx);
        sessionManager = new SessionManager(ctx);
        fav_list = new ArrayList<>();
        buisness_List = new ArrayList<>();
    }

    public FavouritsFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_favourites, null);
        initxml();
        setData();
        return view;
    }

    private void setData() {

        SessionManager sessionManager = new SessionManager(ctx);
        double latitude = sessionManager.getDouble(SessionManager.KEY_LAT);
        double longitude = sessionManager.getDouble(SessionManager.KEY_LONG);

        fav_list = helperManager.read();
        if (fav_list.size() == 0) {
            recyclerView.setVisibility(View.GONE);
            nodata_tv.setVisibility(View.VISIBLE);
        } else {
            adp_shopListing = new Adp_ShopListing(fav_list, ctx, latitude, longitude);

            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(ctx.getApplicationContext());
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setAdapter(adp_shopListing);
        }
    }

    private void initxml() {
        recyclerView = (RecyclerView) view.findViewById(R.id.rv_fav_recycleview);
        nodata_tv = (TextView) view.findViewById(R.id.tv_fav_nodata);

        recyclerView.addOnItemTouchListener(
                new SearchFragment.RecyclerItemClickListener(ctx, recyclerView, new SearchFragment.RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {

                        sessionManager.setData(SessionManager.KEY_PAGE, "fav");
                        sessionManager.setData(SessionManager.KEY_BUISNESS_ID, fav_list.get(position).getBuisness_id());
                        ShopDetailFragment detailFragment = new ShopDetailFragment(ctx);
                        setFragment(detailFragment);
                    }

                    @Override
                    public void onLongItemClick(View view, final int position) {
                        // do whatever
//                        removeBuisnessDialog(fav_list.get(position).getBuisness_id());
                        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
                        builder.setMessage("Are you sure you want remove from favourite list?")
                                .setCancelable(false)
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {

                                        int id1 = Integer.parseInt(fav_list.get(position).getBuisness_id());
                                        helperManager.remove(id1);
                                        fav_list.remove(position);
                                        recyclerView.removeViewAt(position);
                                        adp_shopListing.notifyItemRemoved(position);
                                        adp_shopListing.notifyItemRangeChanged(position, fav_list.size());
                                    }
                                })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });
                        AlertDialog alert = builder.create();
                        alert.show();
                    }
                })
        );
    }

    private void setFragment(Fragment fragment) {

        final android.app.FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.framelayout, fragment);
        ft.commit();
    }
}
