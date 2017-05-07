package jjv.uem.com.aidu.UI;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import jjv.uem.com.aidu.Dialog.DatepickerDialog;
import jjv.uem.com.aidu.Dialog.TimepickerDialog;
import jjv.uem.com.aidu.Model.Service;
import jjv.uem.com.aidu.R;



public class NewService extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int PLACE_PICKER_REQUEST = 1;
    private TextView tv_date, tv_hour, tv_points, tv_photo;
    private EditText et_title, et_adress, et_description;
    private Spinner sp_category, sp_kind;
    private SeekBar sb_points;
    private ImageView imgv_addPhoto;
    private RecyclerView rcv_photos;
    private Button btn_addPhoto, btn_newService;
    private int pricePoints = 5;
    private String[] categorys = {"Pets", "Plants", "help"};
    private String[] kinds = {"Offer", "Request"};
    private Service service = new Service();
    private DatabaseReference mDatabase;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    private ActionBar actBar;
    private String userName,userUid;
    private ArrayList<String> photos = new ArrayList<>();

    private static final LatLngBounds BOUNDS_MOUNTAIN_VIEW = new LatLngBounds(
            new LatLng(37.398160, -122.180831), new LatLng(37.430610, -121.972090));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_service);
        getUserData();
        initViews();
        setTypeFace();
    }

    private void initViews() {
        actBar = getSupportActionBar();
        actBar.setDisplayHomeAsUpEnabled(true);
        //actBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#455a64")));
        et_title = (EditText) findViewById(R.id.et_title);
        et_adress = (EditText) findViewById(R.id.et_adress);
        et_description = (EditText) findViewById(R.id.et_description);
        tv_date = (TextView) findViewById(R.id.tv_date);
        tv_hour = (TextView) findViewById(R.id.tv_hour);
        tv_points = (TextView) findViewById(R.id.tv_points);
        tv_photo = (TextView) findViewById(R.id.tv_photos);
        sp_category = (Spinner) findViewById(R.id.sp_category);
        sp_kind = (Spinner) findViewById(R.id.sp_kind);
        sb_points = (SeekBar) findViewById(R.id.sb_points);
        btn_addPhoto = (Button) findViewById(R.id.btn_add_photo);
        btn_newService = (Button) findViewById(R.id.btn_new_service);
        rcv_photos = (RecyclerView) findViewById(R.id.rcv_photos);
        imgv_addPhoto = (ImageView) findViewById(R.id.imageView);
        String currentTime = formatearHora(new Date().getTime());
        String currentDate = sdf.format(new Date());

        rcv_photos.setVisibility(View.INVISIBLE);
        tv_hour.setText(currentTime);
        tv_date.setText(currentDate);
        tv_points.setText(getString(R.string.new_service_hint_points, pricePoints));
        ArrayAdapter<String> adapter_category = new ArrayAdapter(this, android.R.layout.simple_spinner_item, categorys);
        sp_category.setAdapter(adapter_category);
        ArrayAdapter<String> adapter_kind = new ArrayAdapter(this, android.R.layout.simple_spinner_item, kinds);
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

                try {

                    PlacePicker.IntentBuilder intentBuilder =
                            new PlacePicker.IntentBuilder();
                    intentBuilder.setLatLngBounds(BOUNDS_MOUNTAIN_VIEW);
                    Intent intent = intentBuilder.build(NewService.this);
                    startActivityForResult(intent, PLACE_PICKER_REQUEST);

                } catch (GooglePlayServicesRepairableException
                        | GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });

        sb_points.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                pricePoints=progress;
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

    private void setTypeFace() {
        Typeface bubblerFont = Typeface.createFromAsset(getAssets(), "fonts/BubblerOne-Regular.ttf");
        tv_points.setTypeface(bubblerFont);
        tv_date.setTypeface(bubblerFont);
        tv_hour.setTypeface(bubblerFont);
        tv_photo.setTypeface(bubblerFont);
        et_description.setTypeface(bubblerFont);
        et_title.setTypeface(bubblerFont);
        et_adress.setTypeface(bubblerFont);
        btn_addPhoto.setTypeface(bubblerFont);
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

    public void addService (View v){
        String title = et_title.getText().toString();
        String adress = et_adress.getText().toString();
        String description = et_description.getText().toString();


        if (title.equals("")|| adress.equals("") || description.equals("")){
            Toast.makeText(this,getText(R.string.new_service_toast_enterallfields),Toast.LENGTH_SHORT).show();
        } else {
            service.setDescription(description);
            service.setTitle(title);
            service.setLocation(adress);
            service.setPrice_points(""+pricePoints);
            service.setCategory(sp_category.getSelectedItem().toString());
            service.setDate(tv_date.getText().toString());
            service.setHour(tv_hour.getText().toString());
            service.setKind(sp_kind.getSelectedItem().toString());
            service.setUserkey(userUid);
            service.setUserName(userName);
            service.setState("DISPONIBLE");
            service.setUserkeyInterested("anyone");
            String[] mStringArray = new String[photos.size()];
            mStringArray = photos.toArray(mStringArray);
            service.setPhotos(mStringArray);
            Log.d(TAG,service.getTitle()+" "+service.getCategory()+" "+service.getKind());
            mDatabase = FirebaseDatabase.getInstance().getReference();
            String key = mDatabase.child("service").push().getKey();
            service.setServiceKey(key);
            Map<String, Object> servic = service.toMap();
            Map<String, Object> childUpdates = new HashMap<>();

            childUpdates.put("/services/" + key, servic);
            childUpdates.put("/user-trips/" + userUid + "/" + key, servic);
            mDatabase.updateChildren(childUpdates).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d(TAG,getString(R.string.new_service_toast_service_created));
                }
            });
            finish();
            Toast.makeText(this,getText(R.string.new_service_toast_service_created),Toast.LENGTH_SHORT).show();
        }


    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG,"activyty resultttt");
        if (requestCode == PLACE_PICKER_REQUEST) {
            Log.d(TAG,"vuelta del placepicker " + resultCode);
            if (resultCode == RESULT_OK) {
                final Place place = PlacePicker.getPlace(this, data);
                final CharSequence name = place.getName();
                final CharSequence address = place.getAddress();
                String attributions = (String) place.getAttributions();
                Log.d(TAG,"Place:"+ place +", name: "+name+", adress: "+ address + ", atributions: "+attributions.toString());
            }
        }
    }



}
