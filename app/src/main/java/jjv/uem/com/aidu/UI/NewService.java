package jjv.uem.com.aidu.UI;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.AnyRes;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import org.lucasr.twowayview.TwoWayView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import jjv.uem.com.aidu.Adapters.Images_Adapter;
import jjv.uem.com.aidu.Dialog.DatepickerDialog;
import jjv.uem.com.aidu.Dialog.TimepickerDialog;
import jjv.uem.com.aidu.Model.Community;
import jjv.uem.com.aidu.Model.Service;
import jjv.uem.com.aidu.R;
import jjv.uem.com.aidu.util.CommunitiesCardAdapter;
import jjv.uem.com.aidu.util.Constants;


public class NewService extends AppCompatActivity {

    //for the treatmet ofthe images
    public static final String URL_STORAGE_REFERENCE = "gs://aidu-195e7.appspot.com";
    public static final String FOLDER_STORAGE_IMG = "Images/Userimage";
    private static final String TAG = NewService.class.getSimpleName();
    private static final int PLACE_PICKER_REQUEST = 1;
    private static final int CAPTURE_IMAGE = 10;
    private static final int PICK_IMAGE = 20;
    private static final int MAX_IMAGE = 3;


    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReferenceFromUrl(URL_STORAGE_REFERENCE).child(FOLDER_STORAGE_IMG);
    ArrayList<String> photo = new ArrayList<>();
    private Images_Adapter adapter;
    private TextView tv_date, tv_hour, tv_points, tv_photo, tv_community;
    private EditText et_title, et_adress, et_description;
    private MaterialBetterSpinner sp_category, sp_kind;
    private SeekBar sb_points;
    private MaterialBetterSpinner mbs_community ;
    private LinearLayout lt_spinner;
    private TwoWayView twv_photos;
    private Button btn_newService;
    private int pricePoints = 5;
    private String[] categories;


    private String[] kinds;
    private Service service = new Service();
    private DatabaseReference mDatabase;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    private ActionBar actBar;
    private String userName, userUid;
    private ArrayList<String> photos = new ArrayList<>();
    private String key;
    private String communityKey = null;

    private File filePathImageCamera;
    private FirebaseAuth mAuth;
    private LatLng cordenades;
    private double longitude;
    private double latitude;
    private String kind_selected;
    private String category_selected;
    private int icon_selected;

    private ArrayList<Community> myCommunities;
    private ArrayList<String> myCommunitiesnames;


    private ProgressDialog pd;

    /*private static final LatLngBounds BOUNDS_MOUNTAIN_VIEW = new LatLngBounds(
            new LatLng(37.398160, -122.180831), new LatLng(37.430610, -121.972090));*/

    public static final Uri getUriToDrawable(@NonNull Context context,
                                             @AnyRes int drawableId) {
        Uri imageUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE +
                "://" + context.getResources().getResourcePackageName(drawableId)
                + '/' + context.getResources().getResourceTypeName(drawableId)
                + '/' + context.getResources().getResourceEntryName(drawableId));
        return imageUri;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_service);
        getUserData();
        kind_selected="";
        category_selected="";
        icon_selected=-1;

        initKinds();
        initCategories();

        mDatabase = FirebaseDatabase.getInstance().getReference();
        key = mDatabase.child("service").push().getKey();
        initViews();
        getmyCommunities();
        setTypeFace();
    }

    private void initKinds() {
        kinds = new String[2];
        kinds [0]= getString(R.string.solicita);
        kinds [1]= getString(R.string.ayuda);
    }

    private void initCategories() {
        categories = new String[9];
        categories[0] = getString(R.string.cocinar);
        categories[1] = getString(R.string.transporte);
        categories[2] = getString(R.string.educacion);
        categories[3] = getString(R.string.limpieza);
        categories[4] = getString(R.string.tecnologia);
        categories[5] = getString(R.string.compras);
        categories[6] = getString(R.string.mascotas);
        categories[7] = getString(R.string.plantas);
        categories[8] = getString(R.string.otros);
    }

    private void initViews() {
        actBar = getSupportActionBar();

        actBar.setDisplayHomeAsUpEnabled(true);
        //actBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#1c313aeg")));
        et_title = (EditText) findViewById(R.id.et_title);
        et_adress = (EditText) findViewById(R.id.et_adress);
        et_description = (EditText) findViewById(R.id.et_description);
        tv_date = (TextView) findViewById(R.id.tv_date);
        tv_hour = (TextView) findViewById(R.id.tv_hour);
        tv_points = (TextView) findViewById(R.id.tv_points);
        tv_photo = (TextView) findViewById(R.id.tv_photos);
        //tv_community = (TextView) findViewById(R.id.tv_community);
        sp_category = (MaterialBetterSpinner) findViewById(R.id.sp_category);
        sp_kind = (MaterialBetterSpinner) findViewById(R.id.sp_kind);
        sb_points = (SeekBar) findViewById(R.id.sb_points);
        btn_newService = (Button) findViewById(R.id.btn_new_service);
        twv_photos = (TwoWayView) findViewById(R.id.twv_photos);
        String currentTime = formatearHora(new Date().getTime());
        String currentDate = sdf.format(new Date());
        mbs_community = (MaterialBetterSpinner) findViewById(R.id.mbs_community);
        lt_spinner = (LinearLayout) findViewById(R.id.lt_spinner);

        Uri uri = getUriToDrawable(this, R.drawable.addphoto);
        photos.add(uri.toString());
        Log.e("uri del default ", uri.toString());
        adapter = new Images_Adapter(this, photos,true);
        twv_photos.setAdapter(adapter);
        twv_photos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (photos.size()<MAX_IMAGE+1){
                    showImagePicker();
                } else {
                    Log.e("te has pasaoo", getString(R.string.new_service_toast_no_more_images,MAX_IMAGE));
                    Toast.makeText(NewService.this,getString(R.string.new_service_toast_no_more_images,MAX_IMAGE),Toast.LENGTH_LONG);
                }

            }
        });
        tv_hour.setText(currentTime);
        tv_date.setText(currentDate);
        tv_points.setText(getString(R.string.new_service_hint_points, pricePoints));
        ArrayAdapter<String> adapter_category = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, categories);
        sp_category.setAdapter(adapter_category);
        ArrayAdapter<String> adapter_kind = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, kinds);
        sp_kind.setAdapter(adapter_kind);


        tv_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datepicker = new DatepickerDialog();
                datepicker.show(getSupportFragmentManager(), "Select the time");
            }
        });

        tv_hour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment timepicker = new TimepickerDialog();
                timepicker.show(getSupportFragmentManager(), "Select the time");
            }
        });

        et_adress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                esconderTecladoDe(NewService.this,v);
                showPlacepicker();
            }
        });

        et_adress.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                esconderTecladoDe(NewService.this,v);
                if (hasFocus){
                    showPlacepicker();
                }
            }
        });


        sp_category.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                icon_selected = position;
                category_selected=categories[position];
            }
        });

        sp_kind.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                kind_selected=kinds[position];
            }
        });



        sb_points.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                pricePoints = progress;
                tv_points.setText(getString(R.string.new_service_hint_points, pricePoints));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

    private void showPlacepicker() {
        try {

            PlacePicker.IntentBuilder intentBuilder =
                    new PlacePicker.IntentBuilder();
            //intentBuilder.setLatLngBounds(BOUNDS_MOUNTAIN_VIEW);
            Intent intent = intentBuilder.build(NewService.this);
            startActivityForResult(intent, PLACE_PICKER_REQUEST);

        } catch (GooglePlayServicesRepairableException
                | GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void setTypeFace() {
        Typeface bubblerFont = Typeface.createFromAsset(getAssets(), "fonts/BubblerOne-Regular.ttf");
        tv_points.setTypeface(bubblerFont);
        tv_date.setTypeface(bubblerFont);
        tv_hour.setTypeface(bubblerFont);
        //tv_community.setTypeface(bubblerFont);
        mbs_community.setTypeface(bubblerFont);
        tv_photo.setTypeface(bubblerFont);
        et_description.setTypeface(bubblerFont);
        et_title.setTypeface(bubblerFont);
        sp_category.setTypeface(bubblerFont);
        sp_kind.setTypeface(bubblerFont);

        et_adress.setTypeface(bubblerFont);
        btn_newService.setTypeface(bubblerFont);
        TextView myTextView = new TextView(this);
        myTextView.setTypeface(bubblerFont);
        actBar.setCustomView(myTextView);
    }

    public void getUserData() {
        Intent i = getIntent();
        userName = i.getStringExtra(MainActivity.USERNAME);
        userUid = i.getStringExtra(MainActivity.USERUID);
    }

    public String formatearHora(long hora) {
        String horaFormateada;
        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getTimeZone("GMT+1"));
        cal.setTimeInMillis(hora);

        horaFormateada = String.format("%02d:%02d", cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE));
        return horaFormateada;
    }

    public void addService(View v) {
        String title = et_title.getText().toString();
        String adress = et_adress.getText().toString();
        String description = et_description.getText().toString();


        if (title.equals("") || adress.equals("") || description.equals("")) {
            Toast.makeText(this, getText(R.string.new_service_toast_enterallfields), Toast.LENGTH_SHORT).show();
        } else if (photos.size() < 2) {
            Toast.makeText(this, getText(R.string.new_service_toast_photos), Toast.LENGTH_SHORT).show();
        } else if (communityKey == null) {
            Toast.makeText(this, getText(R.string.new_service_toast_no_community), Toast.LENGTH_SHORT).show();
        } else if(icon_selected==-1||category_selected.equals("")) {
            Toast.makeText(this, getText(R.string.new_service_toast_no_category), Toast.LENGTH_SHORT).show();
        }else if(kind_selected.equals("")){
            Toast.makeText(this, getText(R.string.new_service_toast_no_kind), Toast.LENGTH_SHORT).show();
        }else {
            pd = new ProgressDialog(NewService.this);
            pd.setMessage("loading");
            pd.show();
            for (int i = 1; i < photos.size(); i++) {
                sendFileFirebase(storageRef, Uri.parse(photos.get(i)), "photo" + i);
                //Log.e("url sss"+(photo.size()-1),photo.get(photo.size()-1));
            }
        }


    }

    private void uploadService() {

        if (photo.size() == photos.size() - 1) {
            service.setDescription(et_description.getText().toString());
            service.setTitle(et_title.getText().toString());
            service.setLocation(et_adress.getText().toString());
            service.setPrice_points("" + pricePoints);
            service.setCategory(category_selected);
            service.setDate(tv_date.getText().toString());
            service.setHour(tv_hour.getText().toString());
            service.setKind(kind_selected);
            service.setUserkey(userUid);
            service.setUserName(userName);
            service.setCommunity(communityKey);

            service.setState(Constants.DISPONIBLE);
            service.setUserkeyInterested("");
            service.setLatitude(latitude);
            service.setLongitude(longitude);
            service.setIcon(icon_selected);


            service.setPhotos(photo);
            Log.d(TAG, service.getTitle() + " " + service.getCategory() + " " + service.getKind());

            service.setServiceKey(key);

            Map<String, Object> servic = service.toMap();
            Map<String, Object> childUpdates = new HashMap<>();

            childUpdates.put("/services/" + key, servic);
            childUpdates.put("/user-services/" + userUid + "/" + key, servic);
            mDatabase.updateChildren(childUpdates).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d(TAG, getString(R.string.new_service_toast_service_created));
                }
            });

            pd.dismiss();
            Toast.makeText(this, getText(R.string.new_service_toast_service_created), Toast.LENGTH_SHORT).show();
            finish();
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
        }


    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Log.d(TAG, "vuelta del placepicker " + resultCode);
            if (requestCode == PLACE_PICKER_REQUEST) {
                final Place place = PlacePicker.getPlace(this, data);
                final CharSequence name = place.getName();
                final CharSequence address = place.getAddress();
                String attributions = (String) place.getAttributions();
                //Log.d(TAG, "Place:" + place + ", name: " + name + ", adress: " + place.getAddress() + ", atributions: " + attributions + "***" + place.getAddress());
                et_adress.setText(address);
                cordenades = place.getLatLng() ;
                longitude = cordenades.longitude;
                latitude = cordenades.latitude;
                //Log.e("cordenadas:  ", cordenades.toString());

            } else if (requestCode == PICK_IMAGE)
                onSelectFromGalleryResult(data);
            else if (requestCode == CAPTURE_IMAGE)
                onCaptureImageResult(data);
        }
    }


    // Mostramos un dialogo para dara elgir entre seleccionar la imagen de la galeria
    // o hacer una foto desde la camara
    private void showImagePicker() {


        final boolean result = Utility.checkPermission(NewService.this);
        if (result) {
            CharSequence options[] = new CharSequence[]{"Gallery", "Camera"};

            AlertDialog.Builder picker = new AlertDialog.Builder(this);
            picker.setTitle(getString(R.string.new_service_photo_selection));


            picker.setCancelable(true);
            picker.setItems(options, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (which == 0) {
                        Log.e("Pulsado el btngaleria: ", "en on click");
                        galleryIntent();
                    } else {
                        Log.e("Pulsado el btn camara: ", "en on click");
                        cameraIntent();
                    }
                }
            });
            picker.show();
        }

    }

    //Iniciamos un nuevo intent que nos abrira la camara
    private void cameraIntent() {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAPTURE_IMAGE);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        filePathImageCamera = image;
        return image;
    }

    private void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, getString(R.string.new_service_photo_selection)), PICK_IMAGE);
    }


    private void onSelectFromGalleryResult(Intent data) {
        Uri selectedImageUri = data.getData();
        if (selectedImageUri != null) {
            //sendFileFirebase(storageRef, selectedImageUri);
            photos.add(selectedImageUri.toString());
            adapter = new Images_Adapter(NewService.this, photos,true);
            adapter.notifyDataSetChanged();
            twv_photos.setAdapter(adapter);
            Log.e("Url", selectedImageUri.toString());
        } else {
            //IS NULL
        }


    }

    private void onCaptureImageResult(Intent data) {

        if (filePathImageCamera != null && filePathImageCamera.exists()) {
            Uri ImageUri = Uri.fromFile(filePathImageCamera);
            //sendFileFirebase(storageRef, ImageUri);
            photos.add(ImageUri.toString());
            Log.e("Url", ImageUri.toString());
        } else {
            //IS NULL
        }
    }

    public void sendFileFirebase(final StorageReference storageReference, final Uri file, String name) {

        if (storageReference != null) {

            final StorageReference ref = storageReference.child(userUid + "/" + key + "/" + name);
            Log.e("Url", "entrando en send");
            ref.putFile(file).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Log.e("Url", uri.toString());
                            photo.add(uri.toString());
                            uploadService();

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle any errors
                        }
                    });
                }
            });

        } else {
//IS NULL
        }
    }

    private void getmyCommunities() {
        // Acceso a BBDD Firebase

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("communities");
            //Query query = reference.orderByChild("members").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid());
            reference.addValueEventListener(new ValueEventListener() {
                @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Iterable<DataSnapshot> iterator = dataSnapshot.getChildren();
                    myCommunities = new ArrayList<>();
                    myCommunitiesnames = new ArrayList<>();
                    myCommunitiesnames.add(getString(R.string.new_service_no_community));
                    Log.e("mycommunity"," "+dataSnapshot.getChildrenCount()+"  "+ dataSnapshot.getKey());
                    for (DataSnapshot ds : iterator) {
                        Community c = ds.getValue(Community.class);
                        Log.e("mycommunity",c.getName()+"  "+ c.getKey());
                        if (c.getMembers().contains(FirebaseAuth.getInstance().getCurrentUser().getUid())) {

                            //Log.i("SERVICE GET:",c.toString());
                            myCommunities.add(c);
                            myCommunitiesnames.add(c.getName());
                        }
                    }

                    if(myCommunities!=null){
                        lt_spinner.setVisibility(View.VISIBLE);
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(NewService.this,
                                android.R.layout.simple_dropdown_item_1line, myCommunitiesnames);

                        mbs_community.setAdapter(adapter);
                        mbs_community.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                if (position>0){
                                    communityKey = myCommunities.get(position-1).getKey();
                                }else {
                                    communityKey = getString(R.string.new_service_no_community);
                                }

                            }
                        });
                    }



                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

    }



    public static class Utility {
        public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        public static boolean checkPermission(final Context context) {
            int currentAPIVersion = Build.VERSION.SDK_INT;
            if (currentAPIVersion >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
                        alertBuilder.setCancelable(true);
                        alertBuilder.setTitle("Permission necessary");
                        alertBuilder.setMessage("External storage permission is necessary");
                        alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                            }
                        });
                        AlertDialog alert = alertBuilder.create();
                        alert.show();
                    } else {
                        ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                    }
                    return false;
                } else {
                    return true;
                }
            } else {
                return true;
            }
        }
    }

    private void setFocuListener(final TextView tv) {
        View.OnFocusChangeListener focusListener = new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                esconderTecladoDe(NewService.this, tv);
            }
        };

        tv.setOnFocusChangeListener(focusListener);
    }

    public static void esconderTecladoDe(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    @Override
    public void onBackPressed() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }


}