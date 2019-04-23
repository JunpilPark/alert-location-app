package com.toyproject.honeyimleaving.fragment;

import android.content.DialogInterface;
import android.content.IntentSender;
import android.graphics.Color;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.toyproject.honeyimleaving.HoneyImLeaving;
import com.toyproject.honeyimleaving.R;
import com.toyproject.honeyimleaving.custom.CustomDialogSearchedPlace;
import com.toyproject.honeyimleaving.model.PlaceAlert;
import com.toyproject.honeyimleaving.retrofit.model.Candidates;
import com.toyproject.honeyimleaving.retrofit.model.ResponsePlace;
import com.toyproject.honeyimleaving.myutil.Dlog;
import com.toyproject.honeyimleaving.retrofit.MyGooglePlaceAPI;
import com.toyproject.honeyimleaving.retrofit.MyRetrofit;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlaceFragment extends Fragment implements OnMapReadyCallback, FragmentReturnInterface<PlaceAlert> {

    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;
    private static final int REQUEST_CHECK_SETTINGS = 0x1;

    private MapView mMapView;

    private EditText mEditPlaceAlias;
    private EditText mEditSearchLocation;
    private ImageButton mBtnSearchLocation;
    private ImageButton mBtnMoveCurrenLocation;

    private FusedLocationProviderClient mFusedLocationClient;
    private SettingsClient mSettingsClient;
    private LocationRequest mLocationRequest;
    private LocationSettingsRequest mLocationSettingsRequest;

    private GoogleMap mGoogleMap;

    private PlaceAlert mParam;
    private Location mSelectedLocation;
    private CustomDialogSearchedPlace mDialogSearchedPlace;
    private String mErrMessage;
    public PlaceFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Dlog.d("프레그먼트의 onCreate 호출");
        mParam = null;
        if (getArguments() != null) {
            mParam = (PlaceAlert) getArguments().getSerializable("placeAlert");
            Dlog.d("프레그먼트의 onCreate 호출 - 파라메터있음");
        }
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());
        mSettingsClient = LocationServices.getSettingsClient(getContext());

        createLocationRequest();
        buildLocationSettingsRequest();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View layout = inflater.inflate(R.layout.frag_place_alert, container, false);

        mMapView = layout.findViewById(R.id.mapView);
        mMapView.getMapAsync(this);

        mBtnSearchLocation = layout.findViewById(R.id.btn_search_location);
        mBtnMoveCurrenLocation = layout.findViewById(R.id.btn_current_location);
        mEditSearchLocation = layout.findViewById(R.id.edit_search_location);
        mEditPlaceAlias = layout.findViewById(R.id.edit_place_alias);
        initEditBox();

        mBtnMoveCurrenLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCurrentLocation();
            }
        });

        mBtnSearchLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String locationName = mEditSearchLocation.getText().toString();
                if(locationName.isEmpty()) {
                    return;
                }

                final Call<ResponsePlace> request = MyRetrofit.getInstance().getSetviceGooglePlace().getPlaceFromAddress(locationName,
                        MyGooglePlaceAPI.FIELDS, MyGooglePlaceAPI.INPUTTYPE, MyGooglePlaceAPI.LANGUAGE, getString(R.string.google_place_api_key));

                request.enqueue(new Callback<ResponsePlace>() {
                    @Override
                    public void onResponse(Call<ResponsePlace> call, Response<ResponsePlace> response) {
                        final List<Candidates> results =  response.body().getCandidates();

                        if (results.size() == 0) {
                            showAlertDialog(getString(R.string.common_alert_title),getString(R.string.alert_error_no_place_result), null, getString(R.string.common_ok));
                        }
                        else {
                            if (mDialogSearchedPlace == null) {
                                mDialogSearchedPlace = new CustomDialogSearchedPlace(getContext(), new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        setSelectedLocation(mDialogSearchedPlace.getItem(position).getLatitude(), mDialogSearchedPlace.getItem(position).getLongitude());
                                        moveGoogleMapCarmeraAtLocation(mSelectedLocation, mDialogSearchedPlace.getItem(position).getPlaceName());
                                        if(mEditPlaceAlias.getText().length() == 0) {
                                            mEditPlaceAlias.setText(mDialogSearchedPlace.getItem(position).getPlaceName());
                                        }
                                        mDialogSearchedPlace.dismiss();
                                    }
                                });
                            }
                            mDialogSearchedPlace.show(results);
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponsePlace> call, Throwable t) {
                        Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

            }
        });

        return layout;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;

        mGoogleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                selectPlaceFromLongClickInMap(latLng);
            }
        });
        initMovePosition();
        Dlog.d("onMapReady 호출");
    }

    // 최초 위치 초기화 : 파라메터 있을 경우 - 파라메터로, 없을 경우 현재 위치로
    private void initMovePosition() {
        if(mParam != null) {
            setSelectedLocation(mParam.getLatitude(), mParam.getLongitude());
            moveGoogleMapCarmeraAtLocation(mSelectedLocation, mParam.getPlaceName());
            Dlog.d("onMapReady 호출 : 파라메터 있음.");
        }
        else {
            getCurrentLocation();
            Dlog.d("onMapReady 호출 : 파라메터 없음. 현재 위치 호출");
        }
    }

    private void initEditBox() {
        if (mEditSearchLocation != null) {
            mEditSearchLocation.setText(null);
        }

        if (mEditPlaceAlias != null) {
            if (mParam != null) {
                mEditPlaceAlias.setText(mParam.getPlaceName());
            } else {
                mEditPlaceAlias.setText(null);
            }
        }
    }

    private void moveGoogleMapCarmeraAtLocation(Location location, String title) {
        if(mGoogleMap == null) {
            Dlog.d("mGoogleMap이 NULL");
            return;
        }
        mGoogleMap.clear();
        mSelectedLocation = location;
        LatLng moveTargetLatLng = new LatLng(mSelectedLocation.getLatitude(), mSelectedLocation.getLongitude());
        drawMarker(moveTargetLatLng, title);
        drawCycleRadius(moveTargetLatLng,"#556799FF");
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(moveTargetLatLng, 15));
    }

    private void drawCycleRadius(LatLng latLng, String colorString) {
        CircleOptions circleOptions = new CircleOptions();
        circleOptions.strokeColor(Color.BLUE);
        circleOptions.fillColor(Color.parseColor(colorString == null ? "#556799FF" :colorString));
        circleOptions.radius(HoneyImLeaving.PLACE_RADIUS); // 단위 : m
        circleOptions.strokeWidth(2f);
        circleOptions.center(latLng);

        if(mGoogleMap != null)
             mGoogleMap.addCircle(circleOptions);
    }

    private void drawMarker(LatLng latLng, String title) {
        MarkerOptions makerOptions = new MarkerOptions();
        makerOptions.position(latLng);
        if(title != null && title.length() > 0) {
            makerOptions.title(title);
        }
        if(mGoogleMap != null)
            mGoogleMap.addMarker(makerOptions).showInfoWindow();
    }

    private void selectPlaceFromLongClickInMap(LatLng latLng) {
        if(mGoogleMap == null) return;
        mGoogleMap.clear();
        setSelectedLocation(latLng.latitude, latLng.longitude);
        drawCycleRadius(latLng, null);
        drawMarker(latLng, "선택한 위치");
    }

    // 현재 위치를 조회하여, 카메라 이동까지 함.
    // 퍼미션은 메인 진입 시에 확인함.
    @SuppressWarnings("MissingPermission")
    private void getCurrentLocation() {
        // GPS 및 네트워크가 켜져 있는 지 확인
        mSettingsClient.checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        mFusedLocationClient.getLastLocation()
                                .addOnCompleteListener(getActivity(), new OnCompleteListener<Location>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Location> task) {
                                        if (task.isSuccessful() && task.getResult() != null) {
                                            moveGoogleMapCarmeraAtLocation(task.getResult(), getString(R.string.commont_current_position));
                                        } else {
                                            Dlog.w("getLastLocation:exception" + task.getException());
                                        }
                                    }
                                });

            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                int statusCode = ((ApiException) e).getStatusCode();
                switch (statusCode) {
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            ResolvableApiException rae = (ResolvableApiException) e;
                            rae.startResolutionForResult(getActivity(), REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException sie) {
                            Toast.makeText(getContext(), sie.getMessage(), Toast.LENGTH_LONG).show();
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            String errorMessage = "Location settings are inadequate, and cannot be " +
                                        "fixed here. Fix in Settings.";
                        Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();
                    }
                }
          });;

    }

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS); // 위치가 업데이트 되는 주기
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS); // 위치 획득 후 업데이트 되는 주기
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    }

    private void buildLocationSettingsRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mMapView != null)
            mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if(mMapView != null)
            mMapView.onLowMemory();
    }

    @Override
    public void onStart() {
        super.onStart();

        if(mMapView != null)
            mMapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();

        if(mMapView != null)
            mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        if(mMapView != null)
            mMapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        if(mMapView != null)
            mMapView.onStop();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(mMapView != null) {
            mMapView.onCreate(savedInstanceState);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }



    private void showAlertDialog(@Nullable String title, @Nullable String message,
                                   DialogInterface.OnClickListener onPositiveButtonClickListener,
                                   @NonNull String positiveText) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(title);
        builder.setMessage(message);
        if (onPositiveButtonClickListener == null) {
            builder.setPositiveButton(positiveText, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        } else {
            builder.setPositiveButton(positiveText, onPositiveButtonClickListener);
        }
        builder.setCancelable(false);
        builder.show();
    }

    private void setSelectedLocation(double lan, double lng)
    {
        if(mSelectedLocation == null) {
            mSelectedLocation = new Location("selectedLoction");
        }
        mSelectedLocation.setLatitude(lan);
        mSelectedLocation.setLongitude(lng);
    }

    @Override
    public PlaceAlert getFragementReturn() {
        String placeName =  mEditPlaceAlias.getText().toString();
        PlaceAlert.Builder builder = new PlaceAlert.Builder((mParam == null ? -1 : mParam.getPlaceAlertID()), placeName, mSelectedLocation.getLatitude(), mSelectedLocation.getLongitude());
        if(mParam != null) {
            builder.setSmsContents(mParam.getSmsContents());
        }
        PlaceAlert placeAlert = new PlaceAlert(builder);
        return placeAlert;
    }

    @Override
    public String getErrorString() {
        return mErrMessage;
    }

    @Override
    public boolean isError() {
        String placeName =  mEditPlaceAlias.getText().toString();
        if(placeName == null || placeName.length() == 0 ) {
            mErrMessage = getString(R.string.alert_error_empty_placename);
            return true;
        }
        else {
            // 좌표가 제대로 선택되었는지 테스트
            if(mSelectedLocation == null) {
                mErrMessage = getString(R.string.alert_error_selected_placename);
                return true;
            }
            return false;
        }
    }
}
