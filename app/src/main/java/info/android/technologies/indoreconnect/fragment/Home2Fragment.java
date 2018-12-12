package info.android.technologies.indoreconnect.fragment;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import info.android.technologies.indoreconnect.R;
import info.android.technologies.indoreconnect.activity.ItemClickListener;
import info.android.technologies.indoreconnect.activity.MainActivity;
import info.android.technologies.indoreconnect.adapter.Adp_Search;
import info.android.technologies.indoreconnect.adapter.Adp_ShopListing;
import info.android.technologies.indoreconnect.adapter.ImagePagerAdapter;
import info.android.technologies.indoreconnect.model.BuisnessList;
import info.android.technologies.indoreconnect.model.HomeCate;
import info.android.technologies.indoreconnect.model.Search;
import info.android.technologies.indoreconnect.util.AutocompleteCustomArrayAdapter;
import info.android.technologies.indoreconnect.util.ConnectionDetector;
import info.android.technologies.indoreconnect.util.CustomAutoCompleteView;
import info.android.technologies.indoreconnect.util.RoundedCornersTransformation;
import info.android.technologies.indoreconnect.util.SessionManager;
import info.android.technologies.indoreconnect.util.WebAPI;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by kamlesh on 1/11/2018.
 */
public class Home2Fragment extends Fragment implements View.OnClickListener {

    Context ctx;
    View view;

    TextView bottom_line;
    CustomAutoCompleteView autoCompleteTextView;
    ImageView search_iv;

    ImagePagerAdapter pagerAdapter;
    RecyclerView recyclerView_vertical, recyclerView_buiss_listing;
    ConnectionDetector connectionDetector;

    SessionManager sessionManager;

    ArrayList<BuisnessList> buisnessLists;
    ArrayList<HomeCate> category_list;
    ArrayList<HomeCate> sub_category_list;
    ArrayList<String> main_cate_list;
    ArrayList<String> imaglist;

    public Home2Fragment(Context ctx) {
        this.ctx = ctx;
        connectionDetector = new ConnectionDetector();
        sessionManager = new SessionManager(ctx);
        category_list = new ArrayList<>();
        main_cate_list = new ArrayList<>();
        imaglist = new ArrayList<>();
        buisnessLists = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_home2, null);
        initxml();
        setSearching();
        boolean internet = connectionDetector.isConnected();
        if (internet) {
            loadImages();
        } else {
            toastView("No internet connectiom");
        }
        return view;
    }

    private void setSearching() {

        String response = sessionManager.getData(SessionManager.KEY_SEARCH_DATA);
        final ArrayList<Search> search_list = new ArrayList<>();
        ArrayList<String> search_name_list = new ArrayList<>();

        try {
            JSONArray array = new JSONArray(response);
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                String id = object.getString("id");
                String name = object.getString("name");
                String type = object.getString("type");
                search_list.add(new Search(name, id, type));
                search_name_list.add(name);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

//        ArrayAdapter adapter = new ArrayAdapter<String>(ctx, android.R.layout.simple_dropdown_item_1line, search_name_list);

        AutocompleteCustomArrayAdapter adapter = new AutocompleteCustomArrayAdapter(ctx, R.layout.custom_actv, search_name_list);
        autoCompleteTextView.setThreshold(1);
        autoCompleteTextView.setAdapter(adapter);
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String selectd = autoCompleteTextView.getText().toString();
                String id1 = "";
                String type = "";
                for (int i = 0; i < search_list.size(); i++) {

                    if (selectd.equals(search_list.get(i).getName())) {
                        id1 = search_list.get(i).getId();
                        type = search_list.get(i).getType();
                    }
                }
                sessionManager.setData(SessionManager.KEY_SEARCH_ID, id1);
                sessionManager.setData(SessionManager.KEY_SEARCH_TYPE, type);
                sessionManager.setData(SessionManager.KEY_SEARCH_FROM, "home");
                Search2Fragment searchFragment = new Search2Fragment(ctx);
                setFragment(searchFragment);
            }
        });
    }

    private void toastView(String s) {
        Toast.makeText(ctx, s, Toast.LENGTH_LONG).show();
    }

    private void initxml() {
        search_iv = (ImageView) view.findViewById(R.id.iv_home2_search);
        recyclerView_vertical = (RecyclerView) view.findViewById(R.id.rv_home2_listing_vertical);
        recyclerView_buiss_listing = (RecyclerView) view.findViewById(R.id.rv_home2_recycler_view_buis_listing);

        bottom_line = (TextView) view.findViewById(R.id.tv_home2_bottomline);
        autoCompleteTextView = (CustomAutoCompleteView) view.findViewById(R.id.actv_home2_searching);

        search_iv.setOnClickListener(this);
/*
// example of creating header and footer views from inflation or by instantiation in code
        View myHeaderView = getActivity().getLayoutInflater().inflate(R.layout.banner_home, recyclerView_vertical, false);
        viewpager = (ViewPager) myHeaderView.findViewById(R.id.vp_home2_viewpager);

        recyclerView_vertical.addView(myHeaderView);*/
    }

    private void loadImages() {

        final ProgressDialog pd = new ProgressDialog(ctx);
        pd.setMessage("wait..");
        pd.setCancelable(false);
        pd.show();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, WebAPI.URL_BANNER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        pd.dismiss();
                        sessionManager.setData(SessionManager.KEY_BANNER, response);
                        try {
                            JSONArray imageArray = new JSONArray(response);
                            for (int i = 0; i < imageArray.length(); i++) {
                                JSONObject imgobj = imageArray.getJSONObject(i);
                                String url = imgobj.getString("image");
                                imaglist.add(url);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } finally {
                            loadCategory();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ctx, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
        RequestQueue requestQueue = Volley.newRequestQueue(ctx);
        requestQueue.add(stringRequest);
    }

    private void loadCategory() {

        category_list = new ArrayList<>();
        main_cate_list = new ArrayList<>();
        final ProgressDialog pd = new ProgressDialog(ctx);
        pd.setMessage("wait..");
        pd.setCancelable(false);
        pd.show();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, WebAPI.URL_CATEGORY,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        pd.dismiss();
                        try {
                            JSONArray dataarray = new JSONArray(response);
                            for (int i = 0; i < dataarray.length(); i++) {
                                JSONObject imgobj = dataarray.getJSONObject(i);
                                String parent_cate = imgobj.getString("parent_category");
                                String sub_cate = imgobj.getString("sub_category");
                                String category_icon = imgobj.getString("category_icon");
                                String category_id = imgobj.getString("category_id");

                                if (!parent_cate.equals("category_info")) {
                                    category_list.add(new HomeCate(parent_cate, category_id, sub_cate, "", category_icon));
                                    if (!main_cate_list.contains(parent_cate)) {
                                        main_cate_list.add(parent_cate);
                                    }
                                }
                            }
                            setCategory();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ctx, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
        RequestQueue requestQueue = Volley.newRequestQueue(ctx);
        requestQueue.add(stringRequest);
    }

    private void setBuisness() {

        double latitude = sessionManager.getDouble(SessionManager.KEY_LAT);
        double longitude = sessionManager.getDouble(SessionManager.KEY_LONG);

        final SessionManager sessionManager = new SessionManager(ctx);
//        sessionManager.setData(SessionManager.KEY_PAGE, "homelisting");
        recyclerView_vertical.setVisibility(View.GONE);
        bottom_line.setVisibility(View.GONE);
        recyclerView_buiss_listing.setVisibility(View.VISIBLE);

        final Adp_ShopListing adp_shopListing = new Adp_ShopListing(buisnessLists, ctx, latitude, longitude);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(ctx.getApplicationContext());
        recyclerView_buiss_listing.setLayoutManager(mLayoutManager);
        recyclerView_buiss_listing.setAdapter(adp_shopListing);

        recyclerView_buiss_listing.addOnItemTouchListener(
                new SearchFragment.RecyclerItemClickListener(ctx, recyclerView_vertical, new SearchFragment.RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {

                        sessionManager.setData(SessionManager.KEY_BUISNESS_ID, buisnessLists.get(position).getBuisness_id());
                        ShopDetailFragment detailFragment = new ShopDetailFragment(ctx);
                        setFragment(detailFragment);
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        // do whatever
                    }
                })
        );
    }

    private void setFragment(Fragment fragment) {

        final android.app.FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.framelayout, fragment);
        ft.commit();
    }

    private void setCategory() {

        HomeAdapter adapter = new HomeAdapter();
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(ctx.getApplicationContext());
        recyclerView_vertical.setLayoutManager(mLayoutManager);
        recyclerView_vertical.setItemAnimator(new DefaultItemAnimator());
        recyclerView_vertical.setAdapter(adapter);
    }

    RecyclerView.Adapter mAdapter;

    @Override
    public void onClick(View v) {

        String text = autoCompleteTextView.getText().toString();
        if (!text.equals("")) {
            sessionManager.setData(SessionManager.KEY_PAGE, "subhome");
            sessionManager.setData(SessionManager.KEY_SEARCH_TEXT, text);
            sessionManager.setData(SessionManager.KEY_ALL_BUISNESS_TYPE, "search");
            AllBuisnessListingFragment allBuisnessListingFragment = new AllBuisnessListingFragment(ctx);
            setFragment(allBuisnessListingFragment);
        } else {
            toastView("please enter text in search");
        }
    }

    public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.MyViewHolder> {

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView tv_adphome2_category_name, tv_see_all;
            RecyclerView recyclerView_horizontal;
            ViewPager viewpager;
            RelativeLayout relativeLayout;

            RecyclerView.LayoutManager mLayoutManager;

            public MyViewHolder(final View view) {
                super(view);
                viewpager = (ViewPager) view.findViewById(R.id.vp_home2_viewpager);
                relativeLayout = (RelativeLayout) view.findViewById(R.id.rl_banner_home);
                tv_adphome2_category_name = (TextView) view.findViewById(R.id.tv_adphome2_categoryname);
                tv_see_all = (TextView) view.findViewById(R.id.tv_adphome2_seeall);
                recyclerView_horizontal = (RecyclerView) view.findViewById(R.id.rv_home2_listing_horizontal);




                TimerTask timerTask = new TimerTask() {
                    @Override
                    public void run() {
                        viewpager.post(new Runnable(){

                            @Override
                            public void run() {
                                viewpager.setCurrentItem((viewpager.getCurrentItem()+1)%imaglist.size());
                            }
                        });
                    }

                };
               Timer timer = new Timer();
                timer.schedule(timerTask, 3000, 3000);
            }
        }


        public HomeAdapter() {
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.adp_home_listing_vertical, parent, false);

            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            final String main_cate_name = main_cate_list.get(position);
            holder.tv_adphome2_category_name.setText(main_cate_name);

            holder.recyclerView_horizontal.setHasFixedSize(true);

            // The number of Columns
            holder.mLayoutManager = new LinearLayoutManager(ctx, LinearLayoutManager.HORIZONTAL, false);
            holder.recyclerView_horizontal.setLayoutManager(holder.mLayoutManager);

            if (position == 0) {
                pagerAdapter = new ImagePagerAdapter(ctx, imaglist);
                holder.viewpager.setAdapter(pagerAdapter);
            } else {
                holder.relativeLayout.setVisibility(View.GONE);
                holder.viewpager.setVisibility(View.GONE);
            }

            sub_category_list = new ArrayList<>();
            for (int i = 0; i < category_list.size(); i++) {
                if (category_list.get(i).getCategory_name().equals(main_cate_name)) {
                    sub_category_list.add(category_list.get(i));
                }
            }

            mAdapter = new Adp_listing_horizontal(ctx, sub_category_list);
            holder.recyclerView_horizontal.setAdapter(mAdapter);

            holder.tv_see_all.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    sessionManager.setData(SessionManager.KEY_PAGE, "subhome");
                    sessionManager.setData(SessionManager.KEY_MAIN_CATEGORY_NAME, main_cate_name);
                    SeeAllCategoryHomeFragment categoryHomeFragment = new SeeAllCategoryHomeFragment(ctx);
                    setFragment(categoryHomeFragment);
                }
            });
        }

        @Override
        public int getItemViewType(int position) {
            if (position == 0) {
                return 1;
            } else {
                return 0;
            }
        }

        @Override
        public int getItemCount() {
            return main_cate_list.size();
        }
    }

    public class Adp_listing_horizontal extends RecyclerView.Adapter<Adp_listing_horizontal.ViewHolder> {

        ArrayList<HomeCate> subcategory_list;
        Context context;

        public Adp_listing_horizontal(Context context, ArrayList<HomeCate> subcategory_list) {
            super();
            this.context = context;
            this.subcategory_list = subcategory_list;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.grid_item, viewGroup, false);
            ViewHolder viewHolder = new ViewHolder(v);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            viewHolder.name_tv.setText(subcategory_list.get(i).getSubcate_name());

            String url = subcategory_list.get(i).getSubcate_image();
            if (!url.equals("")) {
                String icon_url = WebAPI.BASE_URL + "admin/img/category_icon/" + url;
                icon_url = icon_url.replace(" ", "%20");
                Picasso.with(context)
                        .load(icon_url)
//                        .resize(80, 80)
                        .placeholder(R.drawable.blue_ic_icon)
                        .transform(new RoundedCornersTransformation(10, 1))
                        .error(R.drawable.blue_ic_icon)
                        .into(viewHolder.image);
            }

            viewHolder.setClickListener(new ItemClickListener() {
                @Override
                public void onClick(View view, int position, boolean isLongClick) {
                    if (isLongClick) {

                    } else {
//                        sessionManager.setData(SessionManager.KEY_CATEGORY_NAME, subcategory_list.get(position).getSubcate_name());
//                        loadBuisnessList(subcategory_list.get(position).getCategory_id());

                        sessionManager.setData(SessionManager.KEY_PAGE, "allbuisness");
                        sessionManager.setData(SessionManager.KEY_ALL_BUISNESS_TYPE, "normal");
                        sessionManager.setData(SessionManager.KEY_MAIN_BUISNESS_ID, subcategory_list.get(position).getCategory_id());
                        AllBuisnessListingFragment allBuisnessListingFragment = new AllBuisnessListingFragment(ctx);
                        setFragment(allBuisnessListingFragment);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return subcategory_list.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

            public ImageView image;
            public TextView name_tv;
            private ItemClickListener clickListener;

            public ViewHolder(View itemView) {
                super(itemView);
                image = (ImageView) itemView.findViewById(R.id.iv_adphome_hori_image);
                name_tv = (TextView) itemView.findViewById(R.id.tv_adphome_hori_name);
                itemView.setOnClickListener(this);
                itemView.setOnLongClickListener(this);
            }

            public void setClickListener(ItemClickListener itemClickListener) {
                this.clickListener = itemClickListener;
            }

            @Override
            public void onClick(View view) {
                clickListener.onClick(view, getPosition(), false);
            }

            @Override
            public boolean onLongClick(View view) {
                clickListener.onClick(view, getPosition(), true);
                return true;
            }
        }
    }

}
