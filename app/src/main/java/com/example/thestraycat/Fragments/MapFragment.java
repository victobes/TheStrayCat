package com.example.thestraycat.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.thestraycat.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Locale;


public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static final int COLOR_BLACK_ARGB = 0xff000000;
    private static final int POLYLINE_STROKE_WIDTH_PX = 10;

    static final LatLng FIRST_OBJECT = new LatLng(55.87736922151931,37.72637486457825);
    static final LatLng SECOND_OBJECT = new LatLng(55.87398065114318, 37.71772742271424);
    static final LatLng THIRD_OBJECT = new LatLng(55.876033088763556, 37.72581696510316);

    private String mParam1;
    private String mParam2;

    private View fragmentView;
    private MapView mapView;
    private GoogleMap mMap;

    public MapFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container, new MapFragment()).commit();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);*/
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentView = inflater.inflate(R.layout.fragment_map, container, false);
        return fragmentView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mapView = fragmentView.findViewById(R.id.mapView);
        if (mapView != null) {
            mapView.onCreate(null);
            mapView.onResume();
            mapView.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {

        MapsInitializer.initialize(getActivity());
        mMap = googleMap;
        googleMap.setMapType(mMap.MAP_TYPE_NORMAL);


        Locale.setDefault(Locale.US);
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        /*final PolylineOptions options = new PolylineOptions()
                .color(COLOR_BLACK_ARGB)
                .width(POLYLINE_STROKE_WIDTH_PX)
                .jointType(JointType.ROUND)
                .clickable(true);*/

        /*db.collection("objects")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                MapMarker marker = document.toObject(MapMarker.class);

                                Log.d("lol", marker.getTitle());

                                googleMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(marker.getV()), Double.parseDouble(marker.getV1()))).title(marker.getTitle()));
                                if (marker.getTitle().equals(getString(R.string.marker_title))) {
                                    CameraPosition cameraPosition = CameraPosition.builder()
                                            .target(new LatLng(Double.parseDouble(marker.getV()), Double.parseDouble(marker.getV1())))
                                            .zoom(15)
                                            .bearing(0)
                                            .tilt(45)
                                            .build();
                                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 10000, null);

                                } else {

                                    googleMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(marker.getV()), Double.parseDouble(marker.getV1()))).title(marker.getTitle()));
                                    options.add(new LatLng(Double.parseDouble(marker.getV()), Double.parseDouble(marker.getV1())));
                                    googleMap.addPolyline(options);
                                }
                            }

                        } else {

                            Log.w("TAG", "Error getting documents.", task.getException());
                        }
                    }
                });*/

        /*db.collection("objects")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("lol", document.getId() + " => " + document.getData());

                                MapMarker marker = document.toObject(MapMarker.class);
                                Log.d("xex", marker.getTitle());
                                //googleMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(marker.getV()), Double.parseDouble(marker.getV1()))).title(marker.getTitle()));
                            }
                        } else {
                            Log.w("kek", "Error getting documents.", task.getException());
                        }
                    }
                });*/
        //googleMap.addMarker(new MarkerOptions().position(new LatLng(55.87736922151931, 37.72637486457825)).title("Первый пункт"));
        //MapMarker marker1 = new MapMarker("Первый пункт","55.87736922151931", "37.72637486457825");
        //MarkerOptions marker = new MarkerOptions().position(new LatLng(Double.parseDouble(marker1.getV()), Double.parseDouble(marker1.getV1()))).title(marker1.getTitle());
        //googleMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(marker1.getV()), Double.parseDouble(marker1.getV1()))).title(marker1.getTitle()));
        //googleMap.addMarker(marker);
        //googleMap.setOnMarkerClickListener(OnMarkerClickListener);

        Marker firstObject = mMap.addMarker(new MarkerOptions()
                .position(FIRST_OBJECT)
                .title("Пункт 1")
                .snippet("Ближайщий адрес: ул.Ротерта д.10 корп.2"));
        firstObject.showInfoWindow();

        CameraPosition cameraPosition = CameraPosition.builder()
                .target(FIRST_OBJECT)
                .zoom(15)
                .bearing(0)
                .tilt(45)
                .build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 10000, null);

        Marker secondObject = mMap.addMarker(new MarkerOptions()
                .position(SECOND_OBJECT)
                .title("Second")
                .snippet("Address: 4,137,400"));
        //secondObject.showInfoWindow();

        Marker thirdObject = mMap.addMarker(new MarkerOptions()
                .position(THIRD_OBJECT)
                .title("Third")
                .snippet("Address: 4,137,400"));
        //thirdObject.showInfoWindow();

        mMap.setOnInfoWindowClickListener(this);
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Toast.makeText(getActivity(), "Info window clicked", Toast.LENGTH_SHORT).show();
    }

}