package com.example.pinder99;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.pinder99.databinding.ActivityReportItemBinding;
import com.google.android.gms.cast.framework.media.ImagePicker;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.shape.ShapeAppearanceModel;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class ReportItemActivity extends AppCompatActivity {
    private ActivityReportItemBinding binding;
    private static final String writeExternalStoragePermission = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    private static final String cameraPermission = Manifest.permission.CAMERA;
    private Uri cameraUri;
    private ArrayList<String> pathList = new ArrayList<>();
    private LinearLayout parent;
    Adapter adapter;

    private EditText editText_locate, editText_phoneNum, editText_feature ;
    private RadioButton radioButton_male, radioButton_female,radioButton_unknown;
    private FirebaseStorage storage;
    private DatabaseReference reference;
    String r_time;
    long mNow;
    Date mDate;
    SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd / hh:mm:ss");
    ImageView imageRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityReportItemBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        editText_locate = (EditText) findViewById(R.id.et_locate);
        editText_phoneNum = (EditText) findViewById(R.id.et_reporter_phoneNum);
        editText_feature = (EditText) findViewById(R.id.et_write_feature);
        RadioGroup radio_btn = (RadioGroup)findViewById(R.id.radioGroup);

        setSpinner();
        showPictureBottomSheet();
        initDataBase();
        binding.writeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });
    }

    private void setSpinner() {
        /** 스피너 **/
        String category[]={"신고", "제보"};
        String category_locate[]={"서울특별시",  "경기도",  "부산광역시",  "대구광역시",  "인천광역시",  "광주광역시",
                "세종특별자치시",  "강원도",  "충청북도",  "충청남도",  "전라북도",  "전라남도",  "경상북도",  "경상남도",  "제주특별자치도"};
        String category_kind[]={"강아지",  "고양이",  "그 외"};

        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item, category);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerCategory.setAdapter(spinnerArrayAdapter);

        ArrayAdapter<String> spinner2ArrayAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item, category_locate);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerCategoryLocate.setAdapter(spinner2ArrayAdapter);

        ArrayAdapter<String> spinner3ArrayAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item, category_kind);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerPetKind.setAdapter(spinner3ArrayAdapter);

    }

    private void initDataBase() {
        storage = FirebaseStorage.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        reference = database.getReference().child("Main_Report");
    }

    // RealTimeDataBase에는 텍스트의 데이터를 저장하기 때문에 Image는 Firebase Storage에 선저장후 링크를 받아 DataBase에 넣어야한다.
    // Uri형태의 이미지를 우선 byte[]형태로 만들어서 서버에 올려야한다.
    // 서버에 올라가는 image 이름은 uri를 /로 나누었을 때 마지막 단어로 설정한다.
    // image가 먼저 성공적으로 storage에 올라간 후에, UploadTask에 addOnSuccessListener의
    // onSuccess블록안으로 콜백이 들어오면 download Url을 받아서 DataBase에 올리는 작업을 수행한다.

   private void uploadImage() {
        String[] uriList = cameraUri.toString().split("/");
        String name = Arrays.stream(uriList).collect(Collectors.toList()).get(uriList.length-1);

        StorageReference imageReference = storage.getReference().child("Main_Report").child(name);
        UploadTask uploadTask = imageReference.putBytes(getImageBytes());
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        uploadToRealTimeDataBase(uri.toString());
                        Log.e("success", "success");
                    }
                });
            }
        });
    }

    private void uploadToRealTimeDataBase(String downloadURL) {
        int id = binding.radioGroup.getCheckedRadioButtonId();
        RadioButton radio = (RadioButton)findViewById(id);
        String rbtn = radio.getText().toString();
        String location = editText_locate.getText().toString();
        String phone = editText_phoneNum.getText().toString();
        String feature = editText_feature.getText().toString();

        String type = binding.spinnerCategory.getSelectedItem().toString();
        String location2 = binding.spinnerCategoryLocate.getSelectedItem().toString();
        String kind = binding.spinnerPetKind.getSelectedItem().toString();

        String r_time = getTime();

        String key = reference.push().getKey();

        if (key != null) {
//textView 값들 db에 저장
            reference.child(key).child("detail_locate").setValue(location);
            reference.child(key).child("phoneNum").setValue(phone);
            reference.child(key).child("feature").setValue(feature);
// 스피너들 값 db에 저장
            reference.child(key).child("locate").setValue(location2);
            reference.child(key).child("report_type").setValue(type);
            reference.child(key).child("pet_kind").setValue(kind);
// 라디오 그룹 값 db에 저장
            reference.child(key).child("Pet_Gender").setValue(rbtn);

            reference.child(key).child("report_image").setValue(downloadURL);
            reference.child(key).child("report_time").setValue(r_time); // 글 등록 시간을 db에 저장

            editText_locate.setText("");
            editText_phoneNum.setText("");
            editText_feature.setText("");

            Glide.with(binding.animalImage.getContext()).clear(binding.animalImage);
        }
        Intent intent =new Intent(getApplicationContext(), ReportFragment.class);  // 글 등록을 마친 후 리사이클러뷰 나오는 곳으로 화면 전환
        startActivity(intent);
    }

    private String getTime(){
        mNow=System.currentTimeMillis();
        mDate=new Date(mNow);
        return r_time = mFormat.format(mDate);
    }

    // 갤러리에서 사진 선택, 카메라로 사진촬영을 선택할 수 있는 BottomSheetDialogFragment를 띄워준다.
    // 두가지 버튼 중 하나를 클릭하면 SelectMethodListener로 콜백을 받아 각각의 작업을 수행한다.
    private void showPictureBottomSheet() {
        binding.animalImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddPictureBottomSheetFragment bottomSheet = new AddPictureBottomSheetFragment(new AddPictureBottomSheetFragment.SelectMethodListener() {
                    @Override
                    public void openCamera() {
                        requestCameraPermissions.launch(new String[]{cameraPermission, writeExternalStoragePermission});
                    }

                    @Override
                    public void openGallery() {
                        requestGalleryPermission.launch(new String[]{writeExternalStoragePermission});
                    }
                });
                bottomSheet.show(getSupportFragmentManager(), "add picture");

            }
        });
    }

    // Intent를 이용하여 갤러리를 연다.
    // 생성한 intent 객체를 ActivityResultLauncher 의 launch 함수에 파라미터로 설정하여 결과값을 받아올 수 있도록 한다.
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        intent.setType("image/*");
        fetchPictureFromGallery.launch(intent);
    }

    private ActivityResultLauncher<Intent> fetchPictureFromGallery = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    Uri uri = result.getData().getData();
                    if (uri!= null) {
                        cameraUri = uri;
                        Glide.with(binding.animalImage).load(uri).into(binding.animalImage);
                        setImageRounded(20);
                    }
                }
            }
    );

    // Gallery 접근 권한체크
    // StartActivityForResult가 Deprecated 되었기 때문에 ActivityResultLauncher를 이용한다.
    private ActivityResultLauncher<String[]> requestGalleryPermission = registerForActivityResult(
            new ActivityResultContracts.RequestMultiplePermissions(),
            new ActivityResultCallback<Map<String, Boolean>>() {
                @Override
                public void onActivityResult(Map<String, Boolean> result) {
                    if (result.values().stream().allMatch(r -> r.equals(true))) {
                        openGallery();
                    }
                }
            });

    // camera 접근 권한 체크
    private ActivityResultLauncher<String[]> requestCameraPermissions = registerForActivityResult(
            new ActivityResultContracts.RequestMultiplePermissions(),
            new ActivityResultCallback<Map<String, Boolean>>() {
                @Override
                public void onActivityResult(Map<String, Boolean> result) {
                    if (result.values().stream().allMatch(r -> r.equals(true))) {
                        takePicture();
                    }
                }
            });

    private void takePicture() {
        try {
            File file = File.createTempFile("IMG_", ".jpg", getExternalFilesDir(Environment.DIRECTORY_PICTURES));
            Uri pictureUri = FileProvider.getUriForFile(this, getPackageName() +".provider", file);
            cameraUri = pictureUri;
            cameraActivityLauncher.launch(cameraUri);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private ActivityResultLauncher<Uri> cameraActivityLauncher = registerForActivityResult(
            new ActivityResultContracts.TakePicture(),
            new ActivityResultCallback<Boolean>() {
                @Override
                public void onActivityResult(Boolean result) {
                   if (result) {
                       Glide.with(binding.animalImage.getContext()).load(cameraUri).into(binding.animalImage);
                       setImageRounded(20);
                   }
                }
            });

    private void setImageRounded(Integer round) {
        binding.animalImage.setShapeAppearanceModel(new ShapeAppearanceModel().withCornerSize(round));
    }

    // ImageView에 로드된 사진의 BitmapDrawable을 추출하여 byte array로 변환한다.
    private byte[] getImageBytes() {
        binding.animalImage.setDrawingCacheEnabled(true);
        binding.animalImage.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) binding.animalImage.getDrawable()).getBitmap();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }


}

