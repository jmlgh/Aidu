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
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.AnyRes;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import jjv.uem.com.aidu.Model.Community;
import jjv.uem.com.aidu.R;


public class NewComunity extends AppCompatActivity {

    //for the treatmet ofthe images
    public static final String URL_STORAGE_REFERENCE = "gs://aidu-195e7.appspot.com";
    public static final String FOLDER_STORAGE_IMG = "Images/communities";
    private static final String TAG = NewComunity.class.getSimpleName();
    private static final int PLACE_PICKER_REQUEST = 1;
    private static final int CAPTURE_IMAGE = 10;
    private static final int PICK_IMAGE = 20;


    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReferenceFromUrl(URL_STORAGE_REFERENCE).child(FOLDER_STORAGE_IMG);


    private EditText et_title, et_adress, et_description;
    private TextInputLayout til_title, til_address, til_description;
    private TextView tv_public;
    private ImageView imv_photos;
    private Button btn_newCommunity;
    private Switch sw_public;

    private Community community = new Community();
    private DatabaseReference mDatabase;
    private ActionBar actBar;
    private String userName, userUid;
    private int icon = -1;
    private String photo = null;
    private String key;


    private File filePathImageCamera;
    private Uri imageToUploadUri;
    private FirebaseAuth mAuth;
    private LatLng cordenades;
    private double longitude;
    private double latitude;
    private ProgressDialog pd;

    private Boolean subido = false;

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
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        setContentView(R.layout.activity_new_comunity);
        getUserData();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        key = mDatabase.child("communyty").push().getKey();
        initViews();
        setTypeFace();
    }

    private void initViews() {
        actBar = getSupportActionBar();

        actBar.setDisplayHomeAsUpEnabled(true);
        //actBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#1c313aeg")));

        et_title = (EditText) findViewById(R.id.et_title);
        et_adress = (EditText) findViewById(R.id.et_adress);
        et_description = (EditText) findViewById(R.id.et_description);
        til_title = (TextInputLayout) findViewById(R.id.input_layout_title);
        til_address = (TextInputLayout) findViewById(R.id.input_layout_adress);
        til_description = (TextInputLayout) findViewById(R.id.input_layout_description);
        tv_public = (TextView) findViewById(R.id.tv_public);
        sw_public = (Switch) findViewById(R.id.sw_public);

        btn_newCommunity = (Button) findViewById(R.id.btn_viewmembers);
        imv_photos = (ImageView) findViewById(R.id.imgv_photo);


        et_adress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                esconderTecladoDe(NewComunity.this,v);
                showPlacepicker();
            }
        });

        et_adress.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                esconderTecladoDe(NewComunity.this,v);
                if (hasFocus){
                    showPlacepicker();
                }
            }
        });

        sw_public.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    sw_public.setText(getString(R.string.new_community_text_swicht_On));
                } else {
                    sw_public.setText(getString(R.string.new_community_text_swicht_Off));
                }
            }
        });

        imv_photos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImagePicker();
            }
        });

    }

    private void setTypeFace() {
        Typeface bubblerFont = Typeface.createFromAsset(getAssets(), "fonts/BubblerOne-Regular.ttf");
        et_description.setTypeface(bubblerFont);
        et_title.setTypeface(bubblerFont);
        et_adress.setTypeface(bubblerFont);
        til_description.setTypeface(bubblerFont);
        til_title.setTypeface(bubblerFont);
        til_address.setTypeface(bubblerFont);
        tv_public.setTypeface(bubblerFont);
        sw_public.setTypeface(bubblerFont);
        btn_newCommunity.setTypeface(bubblerFont);
        TextView myTextView = new TextView(this);
        myTextView.setTypeface(bubblerFont);
        actBar.setCustomView(myTextView);
    }


    private void showPlacepicker() {
        try {

            PlacePicker.IntentBuilder intentBuilder =
                    new PlacePicker.IntentBuilder();
            //intentBuilder.setLatLngBounds(BOUNDS_MOUNTAIN_VIEW);
            Intent intent = intentBuilder.build(NewComunity.this);
            startActivityForResult(intent, PLACE_PICKER_REQUEST);

        } catch (GooglePlayServicesRepairableException
                | GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void esconderTecladoDe(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void getUserData() {
        Intent i = getIntent();
        userName = i.getStringExtra(MainActivity.USERNAME);
        userUid = i.getStringExtra(MainActivity.USERUID);
    }


    public void addCommunity(View v) {
        String title = et_title.getText().toString();
        String adress = et_adress.getText().toString();
        String description = et_description.getText().toString();


        if (title.equals("") || adress.equals("") || description.equals("")) {
            Toast.makeText(this, getText(R.string.new_service_toast_enterallfields), Toast.LENGTH_SHORT).show();
        } else if (photo == null && icon == -1) {
            Toast.makeText(this, getText(R.string.new_service_toast_photos), Toast.LENGTH_SHORT).show();
        } else {
            pd = new ProgressDialog(NewComunity.this);
            pd.setMessage("loading");
            pd.show();
            sendFileFirebase(storageRef, Uri.parse(photo), "photocommunity" + key);
        }


    }

    /*private void uploadCommunity(String title, String adress, String description) {
        pd = new ProgressDialog(NewComunity.this);
        pd.setMessage("loading");
        pd.show();

                //Log.e("url sss"+(photo.size()-1),photo.get(photo.size()-1));

            upload(title, adress, description);

    }*/

    private void uploadCommunity() {
        community.setKey(key);
        community.setPublica(sw_public.isChecked());
        community.setIcon(icon);
        community.setName(et_title.getText().toString());
        community.setAddress(et_adress.getText().toString());
        community.setDescription(et_description.getText().toString());
        community.setLongitude(longitude);
        community.setLatitude(latitude);
        community.setOwner(userUid);
        ArrayList<String> members = new ArrayList<>();
        members.add(userUid);
        community.setMembers(members);
        Map<String, Object> servic = community.toMap();
        Map<String, Object> childUpdates = new HashMap<>();

        childUpdates.put("/communities/" + key, servic);
        //childUpdates.put("/user-services/" + userUid + "/" + key, servic);
        mDatabase.updateChildren(childUpdates).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, getString(R.string.new_community_toast_service_created));
            }
        });

        Toast.makeText(this, getText(R.string.new_community_toast_service_created), Toast.LENGTH_SHORT).show();
        pd.dismiss();

        Intent i = new Intent(this, Communities.class);
        startActivity(i);
        finish();

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Log.d(TAG, "vuelta del placepicker " + requestCode);
            if (requestCode == PLACE_PICKER_REQUEST) {
                final Place place = PlacePicker.getPlace(this, data);
                final CharSequence name = place.getName();
                final CharSequence address = place.getAddress();
                String attributions = (String) place.getAttributions();
                Log.d(TAG, "Place:" + place + ", name: " + name + ", adress: " + place.getAddress() + ", atributions: " + attributions + "***" + place.getAddress());
                et_adress.setText(address);
                cordenades = place.getLatLng();
                longitude = cordenades.longitude;
                latitude = cordenades.latitude;
                Log.e("cordenadas:  ", cordenades.toString());

            } else if (requestCode == PICK_IMAGE)
                onSelectFromGalleryResult(data);
            else if (requestCode == CAPTURE_IMAGE)
                onCaptureImageResult(data);
        }
    }


    // Mostramos un dialogo para dara elgir entre seleccionar la imagen de la galeria
    // o hacer una foto desde la camara
    private void showImagePicker() {


        final boolean result = Utility.checkPermission(NewComunity.this);
        if (result) {
            CharSequence options[] = new CharSequence[]{"Galery", "Camera"};

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
            photo = selectedImageUri.toString();
            Picasso.with(this).load(selectedImageUri).into(imv_photos);
            Glide.with(this).load(photo).into(imv_photos);
            imv_photos.setImageURI(selectedImageUri);
            Log.e("Url", selectedImageUri.toString());
        } else {
            //IS NULL
        }


    }

    private void onCaptureImageResult(Intent data) {

        if (filePathImageCamera != null && filePathImageCamera.exists()) {
            Uri ImageUri = Uri.fromFile(filePathImageCamera);
            //sendFileFirebase(storageRef, ImageUri);
            Picasso.with(this).load(ImageUri).into(imv_photos);
            photo = ImageUri.toString();
            Log.e("Url", ImageUri.toString());
        } else {
            //IS NULL
        }

    }

    public void sendFileFirebase(final StorageReference storageReference, final Uri file, String name) {

        if (storageReference != null) {

            final StorageReference ref = storageReference.child(key + "/" + name);
            Log.e("Url", "entrando en send");
            ref.putFile(file).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Log.e("Url", uri.toString());
                            community.setImage(uri.toString());
                            community.setIcon(-1);
                            uploadCommunity();

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

    @Override
    public void onBackPressed() {

        // firebase sign out
        finish();
        Intent i = new Intent(this, Communities.class);
        startActivity(i);


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
}
