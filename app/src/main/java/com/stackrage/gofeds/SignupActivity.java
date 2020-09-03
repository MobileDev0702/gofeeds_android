package com.stackrage.gofeds;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.JsonObject;
import com.stackrage.gofeds.api.ApiClient;
import com.stackrage.gofeds.api.ApiInterface;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignupActivity extends AppCompatActivity {

    public static final String PREF_ID = "PREFERENCE_ID";
    public static final String PREF_USERNAME = "PREFERENCE_USERNAME";
    public static final String PREF_EMAIL = "PREFERENCE_EMAIL";
    public static final String PREF_RANK = "PREFERENCE_RANK";
    public static final String PREF_AGENCY = "PREFERENCE_AGENCY";
    public static final String PREF_OFFICE = "PREFERENCE_OFFICE";
    public static final String PREF_CURRENTPORT = "PREFERENCE_CURRENTPORT";
    public static final String PREF_DESIREPORT = "PREFERENCE_DESIREPORT";
    public static final String PREF_FTOKEN = "PREFERENCE_FTOKEN";

    private ImageView iv_back_btn;
    private EditText et_username, et_pwd, et_email;
    private TextView tv_currentport, tv_desiredport, tv_rank, tv_agency, tv_office, tv_submit_btn;
    private RecyclerView portRecyclerView;

    private LoadingIndicator loadingIndicator;
    private PortAdapter portAdapter;

    private String[] portList = {"Albany, NY","Albuquerque, NM","Alexandria Bay, NY","Ambrose, ND","Anacortes, WA","Andrade, CA","Antler, ND","Appleton International Airport, WI","Ashtabula/Conneaut, OH","Astoria, OR","Atlanta, GA","Atlantic City User Fee Airport, NJ","Austin, TX","Baltimore, MD","Bangor, ME","Austin, TX","Baltimore, MD","Bangor, ME","Baton Rouge, LA","Battle Creek, MI","Baudette, MN","Beaufort-Morehead, NC","Beecher Falls, VT","Bellingham, WA","Binghamton Regional Airport, NY","Birmingham, AL","Blaine, WA","Blue Grass Airport, KY","Boise, ID","Bozeman Yellowstone, MT","Bridgewater, ME","Brownsville, TX","Brunswick, GA","Buffalo, NY","Calais, ME","Calexico, CA","Carbury, ND","Centennial Airport, Englewood, CO","Champlain, NY","Charleston, SC","Charlotte Amalie, USVI","Charlotte, NC","Chattanooga, TN","Chester PA/Wilmington, DE","Chicago, IL","Cincinnati, OH-Lawrenceburg, IN","Cleveland, OH","Saipan, CNMI","Cobb County Airport, GA","Columbia, SC","Columbus, NM","Columbus, OH","Corpus Christi, TX","Cristiansted, VI","Dallas/Fort Worth, TX","Dalton Cache, AK","Danville, WA","Dayton, OH","Daytona Beach, FL","Del Bonita, MT","Del Rio, TX","Denver, CO","Derby Line, VT","Detroit, MI","Detroit (Airport), MI","Douglas, AZ","Dulles Airport, VA","Duluth, MN","Dunseith, ND","Durham, NC","Eagle County, CO","Eagle Pass, TX","Eastport, ID","Eastport, ME","El Paso, TX","Erie, PA","Eureka, CA","Fairbanks, AK","Fajardo Culebra, PR","Fajardo Vieques, PR","Fargo, ND","Fernandina, FL","Fort Fairfield, ME","Fort Kent, ME","Fort Lauderdale, FL","Fort Myers, FL","Fortuna, ND","Freeport, TX","Fresno, CA","Friday Harbor, WA","Frontier, WA","Galveston, TX","Gramercy, LA","Grand Forks, ND","Grand Portage, MN","Grand Rapids, MI","Grant County, WA","Great Falls, MT","Green Bay, WI","Greenville-Spartanburg, SC","Griffiss, NY","Guam, GU","Gulfport, MS","Hannah, ND","Hansboro, ND","Harrisburg, PA","Hartford, CT","Helena, MT","Hidalgo/Pharr, TX","Highgate Springs, VT","Hillsboro, OR","Honolulu, HI","Houlton, ME","Houston (Airport), TX","Houston (Seaport), TX","Huntsville, AL","Indianapolis, IN","International Falls-Rainer, MN","Jackman-Cobum Gore, ME","Jackman-Jackman, ME","Jacksonville, FL","Jefferson City, CO","JFK, NY","Juneau, AK","Kahului, HI","Kalispell, MT","Kansas City, MO","Ketchikan, AK","Key West, FL","Knoxville, TN","Kona-Hilo-Hilo, HI","Kona-Hilo-Kailua Kona, HI","Long Beach, CA","Lake Charles, LA","Lancaster, MN","Laredo, TX","Las Vegas, NV","Laurier, WA","LAX, CA","Leesburg, FL","Lehigh Valley, PA","Little Rock, AR","Logan, MA","Longview, WA","Louisville, KY","Lukeville, AZ","Lynden, WA","Madawaska, ME","Maguire, NJ","Maida, ND","Manatee, FL","Manchester User Fee, NH","Marathon, FL","Massena, NY","Melbourne, FL","Memphis, TN","Metaline Falls, WA","Miami Seaport, FL","Miami Airport, FL","Mid America, IL","Midland, TX","Milwaukee, WI","Minneapolis, MN","Minot, ND","Mobile, AL","Morgan City, LA","Morgan, MT","Naco, AZ","Naples, FL","Nashville, TN","Neche, ND","New Bedford, MA","New Haven, CT","New Orleans, LA","Newark, NJ","Newport, OR","Nogales, AZ","Noonan, ND","Norfolk, VA","Northgate, ND","Norton, VT","Ogdensburg, NY","Oklahoma City, OK","Omaha, NE","Ontario, CA","Opheim, MT","Orlando Executive, FL","Orlando, FL","Oroville, WA","Otay Mesa, CA","Panama City, FL","Pascagoula, MS","Pembina, ND","Pensacola, FL","Philadelphia, PA","Phoenix, AZ","Piegan, MT","Pinecreek, MN","Pittsburgh, PA","Plattsburg, NY","Point Roberts, WA","Port Angeles, WA","Port Arthur/Beaumont, TX","Port Canaveral, FL","Port Everglades, FL","Port Hueneme, CA","Port Huron, MI","Port Townsend, WA","Portal, ND","Porthill, ID","Portland, ME","Portland, OR","Portsmouth, NH","Presidio, TX","Progreso, TX","Providence, RI","Raymond, MT","Reno, NV","Richford, VT","Richmond-Petersburg, VA","Rio Grande City, TX","Rochester, NY","Roma, TX","Roosville, MT","Roseau, MN","Sacramento, CA","Saginaw-Bay City-Flint, MI","Salt Lake City, UT","San Antonio, TX","San Diego, CA","San Francisco, CA","San Juan, PR","San Luis, AZ","San Ysidro, CA","Sanford, FL","Santa Teresa, NM","Sarasota-Bradenton, FL","Sarles, ND","Sasabe, AZ","Sault Sainte Marie, MI","Savannah, GA","Scobey, MT","Seattle, WA","Sherwood, ND","Shreveport, LA","Sioux Falls, SD","Skagway, AK","South Bend, IN","Spokane, WA","St. Augustine, FL","St. John, ND","St. Louis, MO","St. Petersburg, FL","Sumas, WA","Sweetgrass, MT","Syracuse, NY","Tampa, FL","Tecate, CA","Toledo-Sandusky, OH","Tornillo-Guadalupe, TX","Trenton-Merced, NJ","Tri Cities, TN","Trout River-Chateau-Covington, NY","Tucson, AZ","Tulsa, OK","Turner, MT","Van Buren, ME","Vanceboro, ME","Vicksburg, MS","Walhalla, ND","Warroad, MN","West Palm Beach, FL","Westhope, ND","Whitlash, MT","Wichita, KS","Wild Horse, MT","Wilkes Barre-Scranton, PA","Williston, PA","Willow Creek, MT","Wilmington, NC","Winston-Salem, NC","Worchester, MA"};
    private ArrayList<Boolean> currentCheckList = new ArrayList<>();
    private ArrayList<Boolean> desireCheckList = new ArrayList<>();
    private int currentportSelIndex;

    private FirebaseAuth mAuth;
    private DatabaseReference dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        initComponent();
        initFirebase();
        initData();
        onClickBackBtn();
        onClickCurrentPortBtn();
        onClickDesirePortBtn();
        onClickRankBtn();
        onClickAgencyBtn();
        onClickOfficeBtn();
        onClickSubmitBtn();
    }

    private void initComponent() {
        iv_back_btn = findViewById(R.id.iv_back_btn);
        et_username = findViewById(R.id.et_username);
        et_pwd = findViewById(R.id.et_pwd);
        et_email = findViewById(R.id.et_email);
        tv_currentport = findViewById(R.id.tv_currentport);
        tv_desiredport = findViewById(R.id.tv_desiredport);
        tv_rank = findViewById(R.id.tv_rank);
        tv_agency = findViewById(R.id.tv_agency);
        tv_office = findViewById(R.id.tv_office);
        tv_submit_btn = findViewById(R.id.tv_submit_btn);
        loadingIndicator = LoadingIndicator.getInstance();
    }

    private void initFirebase() {
        mAuth = FirebaseAuth.getInstance();
        dbRef = FirebaseDatabase.getInstance().getReference();
    }

    private void initData() {
        for (int i = 0; i < portList.length; i++) {
            currentCheckList.add(false);
            desireCheckList.add(false);
        }
    }

    private void onClickBackBtn() {
        iv_back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void onClickCurrentPortBtn() {
        tv_currentport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater factory = LayoutInflater.from(SignupActivity.this);
                final View portDialogView = factory.inflate(R.layout.portselectlayout, null);
                final AlertDialog portDialog = new AlertDialog.Builder(SignupActivity.this).create();
                portDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                portDialog.setView(portDialogView);

                TextView title = portDialogView.findViewById(R.id.tv_port_text);
                title.setText("Select Current Port");

                portRecyclerView = portDialogView.findViewById(R.id.rv_port);
                portAdapter = new PortAdapter(SignupActivity.this, portList, currentCheckList, true, true, null);
                portRecyclerView.setAdapter(portAdapter);
                portRecyclerView.setLayoutManager(new LinearLayoutManager(SignupActivity.this, LinearLayoutManager.VERTICAL, false));

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
                LayoutInflater factory = LayoutInflater.from(SignupActivity.this);
                final View portDialogView = factory.inflate(R.layout.portselectlayout, null);
                final AlertDialog portDialog = new AlertDialog.Builder(SignupActivity.this).create();
                portDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                portDialog.setView(portDialogView);

                TextView title = portDialogView.findViewById(R.id.tv_port_text);
                title.setText("Select Desired Port");

                portRecyclerView = portDialogView.findViewById(R.id.rv_port);
                portAdapter = new PortAdapter(SignupActivity.this, portList, desireCheckList, false, true, null);
                portRecyclerView.setAdapter(portAdapter);
                portRecyclerView.setLayoutManager(new LinearLayoutManager(SignupActivity.this, LinearLayoutManager.VERTICAL, false));

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

    private void onClickRankBtn() {
        tv_rank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String[] ranks = {"GS-1","GS-2","GS-3","GS-4","GS-5","GS-6","GS-7","GS-8","GS-9","GS-10","GS-11","GS-12","GS-13","GS-14","GS-15","Other"};

                AlertDialog.Builder builder = new AlertDialog.Builder(SignupActivity.this);
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

                AlertDialog.Builder builder = new AlertDialog.Builder(SignupActivity.this);
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

                AlertDialog.Builder builder = new AlertDialog.Builder(SignupActivity.this);
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

    private void onClickSubmitBtn() {
        tv_submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadingIndicator.showProgress(SignupActivity.this);
                final String username = et_username.getText().toString();
                final String pwd = et_pwd.getText().toString();
                final String email = et_email.getText().toString();
                final String currentport = tv_currentport.getText().toString();
                final String desireport = tv_desiredport.getText().toString();
                final String rank = tv_rank.getText().toString();
                final String agency = tv_agency.getText().toString();
                final String office = tv_office.getText().toString();
                String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

                if (username.isEmpty()) {
                    Toast.makeText(SignupActivity.this, "Please Input Username!", Toast.LENGTH_LONG).show();
                    loadingIndicator.hideProgress();
                    return;
                }
                if (pwd.isEmpty()) {
                    Toast.makeText(SignupActivity.this, "Please Input Password!", Toast.LENGTH_LONG).show();
                    loadingIndicator.hideProgress();
                    return;
                }
                if (!email.matches(emailPattern)) {
                    Toast.makeText(SignupActivity.this, "Invalid Email Address!", Toast.LENGTH_LONG).show();
                    loadingIndicator.hideProgress();
                    return;
                }
                if (email.isEmpty()) {
                    Toast.makeText(SignupActivity.this, "Invalid Email Address!", Toast.LENGTH_LONG).show();
                    loadingIndicator.hideProgress();
                    return;
                }
                if (rank.isEmpty()) {
                    Toast.makeText(SignupActivity.this, "Please Input Rank!", Toast.LENGTH_LONG).show();
                    loadingIndicator.hideProgress();
                    return;
                }
                if (agency.isEmpty()) {
                    Toast.makeText(SignupActivity.this, "Please Input Agency!", Toast.LENGTH_LONG).show();
                    loadingIndicator.hideProgress();
                    return;
                }
                if (office.isEmpty()) {
                    Toast.makeText(SignupActivity.this, "Please Input Office!", Toast.LENGTH_LONG).show();
                    loadingIndicator.hideProgress();
                    return;
                }
                if (currentport.isEmpty()) {
                    Toast.makeText(SignupActivity.this, "Please Input Current Port!", Toast.LENGTH_LONG).show();
                    loadingIndicator.hideProgress();
                    return;
                }
                if (desireport.isEmpty()) {
                    Toast.makeText(SignupActivity.this, "Please Input Desired Port!", Toast.LENGTH_LONG).show();
                    loadingIndicator.hideProgress();
                    return;
                }

                mAuth.createUserWithEmailAndPassword(email, pwd).addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(SignupActivity.this, "Sign up Failed: " + task.getException().toString(), Toast.LENGTH_LONG).show();
                        } else {
                            Map<String, Object> result = new HashMap<>();
                            result.put("email", email);
                            result.put("name", username);

                            dbRef.child("users").child(mAuth.getCurrentUser().getUid()).updateChildren(result);

                            ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
                            RequestBody requestEmail = RequestBody.create(MediaType.parse("multipart/form-data"), email);
                            RequestBody requestUsername = RequestBody.create(MediaType.parse("multipart/form-data"), username);
                            RequestBody requestFName = RequestBody.create(MediaType.parse("multipart/form-data"), "hello");
                            RequestBody requestLName = RequestBody.create(MediaType.parse("multipart/form-data"), "hello");
                            RequestBody requestPwd = RequestBody.create(MediaType.parse("multipart/form-data"), pwd);
                            RequestBody requestRank = RequestBody.create(MediaType.parse("multipart/form-data"), rank);
                            RequestBody requestAgency = RequestBody.create(MediaType.parse("multipart/form-data"), agency);
                            RequestBody requestCurrentPort = RequestBody.create(MediaType.parse("multipart/form-data"), currentport);
                            RequestBody requestDesirePort = RequestBody.create(MediaType.parse("multipart/form-data"), desireport);
                            RequestBody requestOffice = RequestBody.create(MediaType.parse("multipart/form-data"), office);
                            RequestBody requestFToken = RequestBody.create(MediaType.parse("multipart/form-data"), "test");
                            RequestBody requestDeviceId = RequestBody.create(MediaType.parse("multipart/form-data"), "test");

                            Call<JsonObject> call = apiInterface.register(requestEmail, requestUsername, requestFName, requestLName, requestPwd, requestRank, requestAgency,
                                    requestCurrentPort, requestDesirePort, requestOffice, requestFToken, requestDeviceId);

                            call.enqueue(new Callback<JsonObject>() {
                                @Override
                                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                                    String response_body = response.body().toString();

                                    try {
                                        JSONObject dataObject = new JSONObject(response_body);
                                        Boolean isSuccess = dataObject.getBoolean("success");
                                        String msg = dataObject.getString("message");
                                        Toast.makeText(SignupActivity.this, msg, Toast.LENGTH_LONG).show();
                                        if (isSuccess) {
                                            String id = dataObject.getString("id");
                                            SharedPreferences idPref = getSharedPreferences(PREF_ID, Context.MODE_PRIVATE);
                                            idPref.edit().putString("Id", id).commit();

                                            String username = dataObject.getString("username");
                                            SharedPreferences usernamePref = getSharedPreferences(PREF_USERNAME, Context.MODE_PRIVATE);
                                            usernamePref.edit().putString("Username", username).commit();

                                            String email = dataObject.getString("email");
                                            SharedPreferences emailPref = getSharedPreferences(PREF_EMAIL, Context.MODE_PRIVATE);
                                            emailPref.edit().putString("Email", email).commit();

                                            String rank = dataObject.getString("rank");
                                            SharedPreferences rankPref = getSharedPreferences(PREF_RANK, Context.MODE_PRIVATE);
                                            rankPref.edit().putString("Rank", rank).commit();

                                            String agency = dataObject.getString("agency");
                                            SharedPreferences agencyPref = getSharedPreferences(PREF_AGENCY, Context.MODE_PRIVATE);
                                            agencyPref.edit().putString("Agency", agency).commit();

                                            String office = dataObject.getString("office");
                                            SharedPreferences officePref = getSharedPreferences(PREF_OFFICE, Context.MODE_PRIVATE);
                                            officePref.edit().putString("Office", office).commit();

                                            String currentport = dataObject.getString("current_port");
                                            SharedPreferences currentportPref = getSharedPreferences(PREF_CURRENTPORT, Context.MODE_PRIVATE);
                                            currentportPref.edit().putString("CurrentPort", currentport).commit();

                                            String desireport = dataObject.getString("desire_port");
                                            SharedPreferences desireportPref = getSharedPreferences(PREF_DESIREPORT, Context.MODE_PRIVATE);
                                            desireportPref.edit().putString("DesirePort", desireport).commit();

                                            String ftoken = dataObject.getString("ftoken");
                                            SharedPreferences ftokenPref = getSharedPreferences(PREF_FTOKEN, Context.MODE_PRIVATE);
                                            ftokenPref.edit().putString("FToken", ftoken).commit();

                                            Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    loadingIndicator.hideProgress();
                                }

                                @Override
                                public void onFailure(Call<JsonObject> call, Throwable t) {
                                    Toast.makeText(SignupActivity.this, t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                                    loadingIndicator.hideProgress();
                                }
                            });
                        }
                    }
                });
            }
        });
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