package com.stackrage.gofeds.ui.profile;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;
import com.stackrage.gofeds.LoadingIndicator;
import com.stackrage.gofeds.LoginActivity;
import com.stackrage.gofeds.PortAdapter;
import com.stackrage.gofeds.R;
import com.stackrage.gofeds.SignupActivity;
import com.stackrage.gofeds.api.ApiClient;
import com.stackrage.gofeds.api.ApiInterface;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {

    public static final String PREF_ID = "PREFERENCE_ID";
    public static final String PREF_USERNAME = "PREFERENCE_USERNAME";
    public static final String PREF_EMAIL = "PREFERENCE_EMAIL";
    public static final String PREF_RANK = "PREFERENCE_RANK";
    public static final String PREF_AGENCY = "PREFERENCE_AGENCY";
    public static final String PREF_OFFICE = "PREFERENCE_OFFICE";
    public static final String PREF_CURRENTPORT = "PREFERENCE_CURRENTPORT";
    public static final String PREF_DESIREPORT = "PREFERENCE_DESIREPORT";
    public static final String PREF_FTOKEN = "PREFERENCE_FTOKEN";

    private ImageView iv_profile, iv_logout_btn;
    private TextView tv_username;
    private TextView tv_rank, tv_agency, tv_office, tv_currentport, tv_desiredport, tv_save_btn;
    private RecyclerView portRecyclerView;
    private PortAdapter portAdapter;

    private LoadingIndicator loadingIndicator;

    private String[] portList = {"Albany, NY","Albuquerque, NM","Alexandria Bay, NY","Ambrose, ND","Anacortes, WA","Andrade, CA","Antler, ND","Appleton International Airport, WI","Ashtabula/Conneaut, OH","Astoria, OR","Atlanta, GA","Atlantic City User Fee Airport, NJ","Austin, TX","Baltimore, MD","Bangor, ME","Austin, TX","Baltimore, MD","Bangor, ME","Baton Rouge, LA","Battle Creek, MI","Baudette, MN","Beaufort-Morehead, NC","Beecher Falls, VT","Bellingham, WA","Binghamton Regional Airport, NY","Birmingham, AL","Blaine, WA","Blue Grass Airport, KY","Boise, ID","Bozeman Yellowstone, MT","Bridgewater, ME","Brownsville, TX","Brunswick, GA","Buffalo, NY","Calais, ME","Calexico, CA","Carbury, ND","Centennial Airport, Englewood, CO","Champlain, NY","Charleston, SC","Charlotte Amalie, USVI","Charlotte, NC","Chattanooga, TN","Chester PA/Wilmington, DE","Chicago, IL","Cincinnati, OH-Lawrenceburg, IN","Cleveland, OH","Saipan, CNMI","Cobb County Airport, GA","Columbia, SC","Columbus, NM","Columbus, OH","Corpus Christi, TX","Cristiansted, VI","Dallas/Fort Worth, TX","Dalton Cache, AK","Danville, WA","Dayton, OH","Daytona Beach, FL","Del Bonita, MT","Del Rio, TX","Denver, CO","Derby Line, VT","Detroit, MI","Detroit (Airport), MI","Douglas, AZ","Dulles Airport, VA","Duluth, MN","Dunseith, ND","Durham, NC","Eagle County, CO","Eagle Pass, TX","Eastport, ID","Eastport, ME","El Paso, TX","Erie, PA","Eureka, CA","Fairbanks, AK","Fajardo Culebra, PR","Fajardo Vieques, PR","Fargo, ND","Fernandina, FL","Fort Fairfield, ME","Fort Kent, ME","Fort Lauderdale, FL","Fort Myers, FL","Fortuna, ND","Freeport, TX","Fresno, CA","Friday Harbor, WA","Frontier, WA","Galveston, TX","Gramercy, LA","Grand Forks, ND","Grand Portage, MN","Grand Rapids, MI","Grant County, WA","Great Falls, MT","Green Bay, WI","Greenville-Spartanburg, SC","Griffiss, NY","Guam, GU","Gulfport, MS","Hannah, ND","Hansboro, ND","Harrisburg, PA","Hartford, CT","Helena, MT","Hidalgo/Pharr, TX","Highgate Springs, VT","Hillsboro, OR","Honolulu, HI","Houlton, ME","Houston (Airport), TX","Houston (Seaport), TX","Huntsville, AL","Indianapolis, IN","International Falls-Rainer, MN","Jackman-Cobum Gore, ME","Jackman-Jackman, ME","Jacksonville, FL","Jefferson City, CO","JFK, NY","Juneau, AK","Kahului, HI","Kalispell, MT","Kansas City, MO","Ketchikan, AK","Key West, FL","Knoxville, TN","Kona-Hilo-Hilo, HI","Kona-Hilo-Kailua Kona, HI","Long Beach, CA","Lake Charles, LA","Lancaster, MN","Laredo, TX","Las Vegas, NV","Laurier, WA","LAX, CA","Leesburg, FL","Lehigh Valley, PA","Little Rock, AR","Logan, MA","Longview, WA","Louisville, KY","Lukeville, AZ","Lynden, WA","Madawaska, ME","Maguire, NJ","Maida, ND","Manatee, FL","Manchester User Fee, NH","Marathon, FL","Massena, NY","Melbourne, FL","Memphis, TN","Metaline Falls, WA","Miami Seaport, FL","Miami Airport, FL","Mid America, IL","Midland, TX","Milwaukee, WI","Minneapolis, MN","Minot, ND","Mobile, AL","Morgan City, LA","Morgan, MT","Naco, AZ","Naples, FL","Nashville, TN","Neche, ND","New Bedford, MA","New Haven, CT","New Orleans, LA","Newark, NJ","Newport, OR","Nogales, AZ","Noonan, ND","Norfolk, VA","Northgate, ND","Norton, VT","Ogdensburg, NY","Oklahoma City, OK","Omaha, NE","Ontario, CA","Opheim, MT","Orlando Executive, FL","Orlando, FL","Oroville, WA","Otay Mesa, CA","Panama City, FL","Pascagoula, MS","Pembina, ND","Pensacola, FL","Philadelphia, PA","Phoenix, AZ","Piegan, MT","Pinecreek, MN","Pittsburgh, PA","Plattsburg, NY","Point Roberts, WA","Port Angeles, WA","Port Arthur/Beaumont, TX","Port Canaveral, FL","Port Everglades, FL","Port Hueneme, CA","Port Huron, MI","Port Townsend, WA","Portal, ND","Porthill, ID","Portland, ME","Portland, OR","Portsmouth, NH","Presidio, TX","Progreso, TX","Providence, RI","Raymond, MT","Reno, NV","Richford, VT","Richmond-Petersburg, VA","Rio Grande City, TX","Rochester, NY","Roma, TX","Roosville, MT","Roseau, MN","Sacramento, CA","Saginaw-Bay City-Flint, MI","Salt Lake City, UT","San Antonio, TX","San Diego, CA","San Francisco, CA","San Juan, PR","San Luis, AZ","San Ysidro, CA","Sanford, FL","Santa Teresa, NM","Sarasota-Bradenton, FL","Sarles, ND","Sasabe, AZ","Sault Sainte Marie, MI","Savannah, GA","Scobey, MT","Seattle, WA","Sherwood, ND","Shreveport, LA","Sioux Falls, SD","Skagway, AK","South Bend, IN","Spokane, WA","St. Augustine, FL","St. John, ND","St. Louis, MO","St. Petersburg, FL","Sumas, WA","Sweetgrass, MT","Syracuse, NY","Tampa, FL","Tecate, CA","Toledo-Sandusky, OH","Tornillo-Guadalupe, TX","Trenton-Merced, NJ","Tri Cities, TN","Trout River-Chateau-Covington, NY","Tucson, AZ","Tulsa, OK","Turner, MT","Van Buren, ME","Vanceboro, ME","Vicksburg, MS","Walhalla, ND","Warroad, MN","West Palm Beach, FL","Westhope, ND","Whitlash, MT","Wichita, KS","Wild Horse, MT","Wilkes Barre-Scranton, PA","Williston, PA","Willow Creek, MT","Wilmington, NC","Winston-Salem, NC","Worchester, MA"};
    private ArrayList<Boolean> currentCheckList = new ArrayList<>();
    private ArrayList<Boolean> desireCheckList = new ArrayList<>();
    private int currentportSelIndex;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_profile, container, false);

        initComponent(root);
        initData();
        loadProfileData();
        onClickRankBtn();
        onClickAgencyBtn();
        onClickOfficeBtn();
        onClickCurrentPortBtn();
        onClickDesirePortBtn();
        onClickSaveBtn(root);
        onClickLogoutBtn();
        return root;
    }

    private void initComponent(View root) {
        iv_profile = root.findViewById(R.id.iv_profile);
        iv_logout_btn = root.findViewById(R.id.iv_logout_btn);
        tv_username = root.findViewById(R.id.tv_username);
        tv_save_btn = root.findViewById(R.id.tv_save_btn);
        tv_rank = root.findViewById(R.id.tv_rank);
        tv_agency = root.findViewById(R.id.tv_agency);
        tv_office = root.findViewById(R.id.tv_office);
        tv_currentport = root.findViewById(R.id.tv_currentport);
        tv_desiredport = root.findViewById(R.id.tv_desiredport);
        loadingIndicator = LoadingIndicator.getInstance();
    }

    private void initData() {
        for (int i = 0; i < portList.length; i++) {
            currentCheckList.add(false);
            desireCheckList.add(false);
        }
    }

    private void loadProfileData() {
        loadingIndicator.showProgress(getContext());
        SharedPreferences idPref = getActivity().getSharedPreferences(PREF_ID, Context.MODE_PRIVATE);
        String id = idPref.getString("Id", "");

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        RequestBody requestId = RequestBody.create(MediaType.parse("multipart/form-data"), id);

        Call<JsonObject> call = apiInterface.myprofile(requestId);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                String response_body = response.body().toString();

                try {
                    JSONObject dataObject = new JSONObject(response_body);
                    Boolean isSuccess = dataObject.getBoolean("success");
                    if (isSuccess) {
                        String username = dataObject.getString("username");
                        String rank = dataObject.getString("rank");
                        String agency = dataObject.getString("agency");
                        String office = dataObject.getString("office");
                        String currentport = dataObject.getString("current_port");
                        String desireport = dataObject.getString("desire_port");
                        tv_username.setText(username);
                        tv_rank.setTextColor(Color.BLACK);
                        tv_rank.setText(rank);
                        tv_agency.setTextColor(Color.BLACK);
                        tv_agency.setText(agency);
                        tv_office.setTextColor(Color.BLACK);
                        tv_office.setText(office);
                        tv_currentport.setTextColor(Color.BLACK);
                        tv_currentport.setText(currentport);
                        tv_desiredport.setTextColor(Color.BLACK);
                        tv_desiredport.setText(desireport);
                    } else {
                        String msg = dataObject.getString("message");
                        Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
                    }
                } catch(JSONException e) {
                    e.printStackTrace();
                }
                loadingIndicator.hideProgress();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(getContext(), t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                loadingIndicator.hideProgress();
            }
        });
    }

    private void onClickRankBtn() {
        tv_rank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String[] ranks = {"GS-1","GS-2","GS-3","GS-4","GS-5","GS-6","GS-7","GS-8","GS-9","GS-10","GS-11","GS-12","GS-13","GS-14","GS-15","Other"};

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Select Rank");
                builder.setItems(ranks, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        tv_rank.setTextColor(Color.BLACK);
                        tv_rank.setText(ranks[which]);
                    }
                });
                builder.show();
            }
        });
    }

    private void onClickAgencyBtn() {
        tv_agency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String[] agencies = {"CBP"};

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Select Agency");
                builder.setItems(agencies, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        tv_agency.setTextColor(Color.BLACK);
                        tv_agency.setText(agencies[which]);
                    }
                });
                builder.show();
            }
        });
    }

    private void onClickOfficeBtn() {
        tv_office.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String[] offices = {"OFO","BP"};

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Select Office");
                builder.setItems(offices, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        tv_office.setTextColor(Color.BLACK);
                        tv_office.setText(offices[which]);
                    }
                });
                builder.show();
            }
        });
    }

    private void onClickCurrentPortBtn() {
        tv_currentport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater factory = LayoutInflater.from(getActivity());
                final View portDialogView = factory.inflate(R.layout.portselectlayout, null);
                final AlertDialog portDialog = new AlertDialog.Builder(getActivity()).create();
                portDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                portDialog.setView(portDialogView);

                TextView title = portDialogView.findViewById(R.id.tv_port_text);
                title.setText("Select Current Port");

                portRecyclerView = portDialogView.findViewById(R.id.rv_port);
                portAdapter = new PortAdapter(getActivity(), portList, currentCheckList, true, false, ProfileFragment.this);
                portRecyclerView.setAdapter(portAdapter);
                portRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

                portDialogView.findViewById(R.id.tv_save_btn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        tv_currentport.setText(portList[currentportSelIndex]);
                        tv_currentport.setTextColor(Color.BLACK);
                        portDialog.dismiss();
                    }
                });
                portDialog.show();
            }
        });
    }

    private void onClickDesirePortBtn() {
        tv_desiredport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater factory = LayoutInflater.from(getContext());
                final View portDialogView = factory.inflate(R.layout.portselectlayout, null);
                final AlertDialog portDialog = new AlertDialog.Builder(getContext()).create();
                portDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                portDialog.setView(portDialogView);

                TextView title = portDialogView.findViewById(R.id.tv_port_text);
                title.setText("Select Desired Port");

                portRecyclerView = portDialogView.findViewById(R.id.rv_port);
                portAdapter = new PortAdapter(getContext(), portList, desireCheckList, false, false, ProfileFragment.this);
                portRecyclerView.setAdapter(portAdapter);
                portRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

                portDialogView.findViewById(R.id.tv_save_btn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String desireStr = "";
                        for (int i = 0; i < desireCheckList.size(); i++) {
                            if (desireCheckList.get(i)) {
                                desireStr += portList[i] + ", ";
                            }
                        }
                        String newStr = desireStr.substring(0, desireStr.length() - 2);
                        tv_desiredport.setTextColor(Color.BLACK);
                        tv_desiredport.setText(newStr);
                        portDialog.dismiss();
                    }
                });
                portDialog.show();
            }
        });
    }

    private void onClickSaveBtn(View root) {
        tv_save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadingIndicator.showProgress(getContext());
                SharedPreferences idPref = getActivity().getSharedPreferences(PREF_ID, Context.MODE_PRIVATE);
                String id = idPref.getString("Id", "");
                String rank = tv_rank.getText().toString();
                String agency = tv_agency.getText().toString();
                String currentport = tv_currentport.getText().toString();
                String desireport = tv_desiredport.getText().toString();
                String office = tv_office.getText().toString();

                ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
                RequestBody requestId = RequestBody.create(MediaType.parse("multipart/form-data"), id);
                RequestBody requestFName = RequestBody.create(MediaType.parse("multipart/form-data"), "");
                RequestBody requestLName = RequestBody.create(MediaType.parse("multipart/form-data"), "");
                RequestBody requestImage = RequestBody.create(MediaType.parse("multipart/form-data"), "");
                RequestBody requestRank = RequestBody.create(MediaType.parse("multipart/form-data"), rank);
                RequestBody requestAgency = RequestBody.create(MediaType.parse("multipart/form-data"), agency);
                RequestBody requestCurrentPort = RequestBody.create(MediaType.parse("multipart/form-data"), currentport);
                RequestBody requestDesirePort = RequestBody.create(MediaType.parse("multipart/form-data"), desireport);
                RequestBody requestOffice = RequestBody.create(MediaType.parse("multipart/form-data"), office);

                Call<JsonObject> call = apiInterface.updateprofile(requestId, requestFName, requestLName, requestImage, requestRank, requestAgency,
                        requestCurrentPort, requestDesirePort, requestOffice);
                call.enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        String response_body = response.body().toString();

                        try {
                            JSONObject dataObject = new JSONObject(response_body);
                            String msg = dataObject.getString("message");
                            Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        loadingIndicator.hideProgress();
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        Toast.makeText(getContext(), t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                        loadingIndicator.hideProgress();
                    }
                });
            }
        });
    }

    private void onClickLogoutBtn() {
        iv_logout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder pictureDialog = new AlertDialog.Builder(getContext());
                pictureDialog.setMessage("Are you sure you want to logout?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                doLogout();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        }).show();
            }
        });
    }

    private void doLogout() {
        SharedPreferences idPref = getContext().getSharedPreferences(PREF_ID, Context.MODE_PRIVATE);
        idPref.edit().remove("Id").commit();

        SharedPreferences usernamePref = getContext().getSharedPreferences(PREF_USERNAME, Context.MODE_PRIVATE);
        usernamePref.edit().remove("Username").commit();

        SharedPreferences emailPref = getContext().getSharedPreferences(PREF_EMAIL, Context.MODE_PRIVATE);
        emailPref.edit().remove("Email").commit();

        SharedPreferences rankPref = getContext().getSharedPreferences(PREF_RANK, Context.MODE_PRIVATE);
        rankPref.edit().remove("Rank").commit();

        SharedPreferences agencyPref = getContext().getSharedPreferences(PREF_AGENCY, Context.MODE_PRIVATE);
        agencyPref.edit().remove("Agency").commit();

        SharedPreferences officePref = getContext().getSharedPreferences(PREF_OFFICE, Context.MODE_PRIVATE);
        officePref.edit().remove("Office").commit();

        SharedPreferences currentportPref = getContext().getSharedPreferences(PREF_CURRENTPORT, Context.MODE_PRIVATE);
        currentportPref.edit().remove("CurrentPort").commit();

        SharedPreferences desireportPref = getContext().getSharedPreferences(PREF_DESIREPORT, Context.MODE_PRIVATE);
        desireportPref.edit().remove("DesirePort").commit();

        SharedPreferences ftokenPref = getContext().getSharedPreferences(PREF_FTOKEN, Context.MODE_PRIVATE);
        ftokenPref.edit().remove("FToken").commit();

        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

    public void onClickCurrentPort(int position) {
        for (int i = 0; i < currentCheckList.size(); i++) {
            currentCheckList.set(i, false);
        }
        currentCheckList.set(position, true);
        currentportSelIndex = position;
        portAdapter.notifyDataSetChanged();
    }

    public void onClickDesirePort(int position) {
        if (desireCheckList.get(position)) {
            desireCheckList.set(position, false);
        } else {
            desireCheckList.set(position, true);
        }
        portAdapter.notifyDataSetChanged();
    }
}