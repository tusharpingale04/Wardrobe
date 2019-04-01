package com.tushar.wardrobe.view.activities;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.tushar.wardrobe.BuildConfig;
import com.tushar.wardrobe.R;
import com.tushar.wardrobe.database.jeans.JeansImages;
import com.tushar.wardrobe.database.liked.Liked;
import com.tushar.wardrobe.database.shirts.ShirtImages;
import com.tushar.wardrobe.databinding.ActivityMainBinding;
import com.tushar.wardrobe.interfaces.IJeansPosition;
import com.tushar.wardrobe.interfaces.IShirtPosition;
import com.tushar.wardrobe.utilities.BitmapUtils;
import com.tushar.wardrobe.utilities.FileUtils;
import com.tushar.wardrobe.view.fragments.JeansFragment;
import com.tushar.wardrobe.view.fragments.ShirtFragment;
import com.tushar.wardrobe.viewmodel.ImagesViewModel;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements IShirtPosition, IJeansPosition {

    private int shirtPosition, jeansPosition;
    private static final int MY_PERMISSIONS_REQUEST_WRITE_STORAGE = 123;
    private static final int PICK_IMAGE_FROM_CAMERA_REQUEST = 1337;
    private static final int REQUEST_IMAGE_CAPTURE_CAMERA = 100;
    private static final int REQUEST_IMAGE_CAPTURE_GALLERY = 200;
    private ActivityMainBinding mainActivityBinding;
    private ImagesViewModel imagesViewModel;
    private List<ShirtImages> shirtImagesList;
    private List<JeansImages> jeansImagesList;
    private Set<String> likedItems = new HashSet<>();
    private String[] options = {"Camera", "Gallery"};
    private String pictureFilePath;
    private String type = "";
    private static final String TAG = "Folder Created";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivityBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        imagesViewModel = ViewModelProviders.of(this).get(ImagesViewModel.class);
        mainActivityBinding.setViewmodel(imagesViewModel);

        //Fragment Shirt
        final ShirtFragment shirtFragment = new ShirtFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.shirtFragment, shirtFragment).commit();

        //Fragment Jeans
        final JeansFragment jeansFragment = new JeansFragment();
        FragmentManager fragmentManagerJeans = getSupportFragmentManager();
        fragmentManagerJeans.beginTransaction().replace(R.id.jeansFragment, jeansFragment).commit();

        //Get Liked Items
        imagesViewModel.getLikedLiveData().observe(this, new Observer<List<Liked>>() {
            @Override
            public void onChanged(@Nullable List<Liked> likeds) {
                if (likeds != null && likeds.size() > 0) {
                    likedItems = new HashSet<>();
                    for (int i = 0; i < likeds.size(); i++) {
                        likedItems.add(likeds.get(i).getLikedID());
                    }
                    if(shirtPosition == 0 && jeansPosition == 0){
                        String liked = shirtPosition + "," + jeansPosition;
                        boolean isAlreadyLiked = likedItems.contains(liked);

                        if (isAlreadyLiked) {
                            mainActivityBinding.btnFavorite.setImageResource(R.drawable.ic_favorite_white_24dp);
                        } else {
                            mainActivityBinding.btnFavorite.setImageResource(R.drawable.ic_favorite_border_white_24dp);
                        }
                    }
                }
            }
        });

        //Live Data to fetch All Shirts
        imagesViewModel.getShirtsImagesLiveData().observe(this, new Observer<List<ShirtImages>>() {
            @Override
            public void onChanged(@Nullable List<ShirtImages> shirtImages) {
                if (shirtImages != null && shirtImages.size() > 0) {
                    shirtImagesList = shirtImages;
                    shirtFragment.shirtAdded(shirtImagesList);
                    if (shirtImagesList != null && shirtImagesList.size() > 0 && jeansImagesList != null && jeansImagesList.size() > 0) {
                        imagesViewModel.fabVisibility.set(0);
                    } else {
                        imagesViewModel.fabVisibility.set(8);
                    }
                } else {
                    imagesViewModel.fabVisibility.set(8);
                }
            }
        });

        //Live Data to fetch all Jeans
        imagesViewModel.getJeansLiveData().observe(this, new Observer<List<JeansImages>>() {
            @Override
            public void onChanged(@Nullable List<JeansImages> jeansImages) {
                if (jeansImages != null && jeansImages.size() > 0) {
                    jeansImagesList = jeansImages;
                    jeansFragment.jeansAdded(jeansImagesList);
                    if (shirtImagesList != null && shirtImagesList.size() > 0 && jeansImagesList != null && jeansImagesList.size() > 0) {
                        imagesViewModel.fabVisibility.set(0);
                    } else {
                        imagesViewModel.fabVisibility.set(8);
                    }
                } else {
                    imagesViewModel.fabVisibility.set(8);
                }
            }
        });

        //Generate Random Collection of Shirt and Jeans
        mainActivityBinding.btnRandom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shirtPosition = shirtFragment.onShirtShuffled();
                jeansPosition = jeansFragment.onJeansShuffled();

                String liked = shirtPosition + "," + jeansPosition;
                boolean isAlreadyLiked = likedItems.contains(liked);

                if (isAlreadyLiked) {
                    mainActivityBinding.btnFavorite.setImageResource(R.drawable.ic_favorite_white_24dp);
                } else {
                    mainActivityBinding.btnFavorite.setImageResource(R.drawable.ic_favorite_border_white_24dp);
                }
            }
        });

        //Add to Like
        mainActivityBinding.btnFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String liked = shirtPosition + "," + jeansPosition;
                boolean isAlreadyLiked = likedItems.contains(liked);

                if (!isAlreadyLiked) {
                    mainActivityBinding.btnFavorite.setImageResource(R.drawable.ic_favorite_white_24dp);
                    likedItems.add(liked);
                    Liked like = new Liked();
                    like.setLikedID(liked);
                    imagesViewModel.addToLike(like);
                } else {
                    mainActivityBinding.btnFavorite.setImageResource(R.drawable.ic_favorite_border_white_24dp);
                    likedItems.remove(liked);
                    imagesViewModel.deleteLike(liked);
                }
            }
        });

        //Shirt Chooser
        mainActivityBinding.btnAddShirt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                type = "Shirt";
                showDialogToChoose();
            }
        });

        //Jeans Chooser
        mainActivityBinding.btnAddJeans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                type = "Jeans";
                showDialogToChoose();
            }
        });


    }

    private void showDialogToChoose() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Select Option");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                boolean result = checkPermission();
                switch (item) {
                    case 0:
                        if (result) {
                            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA)
                                    == PackageManager.PERMISSION_DENIED) {
                                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, PICK_IMAGE_FROM_CAMERA_REQUEST);
                            } else {
                                captureImage();
                            }
                        }
                        break;
                    case 1:
                        if (result) {
                            showGallery();
                        }
                        break;
                }
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public boolean checkPermission() {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(MainActivity.this);
                    alertBuilder.setCancelable(true);
                    alertBuilder.setTitle("Permission necessary");
                    alertBuilder.setMessage("Write Storage permission is necessary to Save Images!");
                    alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_STORAGE);
                        }
                    });
                    AlertDialog alert = alertBuilder.create();
                    alert.show();
                } else {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_STORAGE);
                }
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_STORAGE:
                if (!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    checkAgain();
                } else {
                    createFolder();
                }
                break;
            case PICK_IMAGE_FROM_CAMERA_REQUEST:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    captureImage();
                } else {
                    Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    private void createFolder() {
        String externalDirectory = Environment.getExternalStorageDirectory().toString();
        File folder = new File(externalDirectory + "/Wardrobe");
        boolean folderCreated = folder.mkdir();
        Log.d(TAG, "" + folderCreated);
    }

    private void captureImage() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_FINISH_ON_COMPLETION, true);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            File pictureFile;
            try {
                pictureFile = getPictureFile();
            } catch (IOException ex) {
                Toast.makeText(this,
                        "Photo file can't be created, please try again",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            if (pictureFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        BuildConfig.APPLICATION_ID + ".provider",
                        pictureFile);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE_CAMERA);
            }
        }
    }

    private File getPictureFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss", Locale.US).format(new Date());
        String pictureFile = "IMG_" + timeStamp;
        String externalDirectory = Environment.getExternalStorageDirectory().toString();
        File storageDir = new File(externalDirectory + "/Wardrobe");
        File image = File.createTempFile(pictureFile, ".jpg", storageDir);
        pictureFilePath = image.getAbsolutePath();
        return image;
    }

    private void checkAgain() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(MainActivity.this);
            alertBuilder.setCancelable(true);
            alertBuilder.setTitle("Permission necessary");
            alertBuilder.setMessage("Write Storage permission is necessary to Save Images!");
            alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_STORAGE);
                }
            });
            AlertDialog alert = alertBuilder.create();
            alert.show();
        } else {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_STORAGE);
        }
    }

    private void showGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_IMAGE_CAPTURE_GALLERY);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_IMAGE_CAPTURE_GALLERY:
                if (resultCode == AppCompatActivity.RESULT_OK) {
                    if (data != null) {
                        Uri uri = data.getData();
                        String selectedFilePath = FileUtils.getPath(this, uri);
                        File file = new File(selectedFilePath);
                        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                        Bitmap compressedBitmap = getResizedBitmap(bitmap, 480, 640);
                        byte[] toSaveBm = BitmapUtils.convertBitmapToByteArray(compressedBitmap);
                        saveDataToRoom(toSaveBm);
                    }
                } else if (resultCode == AppCompatActivity.RESULT_CANCELED) {
                    Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show();
                }
                break;
            case REQUEST_IMAGE_CAPTURE_CAMERA:
                if (resultCode == RESULT_OK) {
                    try {
                        File imgFile = new File(pictureFilePath);
                        Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                        Bitmap compressedBitmap = getResizedBitmap(bitmap, 480, 640);
                        byte[] toSaveBm = BitmapUtils.convertBitmapToByteArray(compressedBitmap);
                        saveDataToRoom(toSaveBm);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                }
                break;

        }
    }

    private void saveDataToRoom(byte[] toSaveBm) {
        if (!TextUtils.isEmpty(type)) {
            switch (type) {
                case "Shirt":
                    if (shirtImagesList != null && shirtImagesList.size() > 0) {
                        int primaryKey = shirtImagesList.size() + 1;
                        ShirtImages images = new ShirtImages(primaryKey, toSaveBm);
                        imagesViewModel.insertShirtImage(images);
                    } else {
                        shirtImagesList = new ArrayList<>();
                        ShirtImages images = new ShirtImages(1, toSaveBm);
                        imagesViewModel.insertShirtImage(images);
                    }
                    break;
                case "Jeans":
                    if (jeansImagesList != null && jeansImagesList.size() > 0) {
                        int primaryKey = jeansImagesList.size() + 1;
                        JeansImages images = new JeansImages(primaryKey, toSaveBm);
                        imagesViewModel.insertJeansImage(images);
                    } else {
                        jeansImagesList = new ArrayList<>();
                        JeansImages images = new JeansImages(1, toSaveBm);
                        imagesViewModel.insertJeansImage(images);
                    }
                    break;

            }
        }

    }

    public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        return Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
    }

    @Override
    public void onJeansChanged(int position) {
        jeansPosition = position;
        String liked = shirtPosition + "," + jeansPosition;
        boolean isAlreadyLiked = likedItems.contains(liked);

        if (isAlreadyLiked) {
            mainActivityBinding.btnFavorite.setImageResource(R.drawable.ic_favorite_white_24dp);
        } else {
            mainActivityBinding.btnFavorite.setImageResource(R.drawable.ic_favorite_border_white_24dp);
        }
    }

    @Override
    public void onShirtChanged(int position) {
        shirtPosition = position;
        String liked = shirtPosition + "," + jeansPosition;
        boolean isAlreadyLiked = likedItems.contains(liked);

        if (isAlreadyLiked) {
            mainActivityBinding.btnFavorite.setImageResource(R.drawable.ic_favorite_white_24dp);
        } else {
            mainActivityBinding.btnFavorite.setImageResource(R.drawable.ic_favorite_border_white_24dp);
        }
    }
}
