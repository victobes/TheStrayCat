package com.example.thestraycat.Fragments;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.thestraycat.Models.MapMarker;
import com.example.thestraycat.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.MetadataChanges;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Locale;
import java.util.Objects;


public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static final int COLOR_BLACK_ARGB = 0xff000000;
    private static final int POLYLINE_STROKE_WIDTH_PX = 10;

    private String mParam1;
    private String mParam2;

    private View fragmentView;
    private MapView mapView;
    private GoogleMap mMap;
    private CharSequence[] conditions;

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
        conditions = new CharSequence[]{
                "Достаточно",
                "Мало",
                "Отсутствует"
        };
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
        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("objects")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                //Log.d("lol", document.getId() + " => " + document.getData());

                                DocumentReference docRef = db.collection("objects").document(document.getId());
                                docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        MapMarker marker = documentSnapshot.toObject(MapMarker.class);

                                        if (marker.getTitle().equals(getString(R.string.first_object))) {
                                            CameraPosition cameraPosition = CameraPosition.builder()
                                                    .target(new LatLng(Double.parseDouble(marker.getV()), Double.parseDouble(marker.getV1())))
                                                    .zoom(15)
                                                    .bearing(0)
                                                    .tilt(45)
                                                    .build();
                                            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 10000, null);
                                        }
                                        Marker object = googleMap.addMarker(new MarkerOptions()
                                                .position(new LatLng(Double.parseDouble(marker.getV()), Double.parseDouble(marker.getV1())))
                                                .title(marker.getTitle())
                                                .snippet("Ближайший адрес: " + marker.getInformation()));

                                        switch (marker.getCondition()){
                                            case "Достаточно":
                                                object.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                                                break;
                                            case "Мало":
                                                object.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
                                                break;
                                            default:
                                                break;
                                        }
                                        /*if (marker.getCondition().equals("Достаточно")){
                                            object.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                                        }
                                        if (marker.getCondition().equals("Мало")){
                                            object.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
                                        }*/

                                    }
                                });
                            }
                        } else {
                            Log.w("kek", "Error getting documents.", task.getException());
                        }
                    }
                });

        mMap.setOnInfoWindowClickListener(this);
    }

    @Override
    public void onInfoWindowClick(final Marker marker) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext());
        builder.setTitle("Наличие корма");
        //builder.setIcon(R.drawable.ic_home);
       builder.setSingleChoiceItems(conditions, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Log.d("dumling", (String) conditions[which]);
                final FirebaseFirestore db = FirebaseFirestore.getInstance();
                switch (marker.getTitle()){
                    case "Первый пункт":
                        final DocumentReference firstObject = db.collection("objects").document(getString(R.string.id_first_object));
                        firstObject.update("condition", (String) conditions[which])
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d("dumpling", "DocumentSnapshot successfully updated!");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w("lol", "Error updating document", e);
                                    }
                                });
                        break;
                    case "Второй пункт":
                        final DocumentReference secondObject = db.collection("objects").document(getString(R.string.id_second_object));
                        secondObject.update("condition", (String) conditions[which])
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d("dumpling", "DocumentSnapshot successfully updated!");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w("lol", "Error updating document", e);
                                    }
                                });
                        break;
                    case "Третий пункт":
                        final DocumentReference thirdObject = db.collection("objects").document(getString(R.string.id_third_object));
                        thirdObject.update("condition", (String) conditions[which])
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d("dumpling", "DocumentSnapshot successfully updated!");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w("lol", "Error updating document", e);
                                    }
                                });
                        break;
                    default:
                            break;
                }
                /*if (marker.getTitle().equals("Первый пункт")){
                Log.d("dumpling", "реально первый");
                    final FirebaseFirestore db = FirebaseFirestore.getInstance();
                    final DocumentReference firstObject = db.collection("objects").document(getString(R.string.id_first_object));
                    firstObject.update("condition", (String) conditions[which])
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d("dumpling", "DocumentSnapshot successfully updated!");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w("lol", "Error updating document", e);
                                }
                            });

                }
                if (marker.getTitle().equals("Второй пункт")){
                    Log.d("dumpling", "реально второй");
                }*/
            }
        });
        //builder.setMessage("Достаточно");
        builder.show();
    }

}